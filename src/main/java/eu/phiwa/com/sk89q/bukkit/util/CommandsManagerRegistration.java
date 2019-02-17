/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.plugin.Plugin
 */
package eu.phiwa.com.sk89q.bukkit.util;

import com.sk89q.bukkit.util.CommandInfo;
import com.sk89q.bukkit.util.CommandRegistration;
import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.CommandsManager;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.Plugin;

public class CommandsManagerRegistration
extends CommandRegistration {
    protected CommandsManager<?> commands;

    public CommandsManagerRegistration(Plugin plugin, CommandsManager<?> commands) {
        super(plugin);
        this.commands = commands;
    }

    public CommandsManagerRegistration(Plugin plugin, CommandExecutor executor, CommandsManager<?> commands) {
        super(plugin, executor);
        this.commands = commands;
    }

    public boolean register(Class<?> clazz) {
        return this.registerAll(this.commands.registerAndReturn(clazz));
    }

    public boolean registerAll(List<Command> registered) {
        ArrayList<CommandInfo> toRegister = new ArrayList<CommandInfo>();
        for (Command command : registered) {
            String[] permissions = null;
            Method cmdMethod = this.commands.getMethods().get(null).get(command.aliases()[0]);
            if (cmdMethod != null && cmdMethod.isAnnotationPresent(CommandPermissions.class)) {
                permissions = cmdMethod.getAnnotation(CommandPermissions.class).value();
            }
            toRegister.add(new CommandInfo(command.usage(), command.desc(), command.aliases(), this.commands, permissions));
        }
        return this.register(toRegister);
    }
}

