package com.haulmont.thesis.crm.core.listener;

import com.haulmont.bali.util.Preconditions;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.listener.BeforeInsertEntityListener;
import com.haulmont.cuba.core.listener.BeforeUpdateEntityListener;
import com.haulmont.thesis.crm.entity.RoomLoadingCollision;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Set;

/**
 *  @author Kirill Khoroshilov, 2019
 */
@Component("crm_RoomLoadingCollisionEntityListener")
public class RoomLoadingCollisionEntityListener implements BeforeInsertEntityListener<RoomLoadingCollision>, BeforeUpdateEntityListener<RoomLoadingCollision> {

    @Inject
    protected Persistence persistence;

    @Override
    public void onBeforeInsert(RoomLoadingCollision entity) {
        onBeforeUpdate(entity);
    }

    @Override
    public void onBeforeUpdate(RoomLoadingCollision entity) {
        Set<String> fields = persistence.getTools().getDirtyFields(entity);

        if (CollectionUtils.containsAny(fields, Arrays.asList("room", "loadingInfo", "collisionInfo", "isCollision"))) {
            entity.setDescription(buildDescription(entity));
        }
    }

    protected String buildDescription(RoomLoadingCollision entity) {
        Preconditions.checkNotNullArgument(entity.getRoom(), "Room is not specified");
        Preconditions.checkNotNullArgument(entity.getLoadingInfo(), "LoadingInfo is not specified");
        Preconditions.checkNotNullArgument(entity.getLoadingInfo().getProject(), "Project in the loadingInfo is not specified");
        Preconditions.checkNotNullArgument(entity.getCollisionInfo(), "CollisionInfo is not specified");
        Preconditions.checkNotNullArgument(entity.getCollisionInfo().getProject(), "Project in the collisionInfo is not specified");

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

        return "В " + entity.getRoom().getInstanceName() + ". " +
                entity.getLoadingInfo().getProject().getName() + " (" +
                sdf.format(entity.getLoadingInfo().getInstallationDate()) + " - " +
                sdf.format(entity.getLoadingInfo().getDeinstallationDate()) + ")" + " / " +
                entity.getCollisionInfo().getProject().getName() + " (" +
                sdf.format(entity.getCollisionInfo().getInstallationDate()) + " - " +
                sdf.format(entity.getCollisionInfo().getDeinstallationDate()) + ")";
    }
}