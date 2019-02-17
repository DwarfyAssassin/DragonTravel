/*
 * Decompiled with CFR 0_132.
 */
package eu.phiwa.com.sk89q.minecraft.util.commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value=RetentionPolicy.RUNTIME)
public @interface Logging {
    public LogMode value();

    public static enum LogMode {
        POSITION,
        REGION,
        ORIENTATION_REGION,
        PLACEMENT,
        ALL;
        

        private LogMode() {
        }
    }

}

