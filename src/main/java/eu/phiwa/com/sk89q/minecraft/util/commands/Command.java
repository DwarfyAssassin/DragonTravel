/*
 * Decompiled with CFR 0_132.
 */
package eu.phiwa.com.sk89q.minecraft.util.commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value=RetentionPolicy.RUNTIME)
public @interface Command {
    public String[] aliases();

    public String usage() default "";

    public String desc();

    public int min() default 0;

    public int max() default -1;

    public String flags() default "";

    public String help() default "";

    public boolean anyFlags() default false;
}

