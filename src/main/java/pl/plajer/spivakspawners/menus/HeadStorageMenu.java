package pl.plajer.spivakspawners.menus;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import pl.plajer.spivakspawners.Main;
import pl.plajer.spivakspawners.registry.heads.Head;
import pl.plajer.spivakspawners.user.User;
import pl.plajerlair.core.utils.ItemBuilder;

/**
 * @author Plajer
 * <p>
 * Created at 15.02.2019
 */
public class HeadStorageMenu {

  private Main plugin = JavaPlugin.getPlugin(Main.class);

  public HeadStorageMenu(Player player) {
    Gui gui = new Gui(plugin, 6, plugin.getLanguageManager().color("Menus.Storage-Menu.Inventory-Name"));
    StaticPane pane = new StaticPane(1, 1, 7, 4);

    int x = 0;
    int y = 0;
    User user = plugin.getUserManager().getUser(player);
    for (Head head : plugin.getHeadsRegistry().getHeads()) {
      pane.addItem(new GuiItem(new ItemBuilder(head.getItemStack())
          .name(plugin.getLanguageManager().color("Menus.Storage-Menu.Item-Format.Name")
              .replace("%mob%", head.getEntityType().getName()))
          .lore(plugin.getLanguageManager().color("Menus.Storage-Menu.Item-Format.Lore")
              .replace("%collected%", String.valueOf(user.getOwnedHeads(head))).split(";")).build(),
          e -> e.setCancelled(true)), x, y);
      x++;
      if (x == 7) {
        x = 0;
        y++;
      }
    }
    gui.addPane(pane);
    gui.show(player);
  }

}