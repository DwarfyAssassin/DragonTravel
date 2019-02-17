/*
 * Decompiled with CFR 0_132.
 */
package eu.phiwa.com.sk89q.minecraft.util.commands;

public interface WrappedCommandSender {
    public String getName();

    public void sendMessage(String var1);

    public void sendMessage(String[] var1);

    public boolean hasPermission(String var1);

    public Type getType();

    public Object getCommandSender();

    public static enum Type {
        CONSOLE,
        PLAYER,
        BLOCK,
        UNKNOWN;
        

        private Type() {
        }
    }

}

