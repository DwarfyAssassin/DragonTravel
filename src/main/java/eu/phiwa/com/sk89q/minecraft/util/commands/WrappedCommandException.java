/*
 * Decompiled with CFR 0_132.
 */
package eu.phiwa.com.sk89q.minecraft.util.commands;

import com.sk89q.minecraft.util.commands.CommandException;

public class WrappedCommandException
extends CommandException {
    private static final long serialVersionUID = -4075721444847778918L;

    public WrappedCommandException(Throwable t) {
        super(t);
    }
}

