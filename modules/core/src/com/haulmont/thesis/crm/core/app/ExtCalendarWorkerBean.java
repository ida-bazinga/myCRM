/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app;

import com.haulmont.cuba.core.EntityManager;
import com.haulmont.cuba.core.Transaction;
import com.haulmont.cuba.core.TypedQuery;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.thesis.core.app.CalendarWorkerBean;
import com.haulmont.thesis.core.entity.CalendarEventItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExtCalendarWorkerBean extends CalendarWorkerBean {

    @Override
    public List<CalendarEventItem> loadCalendarEvents(Date startDate, Date endDate, User user, String view) {
        Transaction tx = persistence.getTransaction();
        try {
            EntityManager em = persistence.getEntityManager();
            TypedQuery<Object[]> query = em.createQuery("select a " +
                    "from ts$CalendarEventItem a where " +
                    "exists (select p from a.participants p where p.user.id=:user) " +
                    "and a.startTime is not null " +
                    "and a.card.deleteTs is null order by a.startTime desc", Object[].class);
            query.setView(CalendarEventItem.class, view);
            query //.setParameter("startDate", startDate)
                    //.setParameter("endDate", endDate)
                    .setParameter("user", user);

            List<Object[]> results = query.getResultList();
            List<CalendarEventItem> calendarEventItems = new ArrayList<>(results.size());

            for (Object[] result : results) {
                CalendarEventItem eventItem = (CalendarEventItem) result[0];
                initCalendarEventDescription(eventItem);
                calendarEventItems.add((CalendarEventItem) result[0]);
            }

            return calendarEventItems;

        } finally {
            tx.end();
        }
    }

}
