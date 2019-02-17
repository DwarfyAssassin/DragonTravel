/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.PluginIdentifiableCommand
 *  org.bukkit.plugin.Plugin
 */
package eu.phiwa.com.sk89q.bukkit.util;

import com.sk89q.minecraft.util.commands.CommandsManager;
import com.sk89q.util.StringUtil;
import java.util.Arrays;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;

public class DynamicPluginCommand
extends Command
implements PluginIdentifiableCommand {
    protected final CommandExecutor owner;
    protected final Object registeredWith;
    protected final Plugin owningPlugin;
    protected String[] permissions = new String[0];

    public DynamicPluginCommand(String[] aliases, String desc, String usage, CommandExecutor owner, Object registeredWith, Plugin plugin) {
        super(aliases[0], desc, usage, Arrays.asList(aliases));
        this.owner = owner;
        this.owningPlugin = plugin;
        this.registeredWith = registeredWith;
    }

    public boolean execute(CommandSender sender, String label, String[] args) {
        return this.owner.onCommand(sender, (Command)this, label, args);
    }

    public Object getOwner() {
        return this.owner;
    }

    public Object getRegisteredWith() {
        return this.registeredWith;
    }

    public void setPermissions(String[] permissions) {
        this.permissions = permissions;
        if (permissions != null) {
            super.setPermission(StringUtil.joinString(permissions, ";"));
        }
    }

    public String[] getPermissions() {
        return this.permissions;
    }

    public Plugin getPlugin() {
        return this.owningPlugin;
    }

    public boolean testPermissionSilent(CommandSender sender) {
        if (this.permissions == null || this.permissions.length == 0) {
            return true;
        }
        if (this.registeredWith instanceof CommandsManager) {
            try {
                for (String permission : this.permissions) {
                    if (!((CommandsManager<CommandSender>)this.registeredWith).hasPermission(sender, permission)) continue;
                    return true;
                }
                return false;
            }
            catch (Throwable ignore) {
                // empty catch block
            }
        }
        return super.testPermissionSilent(sender);
    }
}

