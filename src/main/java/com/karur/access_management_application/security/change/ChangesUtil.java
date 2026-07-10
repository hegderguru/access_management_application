package com.karur.access_management_application.security.change;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class ChangesUtil {

    public List<Change> compare(Object newObject, Object oldObject){
        List<Change> changes = new ArrayList<>();
        compare(oldObject,newObject ,changes);
    }

    private void compare(Object oldObject, Object newObject, List<Change> changes) {
        if(Objects.isNull(oldObject) && Objects.nonNull(newObject)){
            updateWithRemovedOrAddedObject(oldObject,newObject,changes);
        }
        if(Objects.nonNull(oldObject) && Objects.isNull(newObject)){
            updateWithRemovedOrAddedObject(oldObject,newObject,changes);
        }
        if(Objects.nonNull(oldObject)){
            //both are non-null
            if(oldObject.getClass().isPrimitive()){
                comparePrimitive(oldObject,newObject,changes);
            }else if(oldObject instanceof Collection<?>){
                compareCollection(oldObject,newObject,changes);
            }
        }
    }

    private void compareCollection(Object oldObject, Object newObject, List<Change> changes) {

    }

    private void comparePrimitive(Object oldObject, Object newObject, List<Change> changes) {
        if(!oldObject.equals(newObject)){
            Change.builder()
                    .index(0)
                    .oldValue(oldObject)
                    .newValue(newObject)
                    .build();
        }
    }

    private void updateWithRemovedOrAddedObject(Object oldObject, Object newObject, List<Change> changes) {
        changes.add(Change.builder()
                        .index(0)
                        .Clazz(newObject.getClass())
                        .oldValue(oldObject)
                        .newObject(newObject)
                .build());
    }

    @Data
    @Builder
    private class Change {
        Integer index;
        Class Clazz;
        String fieldName;
        Object oldValue;
        Object newValue;
        Object oldObject;
        Object newObject;
    }
}
