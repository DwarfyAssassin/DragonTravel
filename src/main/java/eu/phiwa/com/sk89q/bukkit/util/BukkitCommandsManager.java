/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandSender
 */
package eu.phiwa.com.sk89q.bukkit.util;

import com.sk89q.minecraft.util.commands.CommandsManager;
import org.bukkit.command.CommandSender;

public class BukkitCommandsManager
extends CommandsManager<CommandSender> {
    @Override
    public boolean hasPermission(CommandSender player, String perm) {
        return player.hasPermission(perm);
    }
}

