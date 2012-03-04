package to.joe.j2mc.irc;

import java.util.HashSet;

import org.bukkit.ChatColor;
import org.jibble.pircbot.PircBot;

import to.joe.j2mc.core.J2MC_Core;
import to.joe.j2mc.core.event.MessageEvent;

public class IRCBot extends PircBot {

    J2MC_IRC plugin;
    IRCcommands commands;

    public IRCBot(String nick, J2MC_IRC j2mc_irc) {
        this.setName(nick);
        this.setAutoNickChange(true);
        this.setMessageDelay(1100);
        this.plugin = j2mc_irc;
        this.commands = new IRCcommands(this, plugin);
    }
    
    public void onInvite(String targetNick, String sourceNick, String sourceLogin, String sourceHostname, String channel){
        if(targetNick.equalsIgnoreCase(this.getNick())){
            if(sourceNick.equalsIgnoreCase("chanserv")){
                this.joinChannel(channel);
            }
        }
    }

    // COmmands begin here.
    public void onMessage(String channel, String sender, String login, String hostname, String message) {
        // All channel commands.
        String[] MessageArray = message.split(" ");

        // Public channel commands.
        if (channel.equalsIgnoreCase(plugin.NormalChannel)) {
            // !msg command
            if (MessageArray[0].equalsIgnoreCase("!msg")) {
                String toSend = message.substring(5);
                String toSendIRC = "[IRC] <" + sender + "> " + toSend;
                toSend = "(IRC) <" + ChatColor.AQUA + sender + ChatColor.WHITE + "> " + toSend;
                plugin.getServer().broadcastMessage(toSend);
                this.sendMessage(channel, toSendIRC);
            }

            // .kick command
            if (MessageArray[0].equalsIgnoreCase(".kick")) {
                if (commands.hasAdminPrivelages(hostname)) {
                    String[] args = message.substring(6).split(" ");
                    String partialplayer = args[0];
                    String reason = J2MC_Core.combineSplit(1, args, " ");
                    commands.dotKickCommand(partialplayer, reason, sender, hostname);
                }
            }

            // .g command
            if (MessageArray[0].equalsIgnoreCase(".g")) {
                if (commands.hasAdminPrivelages(hostname)) {
                    String gmessage = message.substring(3);
                    commands.dotGCommand(hostname, gmessage, sender);
                }
            }

            // !admins command
            if (MessageArray[0].equalsIgnoreCase("!admins")) {
                commands.AdminsCommandinPublic(channel);
            }

            // .a command
            if (MessageArray[0].equalsIgnoreCase(".a")) {
                if (commands.hasAdminPrivelages(hostname)) {
                    String Derp = message.substring(3);
                    commands.dotAcommand(hostname, Derp);
                }
            }
            
            // !players command
            if (message.toLowerCase().equalsIgnoreCase("!players")) {
                commands.PlayersCommandInPublic(channel);
            }
            
            // !playerlist command
            if (message.toLowerCase().equalsIgnoreCase("!playerlist")) {
                commands.PlayerListCommandInPublic(channel);
            }
            
            // addban command
            if (message.toLowerCase().startsWith(".addban")){
                if(plugin.isBansEnabled){
                    if (commands.hasAdminPrivelages(hostname)){
                        commands.dotAddBanCommand(sender, hostname, message);
                    }
                }else{
                    this.sendNotice(sender, "Bans module isn't enabled on the server, no addban.");
                }
            }
            // ban command
            if (message.toLowerCase().startsWith(".ban")){
                if (plugin.isBansEnabled){
                    if (commands.hasAdminPrivelages(hostname)){
                        commands.dotBanCommand(sender, hostname, message);
                    }
                }else{
                    this.sendNotice(sender, "Bans module isn't enabled on the server, no addban.");
                }
            }
        }

        // Admin chanel commands.
        if (channel.equalsIgnoreCase(plugin.AdminChannel)) {
            // !has command
            if (MessageArray[0].equalsIgnoreCase("!has")) {
                String player = MessageArray[1];
                commands.HasCommand(player, channel);
            }

            // !admins command
            if (MessageArray[0].equalsIgnoreCase("!admins")) {
                commands.AdminsCommandinPrivate(channel);
            }

            // !reports command
            if (message.equalsIgnoreCase("!reports")) {
                HashSet<String> targets = new HashSet<String>();
                targets.add("ReportCall");
                plugin.getServer().getPluginManager().callEvent(new MessageEvent(targets, "Request for reports. Message useless :3"));
            }
            
            //!playerlist command
            if (message.equalsIgnoreCase("!playerlist")){
                commands.PlayerListCommandInPrivate(channel);
            }
            
            //!players command
            if (message.equalsIgnoreCase("!players")){
                commands.PlayersCommandInPrivate(channel);
            }
        }

    }
    
