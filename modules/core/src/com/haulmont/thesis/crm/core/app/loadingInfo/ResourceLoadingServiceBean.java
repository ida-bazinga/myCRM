package com.haulmont.thesis.crm.core.app.loadingInfo;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.cuba.core.global.UuidProvider;
import com.haulmont.thesis.crm.entity.*;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author Kirill Khoroshilov, 2018
 * Created on 27.11.2018.
 */
@Service(ResourceLoadingService.NAME)
public class ResourceLoadingServiceBean implements ResourceLoadingService {

    private Log log = LogFactory.getLog(getClass());

    @Inject
    protected Metadata metadata;
    @Inject
    protected TimeSource timeSource;
    @Inject
    protected ResourceLoadingWorker resourceLoadingWorker;

    @Nonnull
    @Override
    public Collection<Map<String, Object>> getRoomLoadingCollisions(Date startDate, Date endDate, Collection<Room> rooms){
        List<Map<String, Object>> result = new ArrayList<>();

        Collection<RoomLoadingCollision> collisions = resourceLoadingWorker.getCollisionInfo(startDate, endDate, rooms);

        for(RoomLoadingCollision collision : collisions){
            Map<String, Object> collisionMap = new HashMap<>();
            collisionMap.put("id", collision.getId());
            collisionMap.put("row_id", collision.getRoom().getId());
            collisionMap.put("value_id", collision.getLoadingInfo().getId());
            collisionMap.put("collision_id", collision.getCollisionInfo().getId());

            result.add(collisionMap);
        }

        return result;
    }

    @Nonnull
    @Override
    public Collection<Map<String, Object>> getRoomLoadingInfo(Date startDate, Date endDate, Collection<Room> rooms, Boolean isShowOptions){
        List<Map<String, Object>> result = new ArrayList<>();

        isShowOptions = BooleanUtils.isTrue(isShowOptions);

        Collection<IRoomResourceLoadingsInfo> roomLoadingsInfo = resourceLoadingWorker.getRoomLoadingsInfo(startDate, endDate, rooms, isShowOptions);

        for (IRoomResourceLoadingsInfo item : roomLoadingsInfo){
            Map<String, Object> newLine = createLine(item);
            result.add(newLine);
        }

        result.addAll(createPhantoms(roomLoadingsInfo, rooms));

        return result;
    }

    protected Collection<Map<String, Object>> createPhantoms(Collection<IRoomResourceLoadingsInfo> roomLoadingsInfo, Collection<Room> rooms){
        Collection<Map<String, Object> > result = new ArrayList<>();

        Collection<Phantom> phantoms = internalCreatePhantoms(roomLoadingsInfo, rooms);
        for(Phantom phantom : phantoms){
            result.add(createLine(phantom));
        }
        return result;
    }

    protected Collection<Phantom> internalCreatePhantoms(Collection<IRoomResourceLoadingsInfo> roomLoadingsInfo, Collection<Room> rooms){
        Collection<Phantom> result = new TreeSet<>(getPhantomComparator());

        //todo not tested!!!
        rooms = resourceLoadingWorker.getRoomWithDependencies(rooms);

        for (Room room : rooms) {
            Iterable<RoomException> conflictRooms = Iterables.filter(room.getConflictRooms(), new Predicate<RoomException>() {
                @Override
                public boolean apply(RoomException input) {
                    return input.getRoomConflicting().getUseLoadingInfo();
                }
            });

            if (conflictRooms.iterator().hasNext()) {
                Iterable<IRoomResourceLoadingsInfo> roomRows = Iterables.filter(roomLoadingsInfo, getFilterByRoomPredicate(room.getId()));

                for (RoomException conflictRoom: conflictRooms){
                    for(IRoomResourceLoadingsInfo row : roomRows) {
                        Phantom phantom = new Phantom(conflictRoom.getRoomConflicting(), row);
                        result.add(phantom);
                    }
                }
            }
        }

        return mergePhantoms(result);
    }

    protected Collection<Phantom> mergePhantoms(Collection<Phantom> phantoms){
        Collection<Phantom> result = new TreeSet<>(getPhantomComparator());

        for(Phantom phantom : phantoms){
            Phantom intersection = Iterables.find(result, getIntersectionPredicate(phantom), null);
            if (intersection != null) {
                if (phantom.getDeinstallationDate().after(intersection.getDeinstallationDate())) {
                    intersection.setDeinstallationDate(phantom.getDeinstallationDate());
                }
                intersection.addEntityIds(phantom);
            } else {
                result.add(phantom);
            }
        }

        return result;
    }

    protected Comparator<Phantom> getPhantomComparator(){
        return new Comparator<Phantom>() {
            @Override
            public int compare(Phantom o1, Phantom o2) {

                int sort = o1.getRoom().getUuid().compareTo(o2.getRoom().getUuid());

                if (sort == 0) {
                    return o1.getInstallationDate().compareTo(o2.getInstallationDate());
                }
                return sort;
            }
        };
    }

