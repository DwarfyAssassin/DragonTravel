/*
 * Decompiled with CFR 0_132.
 * 
 * Could not load the following classes:
 *  org.bukkit.ChatColor
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandSender
 *  org.bukkit.help.HelpTopic
 *  org.bukkit.help.HelpTopicFactory
 */
package eu.phiwa.com.sk89q.bukkit.util;

import com.sk89q.bukkit.util.DynamicPluginCommand;
import com.sk89q.minecraft.util.commands.CommandsManager;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.help.HelpTopic;
import org.bukkit.help.HelpTopicFactory;

public class DynamicPluginCommandHelpTopic
extends HelpTopic {
    private final DynamicPluginCommand cmd;

    public DynamicPluginCommandHelpTopic(DynamicPluginCommand cmd) {
        this.cmd = cmd;
        this.name = "/" + cmd.getName();
        String fullTextTemp = null;
        StringBuilder fullText = new StringBuilder();
        if (cmd.getRegisteredWith() instanceof CommandsManager) {
            String lookupName;
            Map<String, String> helpText = ((CommandsManager<?>)cmd.getRegisteredWith()).getHelpMessages();
            if (helpText.containsKey(lookupName = cmd.getName().replaceAll("/", ""))) {
                fullTextTemp = helpText.get(lookupName);
            }
            if ((helpText = ((CommandsManager<?>)cmd.getRegisteredWith()).getCommands()).containsKey(cmd.getName())) {
                String shortText = helpText.get(cmd.getName());
                if (fullTextTemp == null) {
                    fullTextTemp = this.name + " " + shortText;
                }
                this.shortText = shortText;
            }
        } else {
            this.shortText = cmd.getDescription();
        }
        String[] split = fullTextTemp == null ? new String[2] : fullTextTemp.split("\n", 2);
        fullText.append((Object)ChatColor.BOLD).append((Object)ChatColor.GOLD).append("Usage: ").append((Object)ChatColor.WHITE);
        fullText.append(split[0]).append("\n");
        if (cmd.getAliases().size() > 0) {
            fullText.append((Object)ChatColor.BOLD).append((Object)ChatColor.GOLD).append("Aliases: ").append((Object)ChatColor.WHITE);
            boolean first = true;
            for (String alias : cmd.getAliases()) {
                if (!first) {
                    fullText.append(", ");
                }
                fullText.append(alias);
                first = false;
            }
            fullText.append("\n");
        }
        if (split.length > 1) {
            fullText.append(split[1]);
        }
        this.fullText = fullText.toString();
    }

    public boolean canSee(CommandSender player) {
        if (this.cmd.getPermissions() != null && this.cmd.getPermissions().length > 0) {
            if (this.cmd.getRegisteredWith() instanceof CommandsManager) {
                try {
                    for (String perm : this.cmd.getPermissions()) {
                        if (!((CommandsManager<CommandSender>)this.cmd.getRegisteredWith()).hasPermission(player, perm)) continue;
                        return true;
                    }
                }
                catch (Throwable t) {
                    // empty catch block
                }
            }
            for (String perm : this.cmd.getPermissions()) {
                if (!player.hasPermission(perm)) continue;
                return true;
            }
            return false;
        }
        return true;
    }

    public String getFullText(CommandSender forWho) {
        if (this.fullText == null || this.fullText.length() == 0) {
            return this.getShortText();
        }
        return this.fullText;
    }

    public static class Factory
    implements HelpTopicFactory<DynamicPluginCommand> {
        public HelpTopic createTopic(DynamicPluginCommand command) {
            return new DynamicPluginCommandHelpTopic(command);
        }
    }

}

