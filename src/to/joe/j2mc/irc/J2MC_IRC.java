package to.joe.j2mc.irc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import to.joe.j2mc.core.J2MC_Manager;
import to.joe.j2mc.irc.commands.IRCMessageCommand;
import to.joe.j2mc.irc.commands.SmackIRCCommand;
import to.joe.j2mc.irc.threads.Queue;
import to.joe.j2mc.irc.threads.UptimeNagger;
import to.joe.j2mc.irc.threads.UptimeSetter;

public class J2MC_IRC extends JavaPlugin implements Listener {

    public Queue queue;
    Timer timer;
    public HashMap<String, String> hosts;
    public IRCManager ircManager;
    public String serverHost;
    public int serverPort;
    public String nick;
    public boolean bindToIP;
    public String bindIP;
    public String normalChannel;
    public String adminChannel;
    public String authservUsername;
    public String authservPassword;
    public boolean announceFlow;
    public boolean isBansEnabled = false;
    public List<String> adminGroups;
    public long lastUp;

    @Override
    public void onDisable() {
        this.timer.cancel();
        this.getServer().getScheduler().cancelTasks(this);
        this.ircManager.disconnect();
        this.getLogger().info("IRC module disabled");
    }

    @Override
    public void onEnable() {
        this.getConfig().options().copyDefaults(true);
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getServer().getPluginManager().registerEvents(new MessageListener(this), this);

        this.deployDefaultConfig();
        this.readData();

        this.getCommand("smackirc").setExecutor(new SmackIRCCommand(this));
        this.getCommand("ircmessage").setExecutor(new IRCMessageCommand(this));

        this.lastUp = 0L;
        this.ircManager = new IRCManager(this);
        this.ircManager.setDaemon(true);
        this.queue = new Queue(this);
        this.timer = new Timer();
        this.timer.schedule(this.queue, 10000, 10000);
        this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new UptimeSetter(this), 20L, 20L);
        this.timer.schedule(new UptimeNagger(this), 60000, 60000);
        this.ircManager.start();

        this.getLogger().info("IRC module enabled");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled=true)
    public void onMessage(AsyncPlayerChatEvent event) {
        final String message = "<" + event.getPlayer().getName() + "> " + event.getMessage();
        this.queue.sendMessage(message, this.normalChannel);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        if (this.announceFlow && (event.getJoinMessage() != null)) {
            this.queue.sendMessage(ChatColor.stripColor(event.getJoinMessage()), this.normalChannel);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(PlayerQuitEvent event) {
        if (this.announceFlow && (event.getQuitMessage() != null)) {
            this.queue.sendMessage(ChatColor.stripColor(event.getQuitMessage()), this.normalChannel);
        }
    }

    public void readData() {
        this.adminGroups = this.getConfig().getStringList("commandgroups");

        this.hosts = new HashMap<String, String>();
        for (final String group : this.adminGroups) {
            try {
                final PreparedStatement ps = J2MC_Manager.getMySQL().getFreshPreparedStatementHotFromTheOven("SELECT `name`,`IRChost` FROM users WHERE `IRChost` <> '' AND `group`=?");
                ps.setString(1, group);
                final ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    final String user = rs.getString("name");
                    final String host = rs.getString("IRChost");
                    this.hosts.put(host, user);
                }
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        }
        this.serverHost = this.getConfig().getString("server.host");
        this.serverPort = this.getConfig().getInt("server.port");
        this.nick = this.getConfig().getString("server.nick");
        this.bindToIP = this.getConfig().getBoolean("server.bindtoip");
        this.bindIP = this.getConfig().getString("server.bindip");

        this.normalChannel = this.getConfig().getString("channels.general");
        this.adminChannel = this.getConfig().getString("channels.admin");

        this.authservUsername = this.getConfig().getString("authserv.username");
        this.authservPassword = this.getConfig().getString("authserv.password");

        this.announceFlow = this.getConfig().getBoolean("announce_flow", false);

        if (this.getServer().getPluginManager().isPluginEnabled("Bans")) {
            this.isBansEnabled = true;
        }
    }

    public void deployDefaultConfig() {
        final File target = new File(this.getDataFolder(), "config.yml");
        final InputStream source = this.getResource("config.yml");
        if (!this.getDataFolder().exists()) {
            this.getDataFolder().mkdir();
        }
        try {
            if (!target.exists()) {
                final OutputStream output = new FileOutputStream(target);
                int len;
                final byte[] buffer = new byte[1024];
                while ((len = source.read(buffer)) > 0) {
                    output.write(buffer, 0, len);
                }
                output.close();
            }
            source.close();
        } catch (final Exception ex) {
            this.getLogger().warning("Exception while copying default config");
        }
    }

}
