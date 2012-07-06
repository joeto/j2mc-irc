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
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import to.joe.j2mc.core.J2MC_Manager;
import to.joe.j2mc.core.event.MessageEvent;
import to.joe.j2mc.irc.commands.IRCMessageCommand;
import to.joe.j2mc.irc.commands.SmackIRCCommand;
import to.joe.j2mc.irc.threads.Queue;
import to.joe.j2mc.irc.threads.UptimeNagger;
import to.joe.j2mc.irc.threads.UptimeSetter;

public class J2MC_IRC extends JavaPlugin implements Listener {

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
    public List<String> AdminGroups;
    public long lastUp;

    @Override
    public void onDisable() {
        this.IRCManager.disconnect();
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
        this.IRCManager = new IRCManager(this);
        this.queue = new Queue(this);
        this.timer = new Timer();
        this.timer.schedule(this.queue, 10000, 10000);
        this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new UptimeSetter(this), 20L, 20L);
        this.timer.schedule(new UptimeNagger(this), 60000, 60000);
        this.IRCManager.start();
        this.IRCManager.connect();

        this.getLogger().info("IRC module enabled");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMessage(PlayerChatEvent event) {
        if (!event.isCancelled()) {
            final String message = "<" + event.getPlayer().getName() + "> " + event.getMessage();
            this.queue.sendMessage(message, this.NormalChannel);
            if (event.getMessage().toLowerCase().contains("fag") || event.getMessage().toLowerCase().contains("nigg")) {
                this.IRCManager.sendMessage("Watch " + event.getPlayer().getName() + " for language: " + event.getMessage(), true);
            }
            if (event.getMessage().contains("_____##___##") || event.getMessage().contains("_-_-_-_-_-_-_-''    ''") || event.getMessage().contains("-_-_-_-_-_-_-_,------,") || event.getMessage().contains("##___#######")) {
                if (this.isBansEnabled) {
                    final String toSend = "BobTheHAXXXXXXguy:" + event.getPlayer().getName() + ":spam hacks:ammar2";
                    this.getServer().getPluginManager().callEvent(new MessageEvent(MessageEvent.compile("NEWADDBAN"), toSend));
                    this.IRCManager.sendMessage("Banned " + event.getPlayer().getName() + " for using nyancraft", true);
                } else {
                    this.IRCManager.sendMessage("Check out " + event.getPlayer().getName() + ", he is probably using spam hacks (nyancraft)", true);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSignChange(SignChangeEvent event) {
        String bad = null;
        for (final String line : event.getLines()) {
            if (line.toLowerCase().contains("fag") || (line.toLowerCase().contains("nigg"))) {
                bad = line;
                break;
            }
        }
        if (bad!=null) {
            final Location loc = event.getBlock().getLocation();
            final String msg = event.getPlayer().getName() + " created a bad sign @ X" + loc.getBlockX() + " Y" + loc.getBlockY() + " Z" + loc.getBlockZ() + "! Line: \"" + bad + "\"";
            this.IRCManager.sendMessage(msg, true);
            J2MC_Manager.getCore().adminAndLog(ChatColor.RED + msg);
        }
    }

    public void readData() {
        this.AdminGroups = this.getConfig().getStringList("commandgroups");

        this.hosts = new HashMap<String, String>();
        for (final String group : this.AdminGroups) {
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
            } catch (final ClassNotFoundException e) {
                e.printStackTrace();
            }
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
