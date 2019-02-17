/*
 * Decompiled with CFR 0_132.
 */
package eu.phiwa.com.sk89q.minecraft.util.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandAlias;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.minecraft.util.commands.CommandPermissionsException;
import com.sk89q.minecraft.util.commands.CommandUsageException;
import com.sk89q.minecraft.util.commands.Injector;
import com.sk89q.minecraft.util.commands.MissingNestedCommandException;
import com.sk89q.minecraft.util.commands.NestedCommand;
import com.sk89q.minecraft.util.commands.UnhandledCommandException;
import com.sk89q.minecraft.util.commands.WrappedCommandException;
import com.sk89q.util.StringUtil;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class CommandsManager<T> {
    protected static final Logger logger = Logger.getLogger(CommandsManager.class.getCanonicalName());
    protected Map<Method, Map<String, Method>> commands = new HashMap<Method, Map<String, Method>>();
    protected Map<Method, Object> instances = new HashMap<Method, Object>();
    protected Map<String, String> descs = new HashMap<String, String>();
    protected Injector injector;
    protected Map<String, String> helpMessages = new HashMap<String, String>();

    public void register(Class<?> cls) {
        this.registerMethods(cls, null);
    }

    public List<Command> registerAndReturn(Class<?> cls) {
        return this.registerMethods(cls, null);
    }

    public List<Command> registerMethods(Class<?> cls, Method parent) {
        try {
            if (this.getInjector() == null) {
                return this.registerMethods(cls, parent, null);
            }
            Object obj = this.getInjector().getInstance(cls);
            return this.registerMethods(cls, parent, obj);
        }
        catch (InvocationTargetException e) {
            logger.log(Level.SEVERE, "Failed to register commands", e);
        }
        catch (IllegalAccessException e) {
            logger.log(Level.SEVERE, "Failed to register commands", e);
        }
        catch (InstantiationException e) {
            logger.log(Level.SEVERE, "Failed to register commands", e);
        }
        return null;
    }

    private List<Command> registerMethods(Class<?> cls, Method parent, Object obj) {
        Map<String, Method> map;
        ArrayList<Command> registered = new ArrayList<Command>();
        if (this.commands.containsKey(parent)) {
            map = this.commands.get(parent);
        } else {
            map = new HashMap<String, Method>();
            this.commands.put(parent, map);
        }
        for (Method method : cls.getMethods()) {
            if (!method.isAnnotationPresent(Command.class)) continue;
            boolean isStatic = Modifier.isStatic(method.getModifiers());
            Command cmd = method.getAnnotation(Command.class);
            for (String alias : cmd.aliases()) {
                map.put(alias, method);
            }
            if (!isStatic) {
                if (obj == null) continue;
                this.instances.put(method, obj);
            }
            if (parent == null) {
                String commandName = cmd.aliases()[0];
                String desc = cmd.desc();
                String usage = cmd.usage();
                if (usage.length() == 0) {
                    this.descs.put(commandName, desc);
                } else {
                    this.descs.put(commandName, usage + " - " + desc);
                }
                String help = cmd.help();
                if (help.length() == 0) {
                    help = desc;
                }
                CharSequence arguments = this.getArguments(cmd);
                for (String alias : cmd.aliases()) {
                    String helpMessage = "/" + alias + " " + arguments + "\n\n" + help;
                    String key = alias.replaceAll("/", "");
                    String previous = this.helpMessages.put(key, helpMessage);
                    if (previous == null || previous.replaceAll("^/[^ ]+ ", "").equals(helpMessage.replaceAll("^/[^ ]+ ", ""))) continue;
                    this.helpMessages.put(key, previous + "\n\n" + helpMessage);
                }
            }
            registered.add(cmd);
            if (!method.isAnnotationPresent(NestedCommand.class)) continue;
            NestedCommand nestedCmd = method.getAnnotation(NestedCommand.class);
            for (Class<?> nestedCls : nestedCmd.value()) {
                this.registerMethods(nestedCls, method);
            }
        }
        if (cls.getSuperclass() != null) {
            this.registerMethods(cls.getSuperclass(), parent, obj);
        }
        return registered;
    }

    public boolean hasCommand(String command) {
        return this.commands.get(null).containsKey(command.toLowerCase());
    }

    public Map<String, String> getCommands() {
        return this.descs;
    }

    public Map<Method, Map<String, Method>> getMethods() {
        return this.commands;
    }

    public Map<String, String> getHelpMessages() {
        return this.helpMessages;
    }

    protected String getUsage(String[] args, int level, Command cmd) {
        StringBuilder command = new StringBuilder();
        command.append('/');
        for (int i = 0; i <= level; ++i) {
            command.append(args[i]);
            command.append(' ');
        }
        command.append(this.getArguments(cmd));
        String help = cmd.help();
        if (help.length() > 0) {
            command.append("\n\n");
            command.append(help);
        }
        return command.toString();
    }

    protected CharSequence getArguments(Command cmd) {
        String flagString;
        String flags = cmd.flags();
        StringBuilder command2 = new StringBuilder();
        if (flags.length() > 0 && (flagString = flags.replaceAll(".:", "")).length() > 0) {
            command2.append("[-");
            for (int i = 0; i < flagString.length(); ++i) {
                command2.append(flagString.charAt(i));
            }
            command2.append("] ");
        }
        command2.append(cmd.usage());
        return command2;
    }

    protected String getNestedUsage(String[] args, int level, Method method, T player) throws CommandException {
        StringBuilder command = new StringBuilder();
        command.append("/");
        for (int i = 0; i <= level; ++i) {
            command.append(args[i] + " ");
        }
        Map<String, Method> map = this.commands.get(method);
        boolean found = false;
        command.append("<");
        HashSet<String> allowedCommands = new HashSet<String>();
        for (Map.Entry<String, Method> entry : map.entrySet()) {
            Method childMethod = entry.getValue();
            found = true;
            if (!this.hasPermission(childMethod, player)) continue;
            Command childCmd = childMethod.getAnnotation(Command.class);
            allowedCommands.add(childCmd.aliases()[0]);
        }
        if (allowedCommands.size() > 0) {
            command.append(StringUtil.joinString(allowedCommands, "|", 0));
        } else if (!found) {
            command.append("?");
        } else {
            throw new CommandPermissionsException();
        }
        command.append(">");
        return command.toString();
    }

    public /* varargs */ void execute(String cmd, String[] args, T player, Object ... methodArgs) throws CommandException {
        String[] newArgs = new String[args.length + 1];
        System.arraycopy(args, 0, newArgs, 1, args.length);
        newArgs[0] = cmd;
        Object[] newMethodArgs = new Object[methodArgs.length + 1];
        System.arraycopy(methodArgs, 0, newMethodArgs, 1, methodArgs.length);
        this.executeMethod(null, newArgs, player, newMethodArgs, 0);
    }

    public /* varargs */ void execute(String[] args, T player, Object ... methodArgs) throws CommandException {
        Object[] newMethodArgs = new Object[methodArgs.length + 1];
        System.arraycopy(methodArgs, 0, newMethodArgs, 1, methodArgs.length);
        this.executeMethod(null, args, player, newMethodArgs, 0);
    }

    public void executeMethod(Method parent, String[] args, T player, Object[] methodArgs, int level) throws CommandException {
        boolean executeNested;
        String cmdName = args[level];
        Map<String, Method> map = this.commands.get(parent);
        Method method = map.get(cmdName.toLowerCase());
        if (method == null) {
            if (parent == null) {
                throw new UnhandledCommandException();
            }
            throw new MissingNestedCommandException("Unknown command: " + cmdName, this.getNestedUsage(args, level - 1, parent, player));
        }
        this.checkPermission(player, method);
        int argsCount = args.length - 1 - level;
        executeNested = method.isAnnotationPresent(NestedCommand.class) && (argsCount > 0 || !method.getAnnotation(NestedCommand.class).executeBody());
        if (executeNested) {
            if (argsCount == 0) {
                throw new MissingNestedCommandException("Sub-command required.", this.getNestedUsage(args, level, method, player));
            }
            this.executeMethod(method, args, player, methodArgs, level + 1);
        } else if (method.isAnnotationPresent(CommandAlias.class)) {
            CommandAlias aCmd = method.getAnnotation(CommandAlias.class);
            this.executeMethod(parent, aCmd.value(), player, methodArgs, level);
        } else {
            Command cmd = method.getAnnotation(Command.class);
            String[] newArgs = new String[args.length - level];
            System.arraycopy(args, level, newArgs, 0, args.length - level);
            HashSet<Character> valueFlags = new HashSet<Character>();
            char[] flags = cmd.flags().toCharArray();
            HashSet<Character> newFlags = new HashSet<Character>();
            for (int i = 0; i < flags.length; ++i) {
                if (flags.length > i + 1 && flags[i + 1] == ':') {
                    valueFlags.add(Character.valueOf(flags[i]));
                    ++i;
                }
                newFlags.add(Character.valueOf(flags[i]));
            }
            CommandContext context = new CommandContext(newArgs, valueFlags);
            if (context.argsLength() < cmd.min()) {
                throw new CommandUsageException("Too few arguments.", this.getUsage(args, level, cmd));
            }
            if (cmd.max() != -1 && context.argsLength() > cmd.max()) {
                throw new CommandUsageException("Too many arguments.", this.getUsage(args, level, cmd));
            }
            if (!cmd.anyFlags()) {
                Iterator<Character> i$ = context.getFlags().iterator();
                while (i$.hasNext()) {
                    char flag = i$.next().charValue();
                    if (newFlags.contains(Character.valueOf(flag))) continue;
                    throw new CommandUsageException("Unknown flag: " + flag, this.getUsage(args, level, cmd));
                }
            }
            methodArgs[0] = context;
            Object instance = this.instances.get(method);
            this.invokeMethod(parent, args, player, method, instance, methodArgs, argsCount);
        }
    }

    protected void checkPermission(T player, Method method) throws CommandException {
        if (!this.hasPermission(method, player)) {
            throw new CommandPermissionsException();
        }
    }

    public void invokeMethod(Method parent, String[] args, T player, Method method, Object instance, Object[] methodArgs, int level) throws CommandException {
        try {
            method.invoke(instance, methodArgs);
        }
        catch (IllegalArgumentException e) {
            logger.log(Level.SEVERE, "Failed to execute command", e);
        }
        catch (IllegalAccessException e) {
            logger.log(Level.SEVERE, "Failed to execute command", e);
        }
        catch (InvocationTargetException e) {
            if (e.getCause() instanceof CommandException) {
                throw (CommandException)e.getCause();
            }
            throw new WrappedCommandException(e.getCause());
        }
    }

    protected boolean hasPermission(Method method, T player) {
        CommandPermissions perms = method.getAnnotation(CommandPermissions.class);
        if (perms == null) {
            return true;
        }
        for (String perm : perms.value()) {
            if (!this.hasPermission(player, perm)) continue;
            return true;
        }
        return false;
    }

    public abstract boolean hasPermission(T var1, String var2);

    public Injector getInjector() {
        return this.injector;
    }

    public void setInjector(Injector injector) {
        this.injector = injector;
    }
}

