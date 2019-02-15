package pl.plajer.spivakspawners.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import pl.plajer.spivakspawners.Main;
import pl.plajer.spivakspawners.registry.heads.Head;
import pl.plajer.spivakspawners.user.User;

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
    if (e.getItem() == null) {
      return;
    }
    for (Head head : plugin.getHeadsRegistry().getHeads()) {
      if (!e.getItem().isSimilar(head.getItemStack())) {
        continue;
      }
      User user = plugin.getUserManager().getUser(e.getPlayer());
      user.addHeadsAmount(head, e.getItem().getAmount());
      e.getPlayer().sendMessage(plugin.getLanguageManager().color("Messages.Added-Head")
          .replace("%amount%", String.valueOf(e.getItem().getAmount()))
          .replace("%mob%", head.getEntityType().getName()));
      e.getItem().setAmount(0);
      return;
    }
  }

}
