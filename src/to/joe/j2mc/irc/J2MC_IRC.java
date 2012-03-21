package to.joe.j2mc.irc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Timer;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import to.joe.j2mc.core.J2MC_Manager;
import to.joe.j2mc.core.event.MessageEvent;
import to.joe.j2mc.irc.commands.IRCMessageCommand;
import to.joe.j2mc.irc.commands.SmackIRCCommand;

public class J2MC_IRC extends JavaPlugin implements Listener{

    public Queue queue;
    Timer timer;
    public HashMap<String, String> hosts;
	public IRCManager IRCManager;
	public String ServerHost;
	public int ServerPort;
	public String nick;
	public boolean bindToIP;
	public String BindIP;
	public String NormalChannel;
	public String AdminChannel;
	public String AuthservUsername;
	public String AuthservPassword;
	public boolean isBansEnabled = false;
	
	public void onEnable(){
		this.getConfig().options().copyDefaults(true);
		this.getServer().getPluginManager().registerEvents(this, this);
		this.getServer().getPluginManager().registerEvents(new MesssageListener(this), this);
		this.readData();
		this.getCommand("smackirc").setExecutor(new SmackIRCCommand(this));
		this.getCommand("ircmessage").setExecutor(new IRCMessageCommand(this));
		this.IRCManager = new IRCManager(this);
		this.queue = new Queue(this);
		timer = new Timer();
		timer.schedule(queue, 10000, 10000);
		IRCManager.start();
		IRCManager.connect();
		this.getLogger().info("IRC module enabled");
	}
	
	public void onDisable(){
	    IRCManager.disconnect();
		this.getLogger().info("IRC module disabled");
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onMessage(PlayerChatEvent event){
		if(!event.isCancelled()){
			String message = "<" + event.getPlayer().getName() + "> " + event.getMessage();
			queue.sendMessage(message, NormalChannel);
			if(event.getMessage().toLowerCase().contains("fag") || event.getMessage().toLowerCase().contains("nigg")){
			    IRCManager.sendMessage("Watch " + event.getPlayer().getName() + " for language: " + event.getMessage(), true);
			}
			if(event.getMessage().contains("##___#######") || event.getMessage().contains("-_-_-_-_-_-_-_,------,")){
			    if(isBansEnabled){
			        String toSend = "BobTheHAXXXXXXguy:" + event.getPlayer().getName() + ":Using nyancraft spam hacks:ammar2";
			        HashSet<String> targets = new HashSet<String>();
			        targets.add("NEWADDBAN");
			        this.getServer().getPluginManager().callEvent(new MessageEvent(targets, toSend));
			        IRCManager.sendMessage("Banned " + event.getPlayer().getName() + " for using nyancraft, please verify this was not a false positive", true);
			    }else{
			        IRCManager.sendMessage("Check out " + event.getPlayer().getName() + ", he is probably using spam hacks (nyancraft)", true);
			    }
			}
		}
	}
	
	public void readData(){
	    hosts = new HashMap<String, String>();
	    try{
	        PreparedStatement ps = J2MC_Manager.getMySQL().getFreshPreparedStatementHotFromTheOven("SELECT `name`,`IRChost` FROM users WHERE `IRChost` <> '' AND `group`='admin' OR `group`='srstaff' ");
	        ResultSet rs = ps.executeQuery();
	        while(rs.next()){
	            String user = rs.getString("name");
	            String host = rs.getString("IRChost");
	            hosts.put(host, user);
	        }
	    }catch(SQLException e){
	        e.printStackTrace();
	    } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
	    this.ServerHost = this.getConfig().getString("server.host");
	    this.ServerPort = this.getConfig().getInt("server.port");
	    this.nick = this.getConfig().getString("server.nick");
	    this.bindToIP = this.getConfig().getBoolean("server.bindtoip");
	    this.BindIP = this.getConfig().getString("server.bindip");
	      
	    this.NormalChannel = this.getConfig().getString("channels.general");
	    this.AdminChannel = this.getConfig().getString("channels.admin");
	        
	    this.AuthservUsername = this.getConfig().getString("authserv.username");
	    this.AuthservPassword = this.getConfig().getString("authserv.password");
	    
	    if(this.getServer().getPluginManager().isPluginEnabled("J2MC_Bans")){
	        this.isBansEnabled = true;
	    }
	}
	
}
