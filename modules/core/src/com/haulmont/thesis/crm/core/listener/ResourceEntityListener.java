package com.haulmont.thesis.crm.core.listener;

import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.listener.BeforeInsertEntityListener;
import com.haulmont.cuba.core.listener.BeforeUpdateEntityListener;
import com.haulmont.thesis.crm.entity.Resource;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Set;

/**
 * @author Kirill Khoroshilov, 2018
 * Created on 28.11.2018.
 */
@Component("crm_ResourceEntityListener")
public class ResourceEntityListener implements BeforeInsertEntityListener<Resource>, BeforeUpdateEntityListener<Resource> {

    @Inject
    protected Persistence persistence;

    @Override
    public void onBeforeInsert(Resource entity) {
        Set<String> fields = persistence.getTools().getDirtyFields(entity);

        if (fields.contains("parent")) {
            if (entity.getParent() != null){
                entity.setRoot(entity.getParent().getRoot());
            } else {
                entity.setRoot(entity);
            }
        }
    }

    @Override
    public void onBeforeUpdate(Resource entity) {
        onBeforeInsert(entity);
    }
}