    protected Predicate<IRoomResourceLoadingsInfo> getFilterByRoomPredicate(final UUID itemId) {
         return new Predicate<IRoomResourceLoadingsInfo>() {
            @Override
            public boolean apply(IRoomResourceLoadingsInfo input) {
                return input.getRoom().getId().equals(itemId) && !input.isPhantom();
            }
        };
    }

    protected Predicate<IRoomResourceLoadingsInfo> getIntersectionPredicate(final IRoomResourceLoadingsInfo item) {
        return new Predicate<IRoomResourceLoadingsInfo>() {
            @Override
            public boolean apply(IRoomResourceLoadingsInfo input) {

                Calendar cal = Calendar.getInstance();
                cal.setTime(input.getDeinstallationDate());

                cal.add(Calendar.DATE, 2);
                Date d4 = cal.getTime();

                return item.getRoom().equals(input.getRoom()) && item.getInstallationDate().before(d4);
            }
        };
    }

    //todo 2019.05.10 Colors to settings
    protected Map<String, Object> createLine(IRoomResourceLoadingsInfo item) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", UuidProvider.createUuid());
        result.put("row_id", item.getRoom().getId());
        result.put("event", item.getProject());
        result.put("isPhantom", item.isPhantom());
        result.put("d1", item.getInstallationDate());
        result.put("d2", item.getStartDate());
        result.put("d3", item.getEndDate());
        result.put("d4", item.getDeinstallationDate());
        result.put("optionDate", item.getOptionDate());
        result.put("mainColor", "royalBlue");
        result.put("addColor", "gainsboro");
        result.put("entity_id", item.getUuid());
        result.put("isEarlyInstallation", item.isEarlyInstallation());
        result.put("isLateDeinstallation", item.isLateDeinstallation());


        if (item instanceof ProjectRoom) {
            result.put("metaClassName", metadata.getClassNN(ExtProject.class).getName());
            result.put("edit_entity_id", item.getProject().getId());
        }

        if (item instanceof BookingEventDetail){
            result.put("metaClassName", metadata.getClassNN(BookingEvent.class).getName());
            result.put("edit_entity_id", ((BookingEventDetail)item).getBookingEvent().getId());

            String CARD_STATE = ",Endorsement,";
            String mainColor = ((BookingEventDetail) item).getBookingEvent().getState().equals(CARD_STATE) ?  "gold" : "red" ;
            String addColor = ((BookingEventDetail) item).getBookingEvent().getState().equals(CARD_STATE) ? "lemonchiffon" :"pink" ;

            result.put("mainColor", mainColor);
            result.put("addColor", addColor);
        }

        if (item instanceof Phantom) {
            result.put("entity_id", ((Phantom) item).getRealEntityIds());
            result.put("mainColor", "white");
            result.put("addColor", "white");
        }

        return result;
    }

    protected class Phantom implements Serializable, IRoomResourceLoadingsInfo {
        private static final long serialVersionUID = -2030007420509829471L;

        private UUID id;
        private Room room;
        private BigDecimal area;
        private Date installationDate;
        private Date deinstallationDate;
        private ExtProject project;

        private Set<UUID> entityIds = new HashSet<>();

        @Override
        public Date getCreateTs() {
            return timeSource.currentTimestamp();
        }

        @Override
        public UUID getUuid() {
            return id;
        }

        @Override
        public Room getRoom() {
            return room;
        }

        @Override
        public BigDecimal getArea() {
            return area;
        }

        @Override
        public Date getInstallationDate() {
            return installationDate;
        }

        @Override
        public Date getDeinstallationDate() {
            return deinstallationDate;
        }

        public void setDeinstallationDate(Date value) {
            this.deinstallationDate = value;
        }

        @Override
        public Date getStartDate() {
            return installationDate;
        }

        @Override
        public Date getEndDate() {
            return deinstallationDate;
        }

        @Override
        public ExtProject getProject() {
            return project;
        }

        @Override
        public Boolean isOption() {
            return false;
        }

        @Override
        public Date getOptionDate() {
            return null;
        }

        @Override
        public Boolean isPhantom() {
            return true;
        }

        @Override
        public Boolean isEarlyInstallation(){
            return false;
        }

        @Override
        public Boolean isLateDeinstallation(){
            return false;
        }

        public Set<UUID> getRealEntityIds() {
            return entityIds;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Phantom that = (Phantom) o;

            return !(getUuid() != null ? !getUuid().equals(that.getUuid()) : that.getUuid() != null);
        }

        @Override
        public int hashCode() {
            return getUuid() != null ? getUuid().hashCode() : 0;
        }

        public Phantom(){
            this.id = UuidProvider.createUuid();
        }

        public Phantom (Room room, IRoomResourceLoadingsInfo info) {
            this();
            this.room = room;
            this.area = info.getArea();
            this.project = info.getProject();
            this.installationDate = info.getInstallationDate();
            this.deinstallationDate = info.getDeinstallationDate();
            addEntityIds(info);
        }

        public Phantom addEntityIds(IRoomResourceLoadingsInfo info) {
            if (info instanceof Phantom) {
                this.entityIds.addAll(((Phantom) info).getRealEntityIds());
            } else {
                this.entityIds.add(info.getUuid());
            }

            return this;
        }
    }
}