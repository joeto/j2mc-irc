package to.joe.j2mc.irc;

import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import to.joe.j2mc.core.J2MC_Core;
import to.joe.j2mc.core.J2MC_Manager;
import to.joe.j2mc.core.event.MessageEvent;
import to.joe.j2mc.core.exceptions.BadPlayerMatchException;

public class IRCcommands {
    
    J2MC_IRC plugin;
    IRCBot bot;
    
    public IRCcommands(IRCBot bot, J2MC_IRC IRC){
        this.plugin = IRC;
        this.bot = bot;
    }
    
    public boolean hasAdminPrivelages(String hostname){
        return plugin.hosts.containsKey(hostname);
    }
    
    public void dotKickCommand(String partialname, String reason, String sender, String hostname){
 
        Player target = null;
        try{
            target = J2MC_Manager.getVisibility().getPlayer(partialname, null);
        }catch(BadPlayerMatchException e){
            bot.sendNotice(sender, e.getMessage());
            return;
        }
        if(reason == null){
            reason = "Kicked.";
        }
        target.kickPlayer("Kicked: " + reason);
        bot.sendNotice(sender, target.getName() + " kicked.");
        bot.sendMessage(plugin.NormalChannel, target.getName() + " kicked (" + reason + ")");
        J2MC_Manager.getCore().adminAndLog(plugin.hosts.get(hostname) + "-irc kicked " + target.getName() + "(" + reason + ")");
        J2MC_Manager.getCore().messageNonAdmin(ChatColor.RED + target.getName() + " kicked (" + reason + ")");
    }
    
    public void dotGCommand(String hostname, String message, String sender){
        for (final Player plr : J2MC_Manager.getVisibility().getOnlinePlayers(null)) {
            if (plr.hasPermission("j2mc.adminToolKit.admin")) {
                plr.sendMessage("<" + plugin.hosts.get(hostname) + "-irc> " + ChatColor.LIGHT_PURPLE + message);
            } else {
                plr.sendMessage("<ADMIN> " + ChatColor.LIGHT_PURPLE + message);
            }
        }
        bot.sendNotice(sender, "Broadcasted your message");
        bot.sendMessage(plugin.NormalChannel, "<ADMIN> " + message);
    }
    
    public void PlayerListCommandInPrivate(String channel){
        String toSend;
        toSend = "Players (" + plugin.getServer().getOnlinePlayers().length + " of " + plugin.getServer().getMaxPlayers() + "): ";
        final StringBuilder builder = new StringBuilder();
        builder.append(toSend);
        if(plugin.getServer().getOnlinePlayers().length != 0){
            for(Player plr : plugin.getServer().getOnlinePlayers()){
                if(J2MC_Manager.getVisibility().isVanished(plr)){
                    builder.append(plr.getName() + "[V], ");
                }else{
                    builder.append(plr.getName() + ", ");
                }
            }
            toSend = builder.toString();
            toSend = toSend.substring(0, toSend.length() - 2);
        }else{
            toSend = toSend + "No one is online :(";
        }
        bot.sendMessage(channel, toSend);
    }
    
    public void PlayerListCommandInPublic(String channel){
        int players = 0;
        for(Player plr : plugin.getServer().getOnlinePlayers()){
            if(!J2MC_Manager.getVisibility().isVanished(plr)){
                players++;
            }
        }
        String toSend = "Players (" + players + " of " + plugin.getServer().getMaxPlayers() + "): ";
        final StringBuilder builder = new StringBuilder();
        builder.append(toSend);
        if(players != 0){
            for(Player plr : plugin.getServer().getOnlinePlayers()){
                if(!J2MC_Manager.getVisibility().isVanished(plr)){
                    builder.append(plr.getName() + ", ");
                }
            }
            toSend = builder.toString();
            toSend = toSend.substring(0, toSend.length() - 2);
        }else{
            toSend = toSend + "No one is online :(";
        }
        bot.sendMessage(channel, toSend);
    }
    
    public void HasCommand(String player, String Channel){
        for(Player plr : J2MC_Manager.getVisibility().getOnlinePlayers(null)){
            if(player.equalsIgnoreCase(plr.getName())){
                bot.sendMessage(Channel, "I has " + player);
            }
        }
    }
    
