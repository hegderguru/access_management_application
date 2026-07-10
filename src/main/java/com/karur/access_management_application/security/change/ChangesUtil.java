package com.karur.access_management_application.security.change;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class ChangesUtil {

    public List<Change> compare(Object oldObject, Object newObject) {
        List<Change> changes = new ArrayList<>();
        compare(oldObject, newObject, "", changes);
        return changes;
    }

    private void compare(Object oldObject, Object newObject, String path, List<Change> changes) {
        // 1. Both null -> No change
        if (oldObject == null && newObject == null) {
            return;
        }

        // 2. One is null -> Addition or Deletion
        if (oldObject == null || newObject == null) {
            updateWithRemovedOrAddedObject(oldObject, newObject, path, changes);
            return;
        }

        // 3. Prevent class mismatch crashes
        if (!oldObject.getClass().equals(newObject.getClass())) {
            updateWithRemovedOrAddedObject(oldObject, newObject, path, changes);
            return;
        }

        Class<?> currentClass = oldObject.getClass();

        // 4. Branch based on type characteristics
        if (isPrimitiveOrWrapperOrString(currentClass) || currentClass.isEnum()) {
            comparePrimitive(oldObject, newObject, path, changes);
        } else if (oldObject instanceof Collection<?>) {
            compareCollection((Collection<?>) oldObject, (Collection<?>) newObject, path, changes);
        } else if (oldObject.getClass().isArray()) {
            compareCollection(convertArrayToCollection(oldObject), convertArrayToCollection(newObject), path, changes);
        } else {
            compareObjects(oldObject, newObject, path, changes);
        }
    }

    private void compareObjects(Object oldObject, Object newObject, String basePath, List<Change> changes) {
        Class<?> currentClass = oldObject.getClass();

        while (currentClass != null && currentClass != Object.class) {
            for (Field field : currentClass.getDeclaredFields()) {
                // Filter out compiler-synthetic fields, static constants, and serialization IDs
                if (field.isSynthetic() || Modifier.isStatic(field.getModifiers())) {
                    continue;
                }

                try {
                    field.setAccessible(true);
                    Object oldVal = field.get(oldObject);
                    Object newVal = field.get(newObject);

                    String fieldPath = basePath.isEmpty() ? field.getName() : basePath + "." + field.getName();
                    compare(oldVal, newVal, fieldPath, changes);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to access field processing path: " + basePath + "." + field.getName(), e);
                }
            }
            currentClass = currentClass.getSuperclass();
        }
    }

    private void compareCollection(Collection<?> oldCol, Collection<?> newCol, String basePath, List<Change> changes) {
        Map<String, OldAndNewObjectPair> compareObjectsMap = new LinkedHashMap<>();
        mapCollectionElements(oldCol, compareObjectsMap, true);
        mapCollectionElements(newCol, compareObjectsMap, false);

        compareObjectsMap.forEach((key, pair) -> {
            String elementPath = basePath + "[" + key + "]";
            compare(pair.getOldObject(), pair.getNewObject(), elementPath, changes);
        });
    }

    private static void mapCollectionElements(Collection<?> collection, Map<String, OldAndNewObjectPair> compareObjectsMap, boolean isOld) {
        if (collection == null) return;

        int index = 0;
        for (Object obj : collection) {
            if (obj == null) {
                index++;
                continue;
            }

            String key = extractIdentityKey(obj, index);
            compareObjectsMap.putIfAbsent(key, new OldAndNewObjectPair());

            if (isOld) {
                compareObjectsMap.get(key).setOldObject(obj);
            } else {
                compareObjectsMap.get(key).setNewObject(obj);
            }
            index++;
        }
    }

    private static String extractIdentityKey(Object obj, int index) {
        Class<?> currentClass = obj.getClass();
        List<String> identityValues = new ArrayList<>();

        while (currentClass != null && currentClass != Object.class) {
            for (Field field : currentClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(ChangeId.class)) {
                    identityValues.add(getFieldValue(field, obj));
                }
            }
            currentClass = currentClass.getSuperclass();
        }

        if (!identityValues.isEmpty()) {
            return String.join("_", identityValues);
        }

        // Fallback safely to object tracking index if identity annotations don't exist
        return "idx_" + index;
    }

    private static String getFieldValue(Field field, Object object) {
        try {
            field.setAccessible(true);
            Object val = field.get(object);
            return Objects.nonNull(val) ? val.toString() : "";
        } catch (IllegalAccessException e) {
            return "";
        }
    }

    private void comparePrimitive(Object oldObject, Object newObject, String path, List<Change> changes) {
        if (!Objects.equals(oldObject, newObject)) {
            changes.add(Change.builder()
                    .fieldName(path)
                    .oldValue(oldObject)
                    .newValue(newObject)
                    .build());
        }
    }

    private void updateWithRemovedOrAddedObject(Object oldObject, Object newObject, String path, List<Change> changes) {
        Object activeObj = (newObject != null) ? newObject : oldObject;
        changes.add(Change.builder()
                .clazz(activeObj != null ? activeObj.getClass() : null)
                .fieldName(path)
                .oldValue(oldObject)
                .newValue(newObject)
                .build());
    }

    private boolean isPrimitiveOrWrapperOrString(Class<?> type) {
        return type.isPrimitive() ||
                type == Double.class || type == Float.class || type == Long.class ||
                type == Integer.class || type == Short.class || type == Character.class ||
                type == Byte.class || type == Boolean.class || type == String.class ||
                type == java.math.BigDecimal.class || type == java.math.BigInteger.class ||
                java.time.temporal.Temporal.class.isAssignableFrom(type) || type == Date.class;
    }

    private Collection<?> convertArrayToCollection(Object array) {
        if (array instanceof Object[]) return Arrays.asList((Object[]) array);
        int length = java.lang.reflect.Array.getLength(array);
        List<Object> list = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            list.add(java.lang.reflect.Array.get(array, i));
        }
        return list;
    }

    @Data
    @Builder
    public static class Change {
        private String fieldName; // Populated dynamically as nested path strings like "user.address.street"
        private Class<?> clazz;
        private Object oldValue;
        private Object newValue;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class OldAndNewObjectPair {
        private Object oldObject;
        private Object newObject;
    }
}