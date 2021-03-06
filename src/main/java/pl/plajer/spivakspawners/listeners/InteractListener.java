package pl.plajer.spivakspawners.listeners;

import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import pl.plajer.spivakspawners.Main;
import pl.plajer.spivakspawners.registry.heads.Head;
import pl.plajer.spivakspawners.user.User;
import pl.plajer.spivakspawners.utils.EntityDisplayNameFixer;

/**
 * @author Plajer
 * <p>
 * Created at 15.02.2019
 */
public class InteractListener implements Listener {

  private Main plugin;

  public InteractListener(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onInteract(PlayerInteractEvent e) {
    if (!plugin.getConfig().getBoolean("Unlockable-Spawners-Enabled")) {
      return;
    }
    if (e.getItem() == null || !e.getItem().hasItemMeta() || !e.getItem().getItemMeta().hasDisplayName()
        || !e.getItem().getItemMeta().hasLore()) {
      return;
    }
    for (Head head : plugin.getHeadsRegistry().getHeads()) {
      if (!e.getItem().getItemMeta().getDisplayName().equals(head.getItemStack().getItemMeta().getDisplayName())) {
        continue;
      }
      if (!e.getItem().getItemMeta().getLore().equals(head.getItemStack().getItemMeta().getLore())) {
        continue;
      }
      e.setCancelled(true);
      User user = plugin.getUserManager().getUser(e.getPlayer());
      user.addHeadsAmount(head, e.getItem().getAmount());
      e.getPlayer().sendMessage(plugin.getLanguageManager().color("Messages.Added-Head")
          .replace("%amount%", String.valueOf(e.getItem().getAmount()))
          .replace("%mob%", EntityDisplayNameFixer.fixDisplayName(head.getEntityType())));
      e.getPlayer().setItemInHand(null);
      e.getPlayer().updateInventory();
      e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.valueOf(plugin.getConfig().getString("Heads-Deposit-Sound").toUpperCase()), 1, 1);
      return;
    }
  }

}
