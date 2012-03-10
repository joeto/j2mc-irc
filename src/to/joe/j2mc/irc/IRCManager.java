package to.joe.j2mc.irc;

import java.io.IOException;

import org.bukkit.ChatColor;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircColors;

public class IRCManager {
    J2MC_IRC plugin;
    public IRCBot bot;

    public IRCManager(J2MC_IRC IRC) {
        this.plugin = IRC;
    }

    public void disconnect() {
        bot.disconnect();
    }

    public void connect() {
        bot = new IRCBot(plugin.nick, plugin, this);
        try {
            plugin.getLogger().info(
                    "Attempting connection to " + plugin.ServerHost + ":" + plugin.ServerPort);
            if (plugin.bindToIP) {
                bot.connect(plugin.ServerHost, plugin.ServerPort, plugin.BindIP);
            } else {
                bot.connectWithNoB(plugin.ServerHost, plugin.ServerPort, null);
            }
        } catch (NickAlreadyInUseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IrcException e) {
            e.printStackTrace();
        }
        plugin.getLogger().info("Connected! Attempting to auth and join channels.");
        bot.joinChannel(plugin.NormalChannel);
        bot.sendMessage(plugin.NormalChannel, "Meow. I am alive");
        plugin.getLogger().info("Attempting auth with " + plugin.AuthservUsername + " and " + plugin.AuthservPassword);
        bot.sendMessage("authserv@services.gamesurge.net", "auth " + plugin.AuthservUsername + " " + plugin.AuthservPassword);
        bot.sendMessage("Chanserv", "inviteme " + plugin.AdminChannel);
        bot.sendMessage("Chanserv", "inviteme " + plugin.NormalChannel);
        bot.joinChannel(plugin.AdminChannel);
    }

    public void sendMessage(String message, boolean adminChannel) {
        if (adminChannel) {
            bot.sendMessage(plugin.AdminChannel, this.colors(message));
        } else {
            bot.sendMessage(plugin.NormalChannel, this.colors(message));
        }
    }

    private String colors(String message) {
        message = message.replace(ChatColor.AQUA.toString(), PircColors.TEAL);
        message = message.replace(ChatColor.BLACK.toString(), PircColors.BLACK);
        message = message.replace(ChatColor.BLUE.toString(), PircColors.BLUE);
        message = message.replace(ChatColor.DARK_AQUA.toString(),
                PircColors.BLUE);
        message = message.replace(ChatColor.DARK_BLUE.toString(),
                PircColors.BLUE);
        message = message.replace(ChatColor.DARK_GRAY.toString(),
                PircColors.DARK_GRAY);
        message = message.replace(ChatColor.DARK_GREEN.toString(),
                PircColors.GREEN);
        message = message.replace(ChatColor.DARK_PURPLE.toString(),
                PircColors.PURPLE);
        message = message
                .replace(ChatColor.DARK_RED.toString(), PircColors.RED);
        message = message.replace(ChatColor.GOLD.toString(), PircColors.OLIVE);
        message = message.replace(ChatColor.GRAY.toString(),
                PircColors.DARK_GRAY);
        message = message.replace(ChatColor.GREEN.toString(), PircColors.GREEN);
        message = message.replace(ChatColor.LIGHT_PURPLE.toString(),
                PircColors.MAGENTA);
        message = message.replace(ChatColor.RED.toString(), PircColors.RED);
        message = message
                .replace(ChatColor.WHITE.toString(), PircColors.NORMAL);
        message = message.replace(ChatColor.YELLOW.toString(),
                PircColors.YELLOW);
        return message;
    }

}
