package com.haulmont.thesis.crm.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

/**
 * @author Kirill Khoroshilov, 2019
 */

/**
 ** Interface to combine information of rooms loading for projects
 */
public interface IRoomResourceLoadingsInfo {

    Date getCreateTs();

    UUID getUuid();

//    void setRoom(Room room);
    Room getRoom();

//    void setArea(BigDecimal area);
    BigDecimal getArea();

//    void setInstallationDate(Date installationDate);
    Date getInstallationDate();

//    void setDeinstallationDate(Date deinstallationDate);
    Date getDeinstallationDate();

//    void setStartDate(Date startDate);
    Date getStartDate();

//    void setEndDate(Date endDate);
    Date getEndDate();

    ExtProject getProject();

    Boolean isOption();
    Date getOptionDate();

    Boolean isPhantom();

    Boolean isEarlyInstallation();
    Boolean isLateDeinstallation();

}
