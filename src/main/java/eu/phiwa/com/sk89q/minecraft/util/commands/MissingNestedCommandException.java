/*
 * Decompiled with CFR 0_132.
 */
package eu.phiwa.com.sk89q.minecraft.util.commands;

import com.sk89q.minecraft.util.commands.CommandUsageException;

public class MissingNestedCommandException
extends CommandUsageException {
    private static final long serialVersionUID = -4382896182979285355L;

    public MissingNestedCommandException(String message, String usage) {
        super(message, usage);
    }
}

