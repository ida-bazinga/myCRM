/*
 * Copyright (c) ${YEAR} ${PACKAGE_NAME}
 */

package com.haulmont.thesis.crm.core.app.projectRoom;

import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.CommitContext;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.LoadContext;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.thesis.crm.entity.*;
import com.haulmont.thesis.crm.enums.BookingEventDetailStatusEnum;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;



@ManagedBean(ICreateProjectRoomMBean.NAME)
public class CreateProjectRoom implements ICreateProjectRoomMBean {

    @Inject
    private DataManager dataManager;
    @Inject
    protected Metadata metadata;

    protected Set<Entity> toCommit = new HashSet<>();
    protected Set<Entity> toRemove = new HashSet<>();
    protected Log log = LogFactory.getLog(ProjectRoom.class);

    public boolean remove(UUID bookingEventId) {
        toCommit.clear();
        toRemove.clear();
        try {
            BookingEvent bookingEvent = getBookingEvent(bookingEventId);
            for (BookingEventDetail bookingEventDetail:bookingEvent.getBookingEventDetail()) {
                bookingEventDetail.setStatus(BookingEventDetailStatusEnum.REMOVED);
                toCommit.add(bookingEventDetail);
            }
            projectRoomToRemove(bookingEvent.getProject().getId());
            return doCommit();
        } catch (Exception e) {
            log.error(String.format("CreateProjectRoom.remove: %s", e.getMessage()));
            return false;
        }

    }

    public boolean create(UUID bookingEventId) {
        try {
            toCommit.clear();
            toRemove.clear();
            BookingEvent bookingEvent = getBookingEvent(bookingEventId);
            if (bookingEvent != null) {
                newProjectRoom(bookingEvent);
            }
            return doCommit();
        } catch (Exception e) {
            log.error(String.format("CreateProjectRoom.create: %s", e.getMessage()));
            return false;
        }
    }

    protected BookingEvent getBookingEvent(UUID Id) {
        LoadContext loadContext = new LoadContext(BookingEvent.class).setView("edit");
        loadContext.setQueryString("select e from crm$BookingEvent e where e.id = :Id").setParameter("Id", Id);
        return dataManager.load(loadContext);
    }

    protected void projectRoomToRemove(UUID projectId) {
        LoadContext loadContext = new LoadContext(ProjectRoom.class).setView("edit");
        loadContext.setQueryString("select e from crm$ProjectRoom e where e.project.id = :projectId").setParameter("projectId", projectId);
        List<ProjectRoom> projectRoomList = dataManager.loadList(loadContext);
        if (projectRoomList.size() > 0) {
            toRemove.addAll(projectRoomList);
        }
    }

    protected ExtProject getExtProject(UUID Id) {
        LoadContext loadContext = new LoadContext(ExtProject.class).setView("ext-edit");
        loadContext.setQueryString("select e from crm$Project e where e.id = :Id").setParameter("Id", Id);
        return dataManager.load(loadContext);
    }


    protected void newProjectRoom(BookingEvent bookingEvent) {
        ExtProject project = getExtProject(bookingEvent.getProject().getId());
        updateProject(bookingEvent, project);
        projectRoomToRemove(project.getId());
        for (BookingEventDetail bookingEventDetail : bookingEvent.getBookingEventDetail()) {
            if (!bookingEventDetail.getStatus().equals(BookingEventDetailStatusEnum.REMOVED)) {
                ProjectRoom projectRoom = metadata.create(ProjectRoom.class);
                projectRoom.setProject(project);
                projectRoom.setStatus(ProjectRoomStatusEnum.approved);
                projectRoom.setRoom(bookingEventDetail.getRoom());
                projectRoom.setArea(bookingEventDetail.getArea());
                projectRoom.setInstallationDate(bookingEventDetail.getInstallationDate());
                projectRoom.setStartDate(bookingEventDetail.getStartDate());
                projectRoom.setEndDate(bookingEventDetail.getEndDate());
                projectRoom.setDeinstallationDate(bookingEventDetail.getDeinstallationDate());
                projectRoom.setOptionDate(bookingEventDetail.getOptionDate());
                projectRoom.setVariant(ProjectRoomVariantEnum.variant1);
                toCommit.add(projectRoom);
            }
        }
    }

    protected void updateProject(BookingEvent bookingEvent, ExtProject project) {
        boolean isEdit = false;
        if(bookingEvent.getName() != null) {
            project.setName(bookingEvent.getName());
            isEdit = true;
        }
        if(bookingEvent.getFullName_ru() != null) {
            project.setFullName_ru(bookingEvent.getFullName_ru());
            isEdit = true;
        }
        if(bookingEvent.getFullName_en() != null) {
            project.setFullName_en(bookingEvent.getFullName_en());
            isEdit = true;
        }
        if(bookingEvent.getCompany() != null) {
            project.setOrganizer(bookingEvent.getCompany());
            isEdit = true;
        }
        if(bookingEvent.getThemes() != null) {
            project.setThemes(bookingEvent.getThemes());
            isEdit = true;
        }
        if(bookingEvent.getExhibitSpace() != null) {
            project.setExhibitSpace(bookingEvent.getExhibitSpace());
            isEdit = true;
        }
        if(bookingEvent.getInstallationDate() != null) {
            project.setInstallationDateFact(bookingEvent.getInstallationDate());
            isEdit = true;
        }
        if(bookingEvent.getDateStart() != null) {
            project.setDateStartFact(bookingEvent.getDateStart());
            isEdit = true;
        }
        if(bookingEvent.getDateFinish() != null) {
            project.setDateFinishFact(bookingEvent.getDateFinish());
            isEdit = true;
        }
        if(bookingEvent.getDeinstallationDate() != null) {
            project.setDeinstallationDateFact(bookingEvent.getDeinstallationDate());
            isEdit = true;
        }

        if (isEdit) {
            toCommit.add(project);
        }
    }

    private boolean doCommit() {
        try{
            if (toCommit.size() > 0 || toRemove.size() > 0) {
                dataManager.commit(new CommitContext(toCommit, toRemove));
            }
            return true;
        }
        catch (Exception e) {
            log.error(String.format("CreateProjectRoom doCommit(): %s", e.getMessage()));
            return false;
        }
    }


}
