package pl.plajer.spivakspawners.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.plajer.spivakspawners.Main;

/**
 * @author Plajer
 * <p>
 * Created at 12.02.2019
 */
public class JoinQuitListener implements Listener {

    private Main plugin;

    public JoinQuitListener(Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        plugin.getUserManager().loadData(plugin.getUserManager().getUser(e.getPlayer()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        plugin.getUserManager().saveData(plugin.getUserManager().getUser(e.getPlayer()));
    }

}
