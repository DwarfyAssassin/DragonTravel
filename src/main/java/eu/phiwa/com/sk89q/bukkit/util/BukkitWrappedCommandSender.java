/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.BlockCommandSender
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.ConsoleCommandSender
 *  org.bukkit.entity.Player
 */
package eu.phiwa.com.sk89q.bukkit.util;

import com.sk89q.minecraft.util.commands.WrappedCommandSender;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class BukkitWrappedCommandSender
implements WrappedCommandSender {
    private final CommandSender wrapped;

    public BukkitWrappedCommandSender(CommandSender wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public String getName() {
        return this.getName();
    }

    @Override
    public void sendMessage(String message) {
        this.wrapped.sendMessage(message);
    }

    @Override
    public void sendMessage(String[] messages) {
        this.wrapped.sendMessage(messages);
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.wrapped.hasPermission(permission);
    }

    @Override
    public WrappedCommandSender.Type getType() {
        if (this.wrapped instanceof ConsoleCommandSender) {
            return WrappedCommandSender.Type.CONSOLE;
        }
        if (this.wrapped instanceof Player) {
            return WrappedCommandSender.Type.PLAYER;
        }
        if (this.wrapped instanceof BlockCommandSender) {
            return WrappedCommandSender.Type.BLOCK;
        }
        return WrappedCommandSender.Type.UNKNOWN;
    }

    @Override
    public Object getCommandSender() {
        return this.wrapped;
    }
}