    @Override
    protected void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target, String notice){
        if((sourceNick.equalsIgnoreCase("authserv") || sourceNick.equalsIgnoreCase("authServ@services.gamesurge.net")) && target.equalsIgnoreCase(this.getNick())){
            plugin.getLogger().info("Authserv said this to me: " + notice);
            if(notice.contains("Your hostmask is not valid for account ")){
                this.sendMessage("AuthServ", "authcookie " + plugin.AuthservUsername);
                plugin.getLogger().info("I detected this was an auth cookie request and automatically sent a message to authserv for a cookie. Use /ircmessage to reply to authserv");
            }
        }
    }

    @Override
    protected void onPrivateMessage(String sender, String login, String hostname, String message) {
        if(sender.equalsIgnoreCase("authserv") || sender.equalsIgnoreCase("authServ@services.gamesurge.net")){
            plugin.getLogger().info("Authserv said this to me: " + message);
            if(message.contains("Your hostmask is not valid for account")){
                this.sendMessage("AuthServ", "authcookie " + plugin.AuthservUsername);
                plugin.getLogger().info("I detected this was an auth cookie request and automatically sent a message to authserv for a cookie. Use /ircmessage to reply to authserv");
            }
        }
        String[] MessageArray = message.split(" ");
        // playerlist command
        if (message.toLowerCase().equalsIgnoreCase("playerlist")) {
            if(commands.hasAdminPrivelages(hostname)){
                commands.PlayerListCommandInPrivate(sender);
            }else{
                commands.PlayerListCommandInPublic(sender);
            }
        }
        // players command
        if (message.toLowerCase().equalsIgnoreCase("players")) {
            if(commands.hasAdminPrivelages(hostname)){
                commands.PlayersCommandInPrivate(sender);
            }else{
                commands.PlayersCommandInPublic(sender);
            }
        }
        // !msg command
        if (MessageArray[0].equalsIgnoreCase("msg")) {
            String toSend = message.substring(4);
            String toSendIRC = "[IRC] <" + sender + "> " + toSend;
            toSend = "(IRC) <" + ChatColor.AQUA + sender + ChatColor.WHITE + "> " + toSend;
            plugin.getServer().broadcastMessage(toSend);
            this.sendMessage(plugin.NormalChannel, toSendIRC);
        }
        // kick command
        if (MessageArray[0].equalsIgnoreCase("kick")) {
            if (commands.hasAdminPrivelages(hostname)) {
                String[] args = message.substring(5).split(" ");
                String partialplayer = args[0];
                String reason = J2MC_Core.combineSplit(1, args, " ");
                commands.dotKickCommand(partialplayer, reason, sender, hostname);
            }
        }
        // g command
        if (MessageArray[0].equalsIgnoreCase("g")) {
            if (commands.hasAdminPrivelages(hostname)) {
                String gmessage = message.substring(2);
                commands.dotGCommand(hostname, gmessage, sender);
            }
        }
        // a command
        if (MessageArray[0].equalsIgnoreCase("a")) {
            if (commands.hasAdminPrivelages(hostname)) {
                String Derp = message.substring(2);
                commands.dotAcommand(hostname, Derp);
            }
        }
        // admins command
        if (MessageArray[0].equalsIgnoreCase("admins")) {
            if (commands.hasAdminPrivelages(hostname)) {
                commands.AdminsCommandinPrivate(sender);
            }else{
                commands.AdminsCommandinPublic(sender);
            }
        }
        // addban command
        if (message.toLowerCase().startsWith("addban")){
            if(plugin.isBansEnabled){
                if (commands.hasAdminPrivelages(hostname)){
                    commands.dotAddBanCommand(sender, hostname, message);
                }
            }else{
                this.sendMessage(sender, "Bans module isn't enabled on the server, no addban.");
            }
        }
        // ban command
        if (message.toLowerCase().startsWith("ban")){
            if (plugin.isBansEnabled){
                if (commands.hasAdminPrivelages(hostname)){
                    commands.dotBanCommand(sender, hostname, message);
                }
            }else{
                this.sendMessage(sender, "Bans module isn't enabled on the server, no addban.");
            }
        }
    }
}
