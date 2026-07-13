package com.karur.access_management_application.security.compare;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class CompareUtil {

    public static <T> List<Change> compare(T left, T right) {
        List<Change> changes = new ArrayList<>();
        if (Objects.isNull(left) && Objects.isNull(right)) {
            return changes;
        }
        if (Objects.isNull(left) || Objects.isNull(right)) {
            return changes;
        }
        if (!left.getClass().equals(right.getClass())) {
            return changes;
        }
        compare(left, right, changes, left, right, null, null, null);
        return changes;
    }

    public static void compare(Object leftParent, Object rightParent, List<Change> changes
            , Object left, Object right, Integer leftIndex, Integer rightIndex, Field field) {
        if (Objects.isNull(left) && Objects.isNull(right)) {
            return;
        }
        if (Objects.isNull(left) || Objects.isNull(right)) {
            changes.add(Change.builder()
                    .field(field.getName())
                    .leftParent(leftParent).rightParent(rightParent)
                    .left(leftParent).right(rightParent)
                    .leftValue(left).rightValue(right)
                    .leftIndex(leftIndex).rightIndex(rightIndex)
                    .build());
            return;
        }
        if (left instanceof Collection<?> && right instanceof Collection<?>) {
            compare(leftParent, rightParent, changes, (Collection<?>) left, (Collection<?>) right, field);
            return;
        }
        if (isSimpleType(left.getClass())) {
            if (!left.equals(right)) {
                changes.add(Change.builder()
                        .field(field.getName())
                        .leftParent(leftParent).rightParent(rightParent)
                        .left(leftParent).right(rightParent)
                        .leftValue(left).rightValue(right)
                        .leftIndex(leftIndex).rightIndex(rightIndex)
                        .build());
            }
            return;
        }
        compare(leftParent, rightParent, changes, leftIndex, rightIndex, left, right);
    }

    private static void compare(Object leftParent, Object rightParent, List<Change> changes, Integer leftIndex, Integer rightIndex, Object left, Object right) {
        Class<?> clazz = left.getClass();
        while (Objects.nonNull(clazz) && !Object.class.equals(clazz)) {
            for (Field field : clazz.getDeclaredFields()) {
                if (Modifier.isStatic(field.getModifiers()) || field.isSynthetic()) {
                    continue;
                }
                if (field.isAnnotationPresent(IgnoreChange.class) || field.isAnnotationPresent(SecretChange.class)) {
                    continue;
                }
                try {
                    field.setAccessible(true);
                    Object leftValue = field.get(left);
                    Object rightValue = field.get(right);
                    compare(left, right, changes, leftValue, rightValue, leftIndex, rightIndex, field);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    private static void compare(Object leftParent, Object rightParent, List<Change> changes, Collection<?> left, Collection<?> right, Field field) {
        if (left.isEmpty() && right.isEmpty()) {
            return;
        }
        if (left.isEmpty() || right.isEmpty()) {
            addCollectionChanges(leftParent, rightParent, changes, left, right, field);
        }
        if (left.iterator().hasNext()) {
            Object firstElement = left.iterator().next();
            List<Field> diffIdFields = findDiffIdFields(firstElement);
            compareCollectionsByDiffId(leftParent, rightParent, changes, left, right, field, diffIdFields);
        }
    }

    private static void compareCollectionsByDiffId(Object leftParent, Object rightParent, List<Change> changes, Collection<?> left, Collection<?> right, Field field, List<Field> diffIdFields) {
        Map<String, IndexedElement> leftMap = buildIndexMap(left, diffIdFields, "left");
        Map<String, IndexedElement> rightMap = buildIndexMap(right, diffIdFields, "right");
        for (Map.Entry<String, IndexedElement> elementEntry : leftMap.entrySet()) {
            String key = elementEntry.getKey();
            IndexedElement leftIndexedElement = elementEntry.getValue();
            IndexedElement rightIndexedElement = rightMap.get(key);
            if (Objects.isNull(rightIndexedElement)) {
                changes.add(Change.builder()
                        .field(field.getName())
                        .leftParent(leftParent).rightParent(rightParent)
                        .left(leftIndexedElement.element).right(null)
                        .leftValue(leftIndexedElement.element).rightValue(null)
                        .leftIndex(leftIndexedElement.index).rightIndex(null)
                        .build());
            } else {
                compare(leftParent, rightParent, changes, leftIndexedElement.element, rightIndexedElement.element, leftIndexedElement.index, rightIndexedElement.index, null);
            }
        }

        for (Map.Entry<String, IndexedElement> elementEntry : rightMap.entrySet()) {
            if (!leftMap.containsKey(elementEntry.getKey())) {
                changes.add(Change.builder()
                        .field(field.getName())
                        .leftParent(leftParent).rightParent(rightParent)
                        .left(null).right(elementEntry.getValue().element)
                        .leftValue(null).rightValue(elementEntry.getValue().element)
                        .leftIndex(null).rightIndex(elementEntry.getValue().index)
                        .build());
            }
        }
    }

    private static Map<String, IndexedElement> buildIndexMap(Collection<?> collection, List<Field> diffIdFields, String side) {
        LinkedHashMap<String, IndexedElement> map = new LinkedHashMap<>();
        int index = 0;
        for (Object element : collection) {
            String key = buildDiffKey(element, diffIdFields);
            if (map.containsKey(key)) {
                throw new IllegalArgumentException("Duplicate @DiffeId in " + side);
            }
            map.put(key, new IndexedElement(element, index++));
        }
        return map;
    }

    private static String buildDiffKey(Object object, List<Field> diffIdFields) {
        return diffIdFields.stream().map(field -> field.getName() + "=" + getFieldValue(field, object))
                .collect(Collectors.joining("_"));
    }

    private static List<Field> findDiffIdFields(Object object) {
        List<Field> fields = new ArrayList<>();
        Class<?> current = object.getClass();
        while (Objects.nonNull(current) && !Object.class.equals(current)) {
            for (Field field : current.getDeclaredFields()) {
                if (field.isAnnotationPresent(DiffId.class)) {
                    field.setAccessible(true);
                    if (Objects.nonNull(getFieldValue(field, object))) {
                        fields.add(field);
                    } else {
                        throw new IllegalArgumentException("Diff Id can't be null");
                    }
                }
            }
            current = current.getSuperclass();
        }
        if (fields.isEmpty()) {
            throw new IllegalArgumentException("Diff Id can't be null");
        }
        return fields;
    }

    private static Object getFieldValue(Field field, Object object) {
        try {
            return field.get(object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static void addCollectionChanges(Object leftParent, Object rightParent, List<Change> changes, Collection<?> left, Collection<?> right, Field field) {
        int index = 0;
        for (Object object : left) {
            changes.add(Change.builder()
                    .field(field.getName())
                    .leftParent(leftParent).rightParent(rightParent)
                    .left(object).right(null)
                    .leftValue(object).rightValue(null)
                    .leftIndex(index).rightIndex(null)
                    .build());
            index++;
        }
        index = 0;
        for (Object object : right) {
            changes.add(Change.builder()
                    .field(field.getName())
                    .leftParent(leftParent).rightParent(rightParent)
                    .left(null).right(object)
                    .leftValue(null).rightValue(object)
                    .leftIndex(null).rightIndex(index)
                    .build());
            index++;
        }
    }

    public static boolean isSimpleType(Class<?> type) {
        return type.isPrimitive()
                || type.isEnum()
                || Number.class.isAssignableFrom(type)
                || CharSequence.class.isAssignableFrom(type)
                || Boolean.class.isAssignableFrom(type)
                || Character.class.isAssignableFrom(type)
                || Data.class.isAssignableFrom(type)
                || LocalDate.class.isAssignableFrom(type)
                || LocalDateTime.class.isAssignableFrom(type)
                || UUID.class.isAssignableFrom(type)
                || Timestamp.class.isAssignableFrom(type);
    }

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Change {
        private String field;
        private Object leftParent;
        private Object rightParent;
        private Object left;
        private Object right;
        private Object leftValue;
        private Object rightValue;
        private Object leftIndex;
        private Object rightIndex;
    }

    @Data
    @AllArgsConstructor
    public static class IndexedElement {
        private final Object element;
        private final int index;
    }
}
