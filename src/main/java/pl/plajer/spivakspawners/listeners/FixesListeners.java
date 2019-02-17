package pl.plajer.spivakspawners.listeners;

import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.SlimeSplitEvent;

import pl.plajer.spivakspawners.Main;

/**
 * @author Plajer
 * <p>
 * Created at 17.02.2019
 */
public class FixesListeners implements Listener {

  private Main plugin;

  public FixesListeners(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  //fix to make slimes and magma cubes don't damage players
  @EventHandler
  public void onPlayerDamage(EntityDamageByEntityEvent e) {
    if (!(e.getEntity() instanceof Player)) {
      return;
    }
    if (!(e.getDamager() instanceof Slime) || !(e.getDamager() instanceof MagmaCube)) {
      return;
    }
    if (e.getDamager().hasMetadata("SpivakSpawnersEntity")) {
      e.setCancelled(true);
    }
  }

  @EventHandler
  public void onSlimeSplit(SlimeSplitEvent e) {
    if (e.getEntity().hasMetadata("SpivakSpawnersEntity")) {
      e.setCancelled(true);
    }
  }

}
