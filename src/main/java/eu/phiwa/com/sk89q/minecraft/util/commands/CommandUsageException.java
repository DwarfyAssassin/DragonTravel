/*
 * Decompiled with CFR 0_132.
 */
package eu.phiwa.com.sk89q.minecraft.util.commands;

import com.sk89q.minecraft.util.commands.CommandException;

public class CommandUsageException
extends CommandException {
    private static final long serialVersionUID = -6761418114414516542L;
    protected String usage;

    public CommandUsageException(String message, String usage) {
        super(message);
        this.usage = usage;
    }

    public String getUsage() {
        return this.usage;
    }
}

