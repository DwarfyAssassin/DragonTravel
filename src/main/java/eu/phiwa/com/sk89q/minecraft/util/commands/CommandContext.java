/*
 * Decompiled with CFR 0_132.
 */
package eu.phiwa.com.sk89q.minecraft.util.commands;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandLocals;
import com.sk89q.minecraft.util.commands.SuggestionContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CommandContext {
    protected final String command;
    protected final List<String> parsedArgs;
    protected final List<Integer> originalArgIndices;
    protected final String[] originalArgs;
    protected final Set<Character> booleanFlags = new HashSet<Character>();
    protected final Map<Character, String> valueFlags = new HashMap<Character, String>();
    protected final SuggestionContext suggestionContext;
    protected final CommandLocals locals;

    public static String[] split(String args) {
        return args.split(" ", -1);
    }

    public CommandContext(String args) throws CommandException {
        this(args.split(" ", -1), null);
    }

    public CommandContext(String[] args) throws CommandException {
        this(args, null);
    }

    public CommandContext(String args, Set<Character> valueFlags) throws CommandException {
        this(args.split(" ", -1), valueFlags);
    }

    public CommandContext(String args, Set<Character> valueFlags, boolean allowHangingFlag) throws CommandException {
        this(args.split(" ", -1), valueFlags, allowHangingFlag, new CommandLocals());
    }

    public CommandContext(String[] args, Set<Character> valueFlags) throws CommandException {
        this(args, valueFlags, false, null);
    }

    public CommandContext(String[] args, Set<Character> valueFlags, boolean allowHangingFlag, CommandLocals locals) throws CommandException {
        String arg;
        if (valueFlags == null) {
            valueFlags = Collections.emptySet();
        }
        this.originalArgs = args;
        this.command = args[0];
        this.locals = locals != null ? locals : new CommandLocals();
        boolean isHanging = false;
        SuggestionContext suggestionContext = SuggestionContext.hangingValue();
        ArrayList<Integer> argIndexList = new ArrayList<Integer>(args.length);
        ArrayList<String> argList = new ArrayList<String>(args.length);
        block3 : for (int i = 1; i < args.length; ++i) {
            isHanging = false;
            arg = args[i];
            if (arg.length() == 0) {
                isHanging = true;
                continue;
            }
            argIndexList.add(i);
            switch (arg.charAt(0)) {
                case '\"': 
                case '\'': {
                    int endIndex;
                    StringBuilder build = new StringBuilder();
                    char quotedChar = arg.charAt(0);
                    for (endIndex = i; endIndex < args.length; ++endIndex) {
                        String arg2 = args[endIndex];
                        if (arg2.charAt(arg2.length() - 1) == quotedChar && arg2.length() > 1) {
                            if (endIndex != i) {
                                build.append(' ');
                            }
                            build.append(arg2.substring(endIndex == i ? 1 : 0, arg2.length() - 1));
                            break;
                        }
                        if (endIndex == i) {
                            build.append(arg2.substring(1));
                            continue;
                        }
                        build.append(' ').append(arg2);
                    }
                    if (endIndex < args.length) {
                        arg = build.toString();
                        i = endIndex;
                    }
                    if (arg.length() == 0) continue block3;
                }
                default: {
                    argList.add(arg);
                }
            }
        }
        this.originalArgIndices = new ArrayList<Integer>(argIndexList.size());
        this.parsedArgs = new ArrayList<String>(argList.size());
        int nextArg = 0;
        block5 : while (nextArg < argList.size()) {
            arg = (String)argList.get(nextArg++);
            suggestionContext = SuggestionContext.hangingValue();
            if (arg.charAt(0) != '-' || arg.length() == 1 || !arg.matches("^-[a-zA-Z]+$")) {
                if (!isHanging) {
                    suggestionContext = SuggestionContext.lastValue();
                }
                this.originalArgIndices.add((Integer)argIndexList.get(nextArg - 1));
                this.parsedArgs.add(arg);
                continue;
            }
            if (arg.equals("--")) {
                while (nextArg < argList.size()) {
                    this.originalArgIndices.add((Integer)argIndexList.get(nextArg));
                    this.parsedArgs.add((String)argList.get(nextArg++));
                }
                break;
            }
            for (int i = 1; i < arg.length(); ++i) {
                char flagName = arg.charAt(i);
                if (valueFlags.contains(Character.valueOf(flagName))) {
                    if (this.valueFlags.containsKey(Character.valueOf(flagName))) {
                        throw new CommandException("Value flag '" + flagName + "' already given");
                    }
                    if (nextArg >= argList.size()) {
                        if (allowHangingFlag) {
                            suggestionContext = SuggestionContext.flag(Character.valueOf(flagName));
                            continue block5;
                        }
                        throw new CommandException("No value specified for the '-" + flagName + "' flag.");
                    }
                    this.valueFlags.put(Character.valueOf(flagName), (String)argList.get(nextArg++));
                    if (isHanging) continue;
                    suggestionContext = SuggestionContext.flag(Character.valueOf(flagName));
                    continue;
                }
                this.booleanFlags.add(Character.valueOf(flagName));
            }
        }
        this.suggestionContext = suggestionContext;
    }

    public SuggestionContext getSuggestionContext() {
        return this.suggestionContext;
    }

    public String getCommand() {
        return this.command;
    }

    public boolean matches(String command) {
        return this.command.equalsIgnoreCase(command);
    }

    public String getString(int index) {
        return this.parsedArgs.get(index);
    }

    public String getString(int index, String def) {
        return index < this.parsedArgs.size() ? this.parsedArgs.get(index) : def;
    }

    public String getJoinedStrings(int initialIndex) {
        initialIndex = this.originalArgIndices.get(initialIndex);
        StringBuilder buffer = new StringBuilder(this.originalArgs[initialIndex]);
        for (int i = initialIndex + 1; i < this.originalArgs.length; ++i) {
            buffer.append(" ").append(this.originalArgs[i]);
        }
        return buffer.toString();
    }

    public String getRemainingString(int start) {
        return this.getString(start, this.parsedArgs.size() - 1);
    }

    public String getString(int start, int end) {
        StringBuilder buffer = new StringBuilder(this.parsedArgs.get(start));
        for (int i = start + 1; i < end + 1; ++i) {
            buffer.append(" ").append(this.parsedArgs.get(i));
        }
        return buffer.toString();
    }

    public int getInteger(int index) throws NumberFormatException {
        return Integer.parseInt(this.parsedArgs.get(index));
    }

    public int getInteger(int index, int def) throws NumberFormatException {
        return index < this.parsedArgs.size() ? Integer.parseInt(this.parsedArgs.get(index)) : def;
    }

    public double getDouble(int index) throws NumberFormatException {
        return Double.parseDouble(this.parsedArgs.get(index));
    }

    public double getDouble(int index, double def) throws NumberFormatException {
        return index < this.parsedArgs.size() ? Double.parseDouble(this.parsedArgs.get(index)) : def;
    }

    public String[] getSlice(int index) {
        String[] slice = new String[this.originalArgs.length - index];
        System.arraycopy(this.originalArgs, index, slice, 0, this.originalArgs.length - index);
        return slice;
    }

    public String[] getPaddedSlice(int index, int padding) {
        String[] slice = new String[this.originalArgs.length - index + padding];
        System.arraycopy(this.originalArgs, index, slice, padding, this.originalArgs.length - index);
        return slice;
    }

    public String[] getParsedSlice(int index) {
        String[] slice = new String[this.parsedArgs.size() - index];
        System.arraycopy(this.parsedArgs.toArray(new String[this.parsedArgs.size()]), index, slice, 0, this.parsedArgs.size() - index);
        return slice;
    }

    public String[] getParsedPaddedSlice(int index, int padding) {
        String[] slice = new String[this.parsedArgs.size() - index + padding];
        System.arraycopy(this.parsedArgs.toArray(new String[this.parsedArgs.size()]), index, slice, padding, this.parsedArgs.size() - index);
        return slice;
    }

    public boolean hasFlag(char ch) {
        return this.booleanFlags.contains(Character.valueOf(ch)) || this.valueFlags.containsKey(Character.valueOf(ch));
    }

    public Set<Character> getFlags() {
        return this.booleanFlags;
    }

    public Map<Character, String> getValueFlags() {
        return this.valueFlags;
    }

    public String getFlag(char ch) {
        return this.valueFlags.get(Character.valueOf(ch));
    }

    public String getFlag(char ch, String def) {
        String value = this.valueFlags.get(Character.valueOf(ch));
        if (value == null) {
            return def;
        }
        return value;
    }

    public int getFlagInteger(char ch) throws NumberFormatException {
        return Integer.parseInt(this.valueFlags.get(Character.valueOf(ch)));
    }

    public int getFlagInteger(char ch, int def) throws NumberFormatException {
        String value = this.valueFlags.get(Character.valueOf(ch));
        if (value == null) {
            return def;
        }
        return Integer.parseInt(value);
    }

    public double getFlagDouble(char ch) throws NumberFormatException {
        return Double.parseDouble(this.valueFlags.get(Character.valueOf(ch)));
    }

    public double getFlagDouble(char ch, double def) throws NumberFormatException {
        String value = this.valueFlags.get(Character.valueOf(ch));
        if (value == null) {
            return def;
        }
        return Double.parseDouble(value);
    }

    public int argsLength() {
        return this.parsedArgs.size();
    }

    public CommandLocals getLocals() {
        return this.locals;
    }
}

