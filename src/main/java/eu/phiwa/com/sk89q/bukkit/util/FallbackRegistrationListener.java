/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandMap
 *  org.bukkit.command.CommandSender
 *  org.bukkit.entity.Player
 *  org.bukkit.event.EventHandler
 *  org.bukkit.event.Listener
 *  org.bukkit.event.player.PlayerCommandPreprocessEvent
 */
package eu.phiwa.com.sk89q.bukkit.util;

import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class FallbackRegistrationListener
implements Listener {
    private final CommandMap commandRegistration;

    public FallbackRegistrationListener(CommandMap commandRegistration) {
        this.commandRegistration = commandRegistration;
    }

    @EventHandler(ignoreCancelled=true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (this.commandRegistration.dispatch((CommandSender)event.getPlayer(), event.getMessage())) {
            event.setCancelled(true);
        }
    }
}

