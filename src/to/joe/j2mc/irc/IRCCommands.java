package to.joe.j2mc.irc;

import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import to.joe.j2mc.core.J2MC_Core;
import to.joe.j2mc.core.J2MC_Manager;
import to.joe.j2mc.core.event.MessageEvent;
import to.joe.j2mc.core.exceptions.BadPlayerMatchException;

public class IRCCommands {

    private J2MC_IRC plugin;
    private IRCBot bot;

    public IRCCommands(IRCBot bot, J2MC_IRC IRC) {
        this.plugin = IRC;
        this.bot = bot;
    }

    public void AdminsCommandinPrivate(String channel) {
        int admins = 0;
        for (final Player plr : this.plugin.getServer().getOnlinePlayers()) {
            if (plr.hasPermission("j2mc.core.admin")) {
                admins++;
            }
        }
        String toSend = " ";
        if (admins == 0) {
            toSend = "No admins online";
        } else {
            toSend = "Admins: ";
            for (final Player plr : this.plugin.getServer().getOnlinePlayers()) {
                if (plr.hasPermission("j2mc.core.admin")) {
                    String name = plr.getName();
                    if (name.equals("untamed")) {
                        name = "untaemd";
                    }
                    toSend = toSend + name + ", ";
                }
            }
            toSend = toSend.substring(0, toSend.length() - 2);
        }
        this.bot.sendMessage(channel, toSend);
    }

    public void AdminsCommandinPublic(String channel) {
        boolean haveAdmins = false;
        for (final Player plr : this.plugin.getServer().getOnlinePlayers()) {
            if (plr.hasPermission("j2mc.core.admin")) {
                haveAdmins = true;
            }
        }
        if (haveAdmins) {
            this.bot.sendMessage(channel, "There are admins online");
        } else {
            this.bot.sendMessage(channel, "No admins online see #joe.to or #minecraft");
        }
    }

    public void dotAcommand(String hostname, String message, String sender) {
        final String broadcastmessage = "<" + ChatColor.LIGHT_PURPLE + this.plugin.hosts.get(hostname) + ChatColor.WHITE + "> " + message;
        J2MC_Manager.getCore().adminAndLog(broadcastmessage);
        this.bot.sendNotice(sender, "Message sent!");
    }

    public void dotAddBanCommand(String sender, String hostname, String message) {
        final String adminName = this.plugin.hosts.get(hostname);
        final String[] split = message.split(" ");
        if (split.length < 3) {
            this.bot.sendNotice(sender, "Usage: addban <player> reason");
            return;
        }
        String target = split[1];
        String reason = J2MC_Core.combineSplit(2, split, " ");
        target = target.replace(":", "/OMGREPLACEWITHCOLON\\");
        reason = reason.replace(":", "/OMGREPLACEWITHCOLON\\");
        final String toSend = (adminName + ":" + target + ":" + reason + ":" + sender);
        final HashSet<String> targets = new HashSet<String>();
        targets.add("NEWADDBAN");
        this.plugin.getServer().getPluginManager().callEvent(new MessageEvent(targets, toSend));
    }

    public void dotBanCommand(String sender, String hostname, String message) {
        final String adminName = this.plugin.hosts.get(hostname);
        final String[] split = message.split(" ");
        if (split.length < 3) {
            this.bot.sendNotice(sender, "Usage: ban <player> reason");
            return;
        }
        String target = split[1];
        String reason = J2MC_Core.combineSplit(2, split, " ");
        target = target.replace(":", "/OMGREPLACEWITHCOLON\\");
        reason = reason.replace(":", "/OMGREPLACEWITHCOLON\\");
        final String toSend = (adminName + ":" + target + ":" + reason + ":" + sender);
        final HashSet<String> targets = new HashSet<String>();
        targets.add("NEWBAN");
        this.plugin.getServer().getPluginManager().callEvent(new MessageEvent(targets, toSend));
    }

    public void dotGCommand(String hostname, String message, String sender) {
        for (final Player plr : J2MC_Manager.getVisibility().getOnlinePlayers(null)) {
            if (plr.hasPermission("j2mc.admintoolkit.admin")) {
                plr.sendMessage("<" + this.plugin.hosts.get(hostname) + "-irc> " + ChatColor.LIGHT_PURPLE + message);
            } else {
                plr.sendMessage("<ADMIN> " + ChatColor.LIGHT_PURPLE + message);
            }
        }
        this.bot.sendNotice(sender, "Broadcasted your message");
        this.bot.sendMessage(this.plugin.normalChannel, "<ADMIN> " + message);
    }

    public void dotKickCommand(String partialname, String reason, String sender, String hostname) {

        Player target = null;
        try {
            target = J2MC_Manager.getVisibility().getPlayer(partialname, null);
        } catch (final BadPlayerMatchException e) {
            this.bot.sendNotice(sender, e.getMessage());
            return;
        }
        if (reason == null) {
            reason = "Kicked.";
        }
        target.kickPlayer("Kicked: " + reason);
        this.bot.sendNotice(sender, target.getName() + " kicked.");
        this.bot.sendMessage(this.plugin.normalChannel, target.getName() + " kicked (" + reason + ")");
        J2MC_Manager.getCore().adminAndLog(this.plugin.hosts.get(hostname) + "-irc kicked " + target.getName() + "(" + reason + ")");
        J2MC_Manager.getCore().messageNonAdmin(ChatColor.RED + target.getName() + " kicked (" + reason + ")");
    }

