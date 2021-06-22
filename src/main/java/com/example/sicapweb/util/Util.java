package com.example.sicapweb.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Util {

    public static <T> T parseEnum(Object value, final Class<T> clazz) {
        if (value == null)
            return null;
        try {
            Method values = clazz.getMethod("values");
            for (Object item : (Object[]) values.invoke(clazz)) {
                if (item.getClass().getDeclaredMethod("getValor").invoke(item) == value ||
                        item.getClass().getDeclaredMethod("getValor").invoke(item).equals(value)) {
                    return (T) item;
                }
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return null;
        }
        return null;
    }
}
