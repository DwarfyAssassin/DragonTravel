/*
 * Decompiled with CFR 0_132.
 */
package eu.phiwa.com.sk89q.util;

import java.lang.reflect.Field;

public final class ReflectionUtil {
    private ReflectionUtil() {
    }

    public static <T> T getField(Object from, String name) {
        Class<?> checkClass = from.getClass();
        do {
            try {
                Field field = checkClass.getDeclaredField(name);
                field.setAccessible(true);
                return (T)field.get(from);
            }
            catch (NoSuchFieldException e) {
            }
            catch (IllegalAccessException e) {
                // empty catch block
            }
        } while (checkClass.getSuperclass() != Object.class && (checkClass = checkClass.getSuperclass()) != null);
        return null;
    }
}