    public void dotUnbanCommand(String sender, String hostname, String whoToUnban) {
        final String adminName = this.plugin.hosts.get(hostname);
        final String toSend = adminName + ":" + whoToUnban + ":" + sender;
        final HashSet<String> targets = new HashSet<String>();
        targets.add("UNBAN");
        this.plugin.getServer().getPluginManager().callEvent(new MessageEvent(targets, toSend));
    }

    public boolean hasAdminPrivileges(String hostname) {
        return this.plugin.hosts.containsKey(hostname);
    }

    public void HasCommand(String player, String Channel) {
        for (final Player plr : J2MC_Manager.getVisibility().getOnlinePlayers(null)) {
            if (player.equalsIgnoreCase(plr.getName())) {
                this.bot.sendMessage(Channel, "I has " + player);
            }
        }
    }

    public void MeCommand(String sender, String message) {
        this.plugin.getServer().broadcastMessage(ChatColor.AQUA + "(IRC) *" + sender + " " + message);
        this.bot.sendMessage(this.plugin.normalChannel, "[IRC] *" + sender + " " + message);
    }

    public void msgCommand(String sender, String message) {
        final String toSendIRC = "[IRC] <" + sender + "> " + message;
        final String toSend = "(IRC) <" + ChatColor.AQUA + sender + ChatColor.WHITE + "> " + message;
        this.plugin.getServer().broadcastMessage(toSend);
        this.bot.sendMessage(this.plugin.normalChannel, toSendIRC);
    }

    public void PlayerListCommandInPrivate(String channel) {
        String toSend;
        toSend = "Players (" + this.plugin.getServer().getOnlinePlayers().length + " of " + this.plugin.getServer().getMaxPlayers() + "): ";
        final StringBuilder builder = new StringBuilder();
        builder.append(toSend);
        if (this.plugin.getServer().getOnlinePlayers().length != 0) {
            for (final Player plr : this.plugin.getServer().getOnlinePlayers()) {
                String name = plr.getName();
                if (name.equals("untamed")) {
                    name = "untaemd";
                }
                if (J2MC_Manager.getVisibility().isVanished(plr)) {
                    builder.append(name + "[V], ");
                } else {
                    builder.append(name + ", ");
                }
            }
            toSend = builder.toString();
            toSend = toSend.substring(0, toSend.length() - 2);
        } else {
            toSend = toSend + "No one is online :(";
        }
        this.bot.sendMessage(channel, toSend);
    }

    public void PlayerListCommandInPublic(String channel) {
        int players = 0;
        for (final Player plr : this.plugin.getServer().getOnlinePlayers()) {
            if (!J2MC_Manager.getVisibility().isVanished(plr)) {
                players++;
            }
        }
        String toSend = "Players (" + players + " of " + this.plugin.getServer().getMaxPlayers() + "): ";
        final StringBuilder builder = new StringBuilder();
        builder.append(toSend);
        if (players != 0) {
            for (final Player plr : this.plugin.getServer().getOnlinePlayers()) {
                if (!J2MC_Manager.getVisibility().isVanished(plr)) {
                    String name = plr.getName();
                    if (name.equals("untamed")) {
                        name = "untaemd";
                    }
                    builder.append(name + ", ");
                }
            }
            toSend = builder.toString();
            toSend = toSend.substring(0, toSend.length() - 2);
        } else {
            toSend = toSend + "No one is online :(";
        }
        this.bot.sendMessage(channel, toSend);
    }

    public void PlayersCommandInPrivate(String channel) {
        int players = 0;
        int vanished = 0;
        for (final Player plr : this.plugin.getServer().getOnlinePlayers()) {
            if (J2MC_Manager.getVisibility().isVanished(plr)) {
                vanished++;
            }
            players++;
        }
        String toSend;
        if (vanished == 1) {
            toSend = "Currently " + players + " out of " + this.plugin.getServer().getMaxPlayers() + " on the server. 1 player invisible.";
        } else {
            toSend = "Currently " + players + " out of " + this.plugin.getServer().getMaxPlayers() + " on the server. " + vanished + " players invisible";
        }
        this.bot.sendMessage(channel, toSend);
    }

    public void PlayersCommandInPublic(String channel) {
        int players = 0;
        for (final Player plr : this.plugin.getServer().getOnlinePlayers()) {
            if (!J2MC_Manager.getVisibility().isVanished(plr)) {
                players++;
            }
        }
        final String toSend = "Currently " + players + " out of " + this.plugin.getServer().getMaxPlayers() + " on the server";
        this.bot.sendMessage(channel, toSend);
    }

}