    public void PlayersCommandInPrivate(String channel){
        int players = 0;
        int vanished = 0;
        for(Player plr : plugin.getServer().getOnlinePlayers()){
            if(J2MC_Manager.getVisibility().isVanished(plr)){
                vanished++;
            }
            players++;
        }
        String toSend;
        if(vanished == 1){
            toSend = "Currently " + players + " out of " + plugin.getServer().getMaxPlayers() + " on the server. " + vanished + " player is vanished";
        }
        toSend = "Currently " + players + " out of " + plugin.getServer().getMaxPlayers() + " on the server. " + vanished + " players are vanished";
        bot.sendMessage(channel, toSend);
    }
    
    public void PlayersCommandInPublic(String channel){
        int players = 0;
        for(Player plr : plugin.getServer().getOnlinePlayers()){
            if(!J2MC_Manager.getVisibility().isVanished(plr)){
                players++;
            }
        }
        String toSend = "Currently " + players + " out of " + plugin.getServer().getMaxPlayers() + " on the server";
        bot.sendMessage(channel, toSend);
    }
    
    public void AdminsCommandinPrivate(String channel){
        int admins = 0;
        for(Player plr : plugin.getServer().getOnlinePlayers()){
            if(J2MC_Manager.getPermissions().isAdmin(plr.getName())){
                admins++;
            }
        }
        String toSend = " ";
        if(admins == 0){
            toSend = "No admins online";
        }else{
            toSend = "Admins: ";
            for(Player plr : plugin.getServer().getOnlinePlayers()){
                if(J2MC_Manager.getPermissions().isAdmin(plr.getName())){
                    toSend = toSend + plr.getName() + ", ";
                }
            }
            toSend = toSend.substring(0, toSend.length() - 2);
        }
        bot.sendMessage(channel, toSend);
    }
    
    public void AdminsCommandinPublic(String channel){
        boolean haveAdmins = false;
        for(Player plr : plugin.getServer().getOnlinePlayers()){
            if(J2MC_Manager.getPermissions().isAdmin(plr.getName())){
                haveAdmins = true;
            }
        }
        if(haveAdmins){
            bot.sendMessage(channel, "There are admins online");
        }else{
            bot.sendMessage(channel, "No admins online see #joe.to or #minecraft");
        }
    }
    
    public void dotAcommand(String hostname, String message){
        final String broadcastmessage = "<" + ChatColor.LIGHT_PURPLE + plugin.hosts.get(hostname) + ChatColor.WHITE + "> " + message;
        J2MC_Manager.getCore().adminAndLog(broadcastmessage);
    }
    
    public void dotBanCommand(String sender, String hostname, String message){
        String adminName = plugin.hosts.get(hostname);
        String[] split = message.split(" ");
        if(split.length < 3){
            bot.sendNotice(sender, "Usage: .ban <player> reason");
            return;
        }
        String target = split[1];
        target.replace("&", "*OMGROFLREPLACEMEIWITHAMPERSAND*").replace("=", "*OMGROFLREPLACEMEWITHEQUALS");
        String reason = J2MC_Core.combineSplit(2, split, " ");
        reason.replace("&", "*OMGROFLREPLACEMEIWITHAMPERSAND*").replace("=", "*OMGROFLREPLACEMEWITHEQUALS");
        String toSend = "admin=" + adminName + "&target=" + target + "&reason=" + reason;
        HashSet<String> targets = new HashSet<String>();
        targets.add("NEWBAN");
        plugin.getServer().getPluginManager().callEvent(new MessageEvent(targets, toSend));
    }
    
    public void dotAddBanCommand(String sender, String hostname, String message){
        String adminName = plugin.hosts.get(hostname);
        String[] split = message.split(" ");
        if(split.length < 3){
            bot.sendNotice(sender, "Usage: .addban <player> reason");
            return;
        }
        String target = split[1];
        target.replace("&", "*OMGROFLREPLACEMEIWITHAMPERSAND*").replace("=", "*OMGROFLREPLACEMEWITHEQUALS");
        String reason = J2MC_Core.combineSplit(2, split, " ");
        reason.replace("&", "*OMGROFLREPLACEMEIWITHAMPERSAND*").replace("=", "*OMGROFLREPLACEMEWITHEQUALS");
        String toSend = "admin=" + adminName + "&target=" + target + "&reason=" + reason;
        HashSet<String> targets = new HashSet<String>();
        targets.add("NEWADDBAN");
        plugin.getServer().getPluginManager().callEvent(new MessageEvent(targets, toSend));
    }

}
