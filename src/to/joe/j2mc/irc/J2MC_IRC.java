package to.joe.j2mc.irc;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import to.joe.j2mc.core.event.MessageEvent;

public class J2MC_IRC extends JavaPlugin implements Listener{

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
	
	public void onEnable(){
		this.getConfig().options().copyDefaults(true);
		this.getServer().getPluginManager().registerEvents(this, this);
		this.getServer().getPluginManager().registerEvents(new MesssageListener(this), this);
		
		this.ServerHost = this.getConfig().getString("server.host");
		this.ServerPort = this.getConfig().getInt("server.port");
		this.nick = this.getConfig().getString("server.nick");
		this.bindToIP = this.getConfig().getBoolean("server.bindtoip");
		this.BindIP = this.getConfig().getString("server.bindip");
		
		this.NormalChannel = this.getConfig().getString("channels.general");
		this.AdminChannel = this.getConfig().getString("channels.admin");
		
		this.AuthservUsername = this.getConfig().getString("authserv.username");
		this.AuthservPassword = this.getConfig().getString("authserv.password");
		
		this.IRCManager = new IRCManager(this);
		IRCManager.connect();
		
		this.getLogger().info("IRC module enabled");
	}
	
	public void onDisable(){
		this.getLogger().info("IRC module disabled");
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onMessage(PlayerChatEvent event){
		if(!event.isCancelled()){
			String message = "<" + event.getPlayer().getName() + "> " + event.getMessage();
			IRCManager.sendMessage(message, false);
		}
	}
	
}
