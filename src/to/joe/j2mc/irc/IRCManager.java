package to.joe.j2mc.irc;

import org.bukkit.ChatColor;
import org.jibble.pircbot.PircColors;

public class IRCManager extends Thread {

    private J2MC_IRC plugin;
    public IRCBot bot;

    public IRCManager(J2MC_IRC IRC) {
        this.plugin = IRC;
    }
    
    @Override
    public void run() {
        this.bot = new IRCBot(this.plugin.nick, this.plugin, this);
        this.connect();   
    }

    public void connect() {
        try {
            this.plugin.getLogger().info("Attempting connection to " + this.plugin.serverHost + ":" + this.plugin.serverPort);
            if (this.plugin.bindToIP) {
                this.bot.connect(this.plugin.serverHost, this.plugin.serverPort, this.plugin.bindIP);
            } else {
                this.bot.connectWithNoB(this.plugin.serverHost, this.plugin.serverPort, null);
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        this.plugin.getLogger().info("Connected! Attempting to auth and join channels.");
        this.bot.joinChannel(this.plugin.normalChannel);
        this.bot.sendMessage(this.plugin.normalChannel, "Meow. I am alive");
        this.plugin.getLogger().info("Attempting auth with " + this.plugin.authservUsername + " and " + this.plugin.authservPassword);
        this.bot.sendMessage("authserv@services.gamesurge.net", "auth " + this.plugin.authservUsername + " " + this.plugin.authservPassword);
        this.bot.sendMessage("Chanserv", "inviteme " + this.plugin.adminChannel);
        this.bot.sendMessage("Chanserv", "inviteme " + this.plugin.normalChannel);
        this.bot.joinChannel(this.plugin.adminChannel);
    }

    public void disconnect() {
        this.bot.quitServer("SHUT. DOWN. EVERYTHING.");
    }

    public void sendMessage(String message, boolean adminChannel) {
        if (adminChannel) {
            this.bot.sendMessage(this.plugin.adminChannel, this.colors(message));
        } else {
            this.bot.sendMessage(this.plugin.normalChannel, this.colors(message));
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
