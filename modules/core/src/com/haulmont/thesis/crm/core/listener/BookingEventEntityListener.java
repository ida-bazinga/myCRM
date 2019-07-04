package com.haulmont.thesis.crm.core.listener;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.Query;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.listener.BeforeInsertEntityListener;
import com.haulmont.cuba.core.listener.BeforeUpdateEntityListener;
import com.haulmont.thesis.crm.entity.BookingEvent;
import com.haulmont.thesis.crm.entity.BookingEventDetail;
import com.haulmont.thesis.crm.entity.ExtProject;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Set;

/**
 * @author kirill
 */
@Component("crm_BookingEventEntityListener")
public class BookingEventEntityListener implements BeforeInsertEntityListener<BookingEvent>, BeforeUpdateEntityListener<BookingEvent> {

    protected Log log = LogFactory.getLog(getClass());

    @Inject
    protected Persistence persistence;

    @Override
    public void onBeforeInsert(BookingEvent entity) {
        onBeforeUpdate(entity);
    }

    @Override
    public void onBeforeUpdate(BookingEvent entity) {
        Set<String> fields = persistence.getTools().getDirtyFields(entity);
        EntityManager em = persistence.getEntityManager();

        if (fields.contains("project")) {
        Set<BookingEventDetail> details = entity.getBookingEventDetail();
        if (details == null) return;

        for (BookingEventDetail detail : details){
            detail.setProject((ExtProject)entity.getProject());
            em.persist(detail);
        }

//            Transaction tx = persistence.getTransaction();
//            try {
//                String updatQuery = "update crm$BookingEventDetail e set e.project= :project where e.bookingEvent.id = :id";
//                Query updateQuery = em.createQuery(updatQuery, BookingEventDetail.class)
//                        .setParameter("project", entity.getProject())
//                        .setParameter("id", entity.getId());
//                updateQuery.executeUpdate();
//                tx.commit();
//            } catch (Exception e) {
//                log.error(ExceptionUtils.getStackTrace(e));
//            } finally {
//                tx.end();
//            }
        }
    }
}