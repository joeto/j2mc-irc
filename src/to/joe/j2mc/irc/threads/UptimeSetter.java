package to.joe.j2mc.irc.threads;

import to.joe.j2mc.irc.J2MC_IRC;

public class UptimeSetter implements Runnable {

    J2MC_IRC plugin;

    public UptimeSetter(J2MC_IRC plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        this.plugin.lastUp = System.currentTimeMillis();
    }

}
