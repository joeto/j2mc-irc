package to.joe.j2mc.irc;

import org.bukkit.ChatColor;
import org.jibble.pircbot.PircColors;

public class IRCManager extends Thread {
    J2MC_IRC plugin;
    public IRCBot bot;
    boolean noreturn = false;

    public IRCManager(J2MC_IRC IRC) {
        this.plugin = IRC;
    }

    public void connect() {
        this.bot = new IRCBot(this.plugin.nick, this.plugin, this);
        try {
            this.plugin.getLogger().info("Attempting connection to " + this.plugin.ServerHost + ":" + this.plugin.ServerPort);
            if (this.plugin.bindToIP) {
                this.bot.connect(this.plugin.ServerHost, this.plugin.ServerPort, this.plugin.BindIP);
            } else {
                this.bot.connectWithNoB(this.plugin.ServerHost, this.plugin.ServerPort, null);
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        this.plugin.getLogger().info("Connected! Attempting to auth and join channels.");
        this.bot.joinChannel(this.plugin.NormalChannel);
        this.bot.sendMessage(this.plugin.NormalChannel, "Meow. I am alive");
        this.plugin.getLogger().info("Attempting auth with " + this.plugin.AuthservUsername + " and " + this.plugin.AuthservPassword);
        this.bot.sendMessage("authserv@services.gamesurge.net", "auth " + this.plugin.AuthservUsername + " " + this.plugin.AuthservPassword);
        this.bot.sendMessage("Chanserv", "inviteme " + this.plugin.AdminChannel);
        this.bot.sendMessage("Chanserv", "inviteme " + this.plugin.NormalChannel);
        this.bot.joinChannel(this.plugin.AdminChannel);
    }

    public void disconnect() {
        this.bot.disconnect();
        this.noreturn = true;
    }

    public void sendMessage(String message, boolean adminChannel) {
        if (adminChannel) {
            this.bot.sendMessage(this.plugin.AdminChannel, this.colors(message));
        } else {
            this.bot.sendMessage(this.plugin.NormalChannel, this.colors(message));
        }
    }

    private String colors(String message) {
        message = message.replace(ChatColor.AQUA.toString(), PircColors.TEAL);
        message = message.replace(ChatColor.BLACK.toString(), PircColors.BLACK);
        message = message.replace(ChatColor.BLUE.toString(), PircColors.BLUE);
        message = message.replace(ChatColor.DARK_AQUA.toString(), PircColors.BLUE);
        message = message.replace(ChatColor.DARK_BLUE.toString(), PircColors.BLUE);
        message = message.replace(ChatColor.DARK_GRAY.toString(), PircColors.DARK_GRAY);
        message = message.replace(ChatColor.DARK_GREEN.toString(), PircColors.GREEN);
        message = message.replace(ChatColor.DARK_PURPLE.toString(), PircColors.PURPLE);
        message = message.replace(ChatColor.DARK_RED.toString(), PircColors.RED);
        message = message.replace(ChatColor.GOLD.toString(), PircColors.OLIVE);
        message = message.replace(ChatColor.GRAY.toString(), PircColors.DARK_GRAY);
        message = message.replace(ChatColor.GREEN.toString(), PircColors.GREEN);
        message = message.replace(ChatColor.LIGHT_PURPLE.toString(), PircColors.MAGENTA);
        message = message.replace(ChatColor.RED.toString(), PircColors.RED);
        message = message.replace(ChatColor.WHITE.toString(), PircColors.NORMAL);
        message = message.replace(ChatColor.YELLOW.toString(), PircColors.YELLOW);
        return message;
    }

}
