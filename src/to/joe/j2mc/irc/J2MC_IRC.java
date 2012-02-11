package to.joe.j2mc.irc;

import org.bukkit.plugin.java.JavaPlugin;

public class J2MC_IRC extends JavaPlugin{

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
		
		this.ServerHost = this.getConfig().getString("server.host");
		this.ServerPort = this.getConfig().getInt("server.port");
		this.nick = this.getConfig().getString("server.nick");
		this.bindToIP = this.getConfig().getBoolean("server.bindtoip");
		this.BindIP = this.getConfig().getString("server.bindip");
		
		this.NormalChannel = this.getConfig().getString("channels.general");
		this.AdminChannel = this.getConfig().getString("channels.admin");
		
		this.AuthservUsername = this.getConfig().getString("authserv.username");
		this.AuthservPassword = this.getConfig().getString("authserv.password");
		
		this.getLogger().info("IRC module enabled");
	}
	
	public void onDisable(){
		this.getLogger().info("IRC module disabled");
	}
	
}
