package pl.plajer.spivakspawners.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import pl.plajer.spivakspawners.Main;
import pl.plajer.spivakspawners.user.User;

/**
 * @author Plajer
 * <p>
 * Created at 12.02.2019
 */
public class JoinQuitListeners implements Listener {

  private Main plugin;

  public JoinQuitListeners(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent e) {
    plugin.getUserManager().loadData(plugin.getUserManager().getUser(e.getPlayer()));
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent e) {
    User user = plugin.getUserManager().getUser(e.getPlayer());
    plugin.getUserManager().saveData(user);
    plugin.getUserManager().removeUser(user);
  }

}
