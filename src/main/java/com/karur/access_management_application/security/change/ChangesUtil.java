package com.karur.access_management_application.security.change;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;
import java.util.*;

public class ChangesUtil {

    public List<Change> compare(Object newObject, Object oldObject) {
        List<Change> changes = new ArrayList<>();
        compare(oldObject, newObject, changes);
    }

    private void compare(Object oldObject, Object newObject, List<Change> changes) {
        if (Objects.isNull(oldObject) && Objects.nonNull(newObject)) {
            updateWithRemovedOrAddedObject(oldObject, newObject, changes);
        }
        if (Objects.nonNull(oldObject) && Objects.isNull(newObject)) {
            updateWithRemovedOrAddedObject(oldObject, newObject, changes);
        }
        if (Objects.nonNull(oldObject)) {
            //both are non-null
            if (oldObject.getClass().isPrimitive()) {
                comparePrimitive(oldObject, newObject, changes);
            } else if (oldObject instanceof Collection<?>) {
                compareCollection(oldObject, newObject, changes);
            }
            else {
                compareObjects(oldObject,newObject,changes);
            }
        }
    }

    private void compareObjects(Object oldObject, Object newObject, List<Change> changes) {
        Arrays.stream(oldObject.getClass().getDeclaredFields()).forEach(field -> {
            try {
                Field declaredField = newObject.getClass().getDeclaredField(field.getName());
                compare(field.get(oldObject),declaredField.get(newObject),changes);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void compareCollection(Object oldObject, Object newObject, List<Change> changes) {
        Map<String, OldAndNewObjectPair> compareObjectsMap = new HashMap<>();
        compareCollection(oldObject, compareObjectsMap, true);
        compareCollection(newObject, compareObjectsMap, false);
        compareObjectsMap.forEach((s, oldAndNewObjectPair) -> compare(oldAndNewObjectPair.getOldObject(), oldAndNewObjectPair.getNewObject(), changes));
    }

    private static void compareCollection(Object object, Map<String, OldAndNewObjectPair> compareObjectsMap, boolean old) {
        if (object instanceof Iterable<?> objects) {
            objects.forEach(obj -> {
                String key = Arrays.stream(obj.getClass().getDeclaredFields()).filter(field -> field.isAnnotationPresent(ChangeId.class))
                        .map(field -> getFieldValue(field, obj))
                        .reduce((value1, value2) -> (value1 + "_" + value2)).get();
                if (Objects.isNull(compareObjectsMap.get(key))) {
                    compareObjectsMap.put(key, new OldAndNewObjectPair());
                }
                if (old) {
                    compareObjectsMap.get(key).setOldObject(object);
                } else {
                    compareObjectsMap.get(key).setNewObject(object);
                }
            });
        }
    }

    private static String getFieldValue(Field field, Object object) {
        field.setAccessible(true);
        Object object1 = field.get(object);
        if (Objects.isNull(object1.toString())) {
            return "";
        }
        return object1.toString();
    }

    private void comparePrimitive(Object oldObject, Object newObject, List<Change> changes) {
        if (!oldObject.equals(newObject)) {
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

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class OldAndNewObjectPair {
        private Object oldObject;
        private Object newObject;
    }
}
