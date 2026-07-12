package com.karur.access_management_application.security.compare;

public class ChangeUtil {

    public static String getStringElseConvert(CompareUtil.Change change) {
        if (change.getRightValue() instanceof String) {
            return (String) change.getRightValue();
        }
        return change.getRightValue().toString();
    }

    public static Integer getIntegerElseConvert(CompareUtil.Change change) {
        if (change.getRightValue() instanceof Integer) {
            return (Integer) change.getRightValue();
        }
        return Integer.parseInt(getStringElseConvert(change));
    }
}
