package com.haulmont.thesis.crm.parsers;

import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.haulmont.cuba.core.entity.AbstractNotPersistentEntity;

/**
 * @author Kirill Khoroshilov, 2018
 * Created on 27.11.2018.
 */

public class IgnoreInheritedIntrospector extends JacksonAnnotationIntrospector {
    public boolean hasIgnoreMarker(AnnotatedMember m) {
        return m.getDeclaringClass() == AbstractNotPersistentEntity.class || super.hasIgnoreMarker(m);
    }
}
