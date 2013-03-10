package to.joe.j2mc.irc;

import java.util.HashSet;

import org.bukkit.ChatColor;
import org.jibble.pircbot.PircBot;

import to.joe.j2mc.core.J2MC_Core;
import to.joe.j2mc.core.J2MC_Manager;
import to.joe.j2mc.core.event.MessageEvent;

public class IRCBot extends PircBot {

    private J2MC_IRC plugin;
    private IRCCommands commands;
    private IRCManager manager;

    public IRCBot(String nick, J2MC_IRC j2mc_irc, IRCManager manager) {
        this.setName(nick);
        this.setAutoNickChange(true);
        this.setMessageDelay(1100);
        this.setLogin("j2mc" + J2MC_Manager.getServerID());
        this.setVersion("J2MC v" + j2mc_irc.getDescription().getVersion());
        this.setFinger("Oh my.");
        this.plugin = j2mc_irc;
        this.manager = manager;
        this.commands = new IRCCommands(this, this.plugin);
    }

    @Override
    public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason) {
        if (sourceNick.equalsIgnoreCase(this.getNick())) {
            this.plugin.getLogger().info("Quit IRC for reason: " + reason);
        }
    }

    @Override
    public void onDisconnect() {
        this.manager.connect();
    }

    @Override
    public void onInvite(String targetNick, String sourceNick, String sourceLogin, String sourceHostname, String channel) {
        this.plugin.getLogger().info(targetNick + " got invited to channel " + channel + " by " + sourceNick);
        if (sourceNick.equalsIgnoreCase("chanserv") && targetNick.equalsIgnoreCase(this.getNick())) {
            this.plugin.getLogger().info("Detected this was an invite from chanserv, so I'm going to join the channel");
            this.joinChannel(this.plugin.adminChannel);
            this.joinChannel(this.plugin.normalChannel);
        }
    }

    // COmmands begin here.
    @Override
    public void onMessage(String channel, String sender, String login, String hostname, String message) {
        // All channel commands.
        final String[] MessageArray = message.split(" ");

        // Public channel commands.
        if (channel.equalsIgnoreCase(this.plugin.normalChannel)) {
            // !msg command
            if (MessageArray[0].equalsIgnoreCase("!msg")) {
                final String toSend = message.substring(5);
                this.commands.msgCommand(sender, toSend);
            }

            //!me command
            if (message.toLowerCase().startsWith("!me")) {
                final String toSend = message.substring(4);
                this.commands.MeCommand(sender, toSend);
            }

            // .kick command
            if (MessageArray[0].equalsIgnoreCase(".kick")) {
                if (this.commands.hasAdminPrivileges(hostname)) {
                    final String[] args = message.substring(6).split(" ");
                    final String partialplayer = args[0];
                    final String reason = J2MC_Core.combineSplit(1, args, " ");
                    this.commands.dotKickCommand(partialplayer, reason, sender, hostname);
                }
            }

            // .g command
            if (MessageArray[0].equalsIgnoreCase(".g")) {
                if (this.commands.hasAdminPrivileges(hostname)) {
                    final String gmessage = message.substring(3);
                    this.commands.dotGCommand(hostname, gmessage, sender);
                }
            }

            // !admins command
            if (MessageArray[0].equalsIgnoreCase("!admins")) {
                this.commands.AdminsCommandinPublic(channel);
            }

            // .a command
            if (MessageArray[0].equalsIgnoreCase(".a")) {
                if (this.commands.hasAdminPrivileges(hostname)) {
                    final String Derp = message.substring(3);
                    this.commands.dotAcommand(hostname, Derp, sender);
                }
            }

            // !players command
            if (message.toLowerCase().equalsIgnoreCase("!players")) {
                this.commands.PlayersCommandInPublic(channel);
            }

            // !playerlist command
            if (message.toLowerCase().equalsIgnoreCase("!playerlist")) {
                this.commands.PlayerListCommandInPublic(channel);
            }

            // .addban command
            if (message.toLowerCase().startsWith(".addban")) {
                if (this.plugin.isBansEnabled) {
                    if (this.commands.hasAdminPrivileges(hostname)) {
                        this.commands.dotAddBanCommand(sender, hostname, message);
                    }
                } else {
                    this.sendNotice(sender, "Bans module isn't enabled on the server, no addban.");
                }
            }
            // .ban command
            if (message.toLowerCase().startsWith(".ban")) {
                if (this.plugin.isBansEnabled) {
                    if (this.commands.hasAdminPrivileges(hostname)) {
                        this.commands.dotBanCommand(sender, hostname, message);
                    }
                } else {
                    this.sendNotice(sender, "Bans module isn't enabled on the server, no addban.");
                }
            }
            // .unban command
            if (message.toLowerCase().startsWith(".unban")) {
                if (this.plugin.isBansEnabled) {
                    if (this.commands.hasAdminPrivileges(hostname)) {
                        final String whoToUnban = message.substring(7);
                        this.commands.dotUnbanCommand(sender, hostname, whoToUnban);
                    }
                }
            }
        }

        // Admin chanel commands.
        if (channel.equalsIgnoreCase(this.plugin.adminChannel)) {
            // !has command
            if (MessageArray[0].equalsIgnoreCase("!has")) {
                final String player = MessageArray[1];
                this.commands.HasCommand(player, channel);
            }

            // !admins command
            if (MessageArray[0].equalsIgnoreCase("!admins")) {
                this.commands.AdminsCommandinPrivate(channel);
            }

            // !reports command
            if (message.equalsIgnoreCase("!reports")) {
                final HashSet<String> targets = new HashSet<String>();
                targets.add("ReportCall");
                this.plugin.getServer().getPluginManager().callEvent(new MessageEvent(targets, "Request for reports. Message useless :3"));
            }

            //!playerlist command
            if (message.equalsIgnoreCase("!playerlist")) {
                this.commands.PlayerListCommandInPrivate(channel);
            }

            //!players command
            if (message.equalsIgnoreCase("!players")) {
                this.commands.PlayersCommandInPrivate(channel);
            }
        }

    }

    @Override
    protected void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target, String notice) {
        if ((sourceNick.equalsIgnoreCase("authserv") || sourceNick.equalsIgnoreCase("authServ@services.gamesurge.net")) && target.equalsIgnoreCase(this.getNick())) {
            this.plugin.getLogger().info("Authserv said this to me: " + notice);
            if (notice.contains("Your hostmask is not valid for account ")) {
                this.sendMessage("AuthServ", "authcookie " + this.plugin.authservUsername);
                this.plugin.getLogger().info("I detected this was an auth cookie request and automatically sent a message to authserv for a cookie. Use /ircmessage to reply to authserv");
            }
        }
    }

    @Override
    protected void onPrivateMessage(String sender, String login, String hostname, String message) {
        if (sender.equalsIgnoreCase("authserv") || sender.equalsIgnoreCase("authServ@services.gamesurge.net")) {
            this.plugin.getLogger().info("Authserv said this to me: " + message);
            if (message.contains("Your hostmask is not valid for account")) {
                this.sendMessage("AuthServ", "authcookie " + this.plugin.authservUsername);
                this.plugin.getLogger().info("I detected this was an auth cookie request and automatically sent a message to authserv for a cookie. Use /ircmessage to reply to authserv");
            }
        }
        final String[] MessageArray = message.split(" ");
        // playerlist command
        if (message.toLowerCase().equalsIgnoreCase("playerlist")) {
            if (this.commands.hasAdminPrivileges(hostname)) {
                this.commands.PlayerListCommandInPrivate(sender);
            } else {
                this.commands.PlayerListCommandInPublic(sender);
            }
        }
        // players command
        if (message.toLowerCase().equalsIgnoreCase("players")) {
            if (this.commands.hasAdminPrivileges(hostname)) {
                this.commands.PlayersCommandInPrivate(sender);
            } else {
                this.commands.PlayersCommandInPublic(sender);
            }
        }
        // !msg command
        if (MessageArray[0].equalsIgnoreCase("msg")) {
            String toSend = message.substring(4);
            final String toSendIRC = "[IRC] <" + sender + "> " + toSend;
            toSend = "(IRC) <" + ChatColor.AQUA + sender + ChatColor.WHITE + "> " + toSend;
            this.plugin.getServer().broadcastMessage(toSend);
            this.sendMessage(this.plugin.normalChannel, toSendIRC);
        }
        // kick command
        if (MessageArray[0].equalsIgnoreCase("kick")) {
            if (this.commands.hasAdminPrivileges(hostname)) {
                final String[] args = message.substring(5).split(" ");
                final String partialplayer = args[0];
                final String reason = J2MC_Core.combineSplit(1, args, " ");
                this.commands.dotKickCommand(partialplayer, reason, sender, hostname);
            }
        }
        // g command
        if (MessageArray[0].equalsIgnoreCase("g")) {
            if (this.commands.hasAdminPrivileges(hostname)) {
                final String gmessage = message.substring(2);
                this.commands.dotGCommand(hostname, gmessage, sender);
            }
        }
        // a command
        if (MessageArray[0].equalsIgnoreCase("a")) {
            if (this.commands.hasAdminPrivileges(hostname)) {
                final String Derp = message.substring(2);
                this.commands.dotAcommand(hostname, Derp, sender);
            }
        }
        // admins command
        if (MessageArray[0].equalsIgnoreCase("admins")) {
            if (this.commands.hasAdminPrivileges(hostname)) {
                this.commands.AdminsCommandinPrivate(sender);
            } else {
                this.commands.AdminsCommandinPublic(sender);
            }
        }
        // addban command
        if (message.toLowerCase().startsWith("addban")) {
            if (this.plugin.isBansEnabled) {
                if (this.commands.hasAdminPrivileges(hostname)) {
                    this.commands.dotAddBanCommand(sender, hostname, message);
                }
            } else {
                this.sendMessage(sender, "Bans module isn't enabled on the server, no addban.");
            }
        }
        // ban command
        if (message.toLowerCase().startsWith("ban")) {
            if (this.plugin.isBansEnabled) {
                if (this.commands.hasAdminPrivileges(hostname)) {
                    this.commands.dotBanCommand(sender, hostname, message);
                }
            } else {
                this.sendMessage(sender, "Bans module isn't enabled on the server, no addban.");
            }
        }
        // unban command
        if (message.toLowerCase().startsWith("unban")) {
            if (this.plugin.isBansEnabled) {
                if (this.commands.hasAdminPrivileges(hostname)) {
                    final String whoToUnban = message.substring(6);
                    this.commands.dotUnbanCommand(sender, hostname, whoToUnban);
                }
            }
        }
    }
}
