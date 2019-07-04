package com.haulmont.thesis.crm.core.app.loadingInfo;

import com.google.common.collect.Lists;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.thesis.crm.entity.*;
import com.haulmont.thesis.crm.enums.BookingEventDetailStatusEnum;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import java.util.*;

/**
 * @author Kirill Khoroshilov, 2019
 */

@Component(ResourceLoadingWorker.NAME)
public class ResourceLoadingWorkerImpl implements ResourceLoadingWorker {

    private static final String PROC_CODE = "Loading_04102016";
    private static final String VIEW_NAME = "for-loading-info";

    @Inject
    protected DataManager dataManager;

    @Nullable
    @Override
    public Room getRoomWithDependencies(Room room){
        Collection<Room> rooms = getRoomWithDependencies(Lists.newArrayList(room));
        return rooms.isEmpty() ? null : rooms.iterator().next();
    }

    @Nonnull
    @Override
    public Collection<Room> getRoomWithDependencies(Collection<Room> rooms){
        LoadContext ctx = new LoadContext(Room.class)
                .setView("with-conflict-room");

        ctx.setQueryString("select e from crm$Room e where e.id in :rooms and e.useLoadingInfo = true")
                .setParameter("rooms", rooms);

        return dataManager.loadList(ctx);
    }

    @Nonnull
    @Override
    public Collection<RoomLoadingCollision> getCollisionInfo(Date startDate, Date endDate, Collection<Room> rooms){
        Map<String, Object> params = buildParams(startDate, endDate, rooms);

        LoadContext ctx = new LoadContext(RoomLoadingCollision.class)
                .setView(VIEW_NAME);
        ctx.setQueryString("select e from crm$RoomLoadingCollision e join e.loadingInfo li join e.collisionInfo ci " +
                "where e.isCollision = true and e.room.id in :rooms and li.deinstallationDate >= :sDate and li.installationDate <= :eDate " +
                "and  ci.deinstallationDate >= :sDate and ci.installationDate <= :eDate " +
                "order by e.room.code")
                .setParameters(params);

        return dataManager.loadList(ctx);
    }

    @Nonnull
    @Override
    public Collection<IRoomResourceLoadingsInfo> getRoomLoadingsInfo(Date startDate, Date endDate, Collection<Room> rooms) {
        return getRoomLoadingsInfo(startDate, endDate, rooms, false);
    }

    @Nonnull
    @Override
    public Collection<IRoomResourceLoadingsInfo> getRoomLoadingsInfo(Date startDate, Date endDate, Collection<Room> rooms, Boolean isShowOptions){
        Map<String, Object> params = buildParams(startDate, endDate, rooms);

        Collection<IRoomResourceLoadingsInfo> items = new TreeSet<>(getLoadingsInfoComparator());
        items.addAll(getProjectRoomsInfo(params));

        params.put("isShowOptions", isShowOptions);
        items.addAll(getBookingEventInfo(params));

        return items;
    }

    protected Comparator<IRoomResourceLoadingsInfo> getLoadingsInfoComparator(){
        return new Comparator<IRoomResourceLoadingsInfo>() {
            @Override
            public int compare(IRoomResourceLoadingsInfo o1, IRoomResourceLoadingsInfo o2) {
                int sort;

                if (o1.getRoom().getCode() == null && o2.getRoom().getCode() == null){
                    sort = 0;
                } else if (o1.getRoom().getCode() == null){
                    sort = 1;
                } else if(o2.getRoom().getCode() == null){
                    sort = -1;
                } else {
                    sort = o1.getRoom().getCode().compareTo(o2.getRoom().getCode());
                }

                if (sort == 0) {
                    if (o1.getInstallationDate() == null) {
                        sort = 1;
                    } else if (o2.getInstallationDate() == null) {
                        sort = -1;
                    } else {
                        sort = o1.getInstallationDate().compareTo(o2.getInstallationDate());
                    }
                }

                if (sort == 0) {
                    if (o1.getProject().getCode() == null) {
                        sort = 1;
                    } else if (o2.getProject().getCode() == null) {
                        sort = -1;
                    } else {
                        sort = o1.getProject().getCode().compareTo(o2.getProject().getCode());
                    }
                }

                if (sort == 0) {
                    sort = o1.getCreateTs().compareTo(o2.getCreateTs());
                }

                return sort;
            }
        };
    }

    protected List<ProjectRoom> getProjectRoomsInfo(Map<String, Object> params){
        LoadContext ctx = new LoadContext(ProjectRoom.class)
                .setView(VIEW_NAME);
        ctx.setQueryString("select e from crm$ProjectRoom e " +
                "where e.deinstallationDate >= :sDate and e.installationDate <= :eDate and e.room.id in :rooms")
                .setParameters(params);

        return dataManager.loadList(ctx);
    }

    protected List<BookingEventDetail> getBookingEventInfo(Map<String, Object> params){
        params.put("pCode", PROC_CODE);
        params.put("states", Lists.newArrayList(BookingEventDetailStatusEnum.NEW.getId(), BookingEventDetailStatusEnum.EDITED.getId()));
        params.put("cardStates", Lists.newArrayList(",Endorsement,", ",Improvement,"));

        LoadContext ctx = new LoadContext(BookingEventDetail.class)
                .setView(VIEW_NAME);
        ctx.setQueryString("select e from crm$BookingEventDetail e join e.bookingEvent.procs p " +
                "where e.deinstallationDate >= :sDate and e.installationDate <= :eDate" +
                " and e.room.id in :rooms and p.proc.code = :pCode and p.active = true and e.status in :states " +
                "and (e.bookingEvent.endorsed = false or e.bookingEvent.endorsed is null) " +
                "and (e.bookingEvent.state is not null and e.bookingEvent.state not in :cardStates or true = :isShowOptions)" )
                .setParameters(params);

        return dataManager.loadList(ctx);
    }

    protected Map<String, Object> buildParams(Date startDate, Date endDate, Collection<Room> rooms){
        Map<String, Object> result = new HashMap<>();
        result.put("sDate", startDate);
        result.put("eDate", endDate);
        result.put("rooms", rooms);

        return result;
    }
}
