package com.haulmont.thesis.crm.core.listener;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.View;
import com.haulmont.cuba.core.listener.BeforeDeleteEntityListener;
import com.haulmont.cuba.core.listener.BeforeInsertEntityListener;
import com.haulmont.cuba.core.listener.BeforeUpdateEntityListener;
import com.haulmont.thesis.crm.core.app.loadingInfo.ResourceLoadingWorker;
import com.haulmont.thesis.crm.entity.*;
import com.haulmont.thesis.crm.enums.BookingEventDetailStatusEnum;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.*;

/**
 *  @author Kirill Khoroshilov, 2019
 */
@Component("crm_RoomLoadingEntityListener")
public class RoomLoadingEntityListener implements BeforeDeleteEntityListener<RoomResourceLoadingsInfo>, BeforeInsertEntityListener<RoomResourceLoadingsInfo>, BeforeUpdateEntityListener<RoomResourceLoadingsInfo> {

    private Log log = LogFactory.getLog(getClass());

    @Inject
    protected Persistence persistence;
    @Inject
    protected Metadata metadata;
    @Inject
    protected DataManager dataManager;
    @Inject
    protected ResourceLoadingWorker resourceLoadingWorker;

    @Override
    public void onBeforeDelete(RoomResourceLoadingsInfo entity) {
        Transaction tx = persistence.createTransaction();
        try {
            removeEntries(entity);
            tx.commit();
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        } finally {
            tx.end();
        }
    }

    @Override
    public void onBeforeInsert(RoomResourceLoadingsInfo entity) {
        createEntries(entity);
    }

    @Override
    public void onBeforeUpdate(RoomResourceLoadingsInfo entity) {
        Set<String> fields = persistence.getTools().getDirtyFields(entity);

        List<String> fieldNames = Arrays.asList("room", "startDate", "installationDate", "deinstallationDate", "endDate", "status");

        if (entity instanceof BookingEventDetail){
            BookingEventDetail item = (BookingEventDetail)entity;
            if (item.getStatus().equals(BookingEventDetailStatusEnum.REMOVED)){
                removeEntries(entity);
                return;
            }

            if ((item.getStatus().equals(BookingEventDetailStatusEnum.APPROVED))){
                return;
            }
        }

        if (CollectionUtils.containsAny(fields, fieldNames)) {
            Transaction tx = persistence.createTransaction();
            try {
                removeEntries(entity);
                createEntries(entity);
                tx.commit();
            } catch (Exception e) {
                log.error(ExceptionUtils.getStackTrace(e));
            } finally {
                tx.end();
            }
        }
    }

    protected void createEntries(RoomResourceLoadingsInfo entity){
        Room room = resourceLoadingWorker.getRoomWithDependencies(entity.getRoom());

        if (room == null) return;

        Set<Room> rooms = new HashSet<>();
        rooms.add(room);
        for (RoomException re : room.getConflictRooms()){
            if (re.getRoomConflicting().getUseLoadingInfo()){
                rooms.add(re.getRoomConflicting());
            }
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(entity.getDeinstallationDate());

        Collection<IRoomResourceLoadingsInfo> loadingInfo = resourceLoadingWorker.getRoomLoadingsInfo(
                entity.getInstallationDate(),
                cal.getTime(),
                rooms
        );

        EntityManager em = persistence.getEntityManager();
        RoomResourceLoadingsInfo li = em.reload(entity, View.MINIMAL);

        for (IRoomResourceLoadingsInfo info : loadingInfo){
            if (entity.getId().equals(info.getUuid())) continue;

            RoomLoadingCollision collision = metadata.create(RoomLoadingCollision.class);
            collision.setRoom(info.getRoom());
            if (entity.getInstallationDate().after(info.getInstallationDate())
                    || (entity.getInstallationDate() == info.getInstallationDate() && entity.getCreateTs().after(info.getCreateTs()))){
                collision.setLoadingInfo(li);
                collision.setCollisionInfo((RoomResourceLoadingsInfo)info);
            } else {
                collision.setLoadingInfo((RoomResourceLoadingsInfo)info);
                collision.setCollisionInfo(li);
            }

            collision.setIsCollision(true);
            em.persist(collision);
        }
    }

    protected void removeEntries(RoomResourceLoadingsInfo entity){
        EntityManager em = persistence.getEntityManager();

        String deleteQuery = "delete from crm$RoomLoadingCollision e where e.loadingInfo.id = :id or e.collisionInfo.id = :id";
        Query updateQuery = em.createQuery(deleteQuery)
                .setParameter("id", entity.getId());
        updateQuery.executeUpdate();
    }
}