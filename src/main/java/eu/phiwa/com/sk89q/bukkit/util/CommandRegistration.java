/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.Server
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandMap
 *  org.bukkit.command.SimpleCommandMap
 *  org.bukkit.event.Listener
 *  org.bukkit.help.HelpMap
 *  org.bukkit.help.HelpTopicFactory
 *  org.bukkit.plugin.Plugin
 *  org.bukkit.plugin.PluginDescriptionFile
 *  org.bukkit.plugin.PluginManager
 */
package eu.phiwa.com.sk89q.bukkit.util;

import com.sk89q.bukkit.util.CommandInfo;
import com.sk89q.bukkit.util.DynamicPluginCommand;
import com.sk89q.bukkit.util.DynamicPluginCommandHelpTopic;
import com.sk89q.bukkit.util.FallbackRegistrationListener;
import com.sk89q.util.ReflectionUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.Listener;
import org.bukkit.help.HelpTopicFactory;
import org.bukkit.plugin.Plugin;

public class CommandRegistration {
    protected final Plugin plugin;
    protected final CommandExecutor executor;
    private CommandMap fallbackCommands;

    public CommandRegistration(Plugin plugin) {
        this(plugin, (CommandExecutor)plugin);
    }

    public CommandRegistration(Plugin plugin, CommandExecutor executor) {
        this.plugin = plugin;
        this.executor = executor;
    }

    public boolean register(List<CommandInfo> registered) {
        CommandMap commandMap = this.getCommandMap();
        if (registered == null || commandMap == null) {
            return false;
        }
        for (CommandInfo command : registered) {
            DynamicPluginCommand cmd = new DynamicPluginCommand(command.getAliases(), command.getDesc(), "/" + command.getAliases()[0] + " " + command.getUsage(), this.executor, command.getRegisteredWith(), this.plugin);
            cmd.setPermissions(command.getPermissions());
            commandMap.register(this.plugin.getDescription().getName(), (Command)cmd);
        }
        return true;
    }

    public CommandMap getCommandMap() {
        CommandMap commandMap = (CommandMap)ReflectionUtil.getField((Object)this.plugin.getServer().getPluginManager(), "commandMap");
        if (commandMap == null) {
            if (this.fallbackCommands != null) {
                commandMap = this.fallbackCommands;
            } else {
                Bukkit.getServer().getLogger().severe(this.plugin.getDescription().getName() + ": Could not retrieve server CommandMap, using fallback instead! Please report to http://redmine.sk89q.com");
                this.fallbackCommands = commandMap = new SimpleCommandMap(Bukkit.getServer());
                Bukkit.getServer().getPluginManager().registerEvents((Listener)new FallbackRegistrationListener(this.fallbackCommands), this.plugin);
            }
        }
        return commandMap;
    }

    public boolean unregisterCommands() {
        CommandMap commandMap = this.getCommandMap();
        ArrayList<String> toRemove = new ArrayList<String>();
        Map<?, ?> knownCommands = (Map<?, ?>)ReflectionUtil.getField((Object)commandMap, "knownCommands");
        Set<?> aliases = (Set<?>)ReflectionUtil.getField((Object)commandMap, "aliases");
        if (knownCommands == null || aliases == null) {
            return false;
        }
        Iterator<?> i = knownCommands.values().iterator();
        while (i.hasNext()) {
            Command cmd = (Command)i.next();
            if (!(cmd instanceof DynamicPluginCommand) || !((DynamicPluginCommand)cmd).getOwner().equals((Object)this.executor)) continue;
            i.remove();
            for (String alias : cmd.getAliases()) {
                Command aliasCmd = (Command)knownCommands.get(alias);
                if (!cmd.equals((Object)aliasCmd)) continue;
                aliases.remove(alias);
                toRemove.add(alias);
            }
        }
        for (String string : toRemove) {
            knownCommands.remove(string);
        }
        return true;
    }

    static {
        Bukkit.getServer().getHelpMap().registerHelpTopicFactory(DynamicPluginCommand.class, (HelpTopicFactory<?>)new DynamicPluginCommandHelpTopic.Factory());
    }
}

