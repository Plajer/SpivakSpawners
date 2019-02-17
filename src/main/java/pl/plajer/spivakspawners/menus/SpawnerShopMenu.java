package pl.plajer.spivakspawners.menus;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import pl.plajer.spivakspawners.Main;
import pl.plajer.spivakspawners.registry.spawner.buyable.BuyableSpawner;
import pl.plajer.spivakspawners.user.User;
import pl.plajer.spivakspawners.utils.EntityDisplayNameFixer;
import pl.plajer.spivakspawners.utils.Utils;
import pl.plajerlair.core.utils.ItemBuilder;

/**
 * @author Plajer
 * <p>
 * Created at 15.02.2019
 */
public class SpawnerShopMenu {

  private Main plugin = JavaPlugin.getPlugin(Main.class);

  public SpawnerShopMenu(Player player) {
    Gui gui = new Gui(plugin, 6, plugin.getLanguageManager().color("Menus.Shop-Menu.Inventory-Name"));
    StaticPane pane = new StaticPane(1, 1, 7, 4);

    int x = 0;
    int y = 0;
    User user = plugin.getUserManager().getUser(player);
    for (BuyableSpawner buyableSpawner : plugin.getBuyableSpawnersRegistry().getBuyableSpawners()) {
      ItemStack stack = new ItemBuilder(new ItemStack(Material.MOB_SPAWNER))
          .name(plugin.getLanguageManager().color("Menus.Shop-Menu.Item-Format.Name")
              .replace("%mob%", EntityDisplayNameFixer.fixDisplayName(buyableSpawner.getType())))
          .lore(plugin.getLanguageManager().color("Menus.Shop-Menu.Item-Format.Lore")
              .replace("%money%", String.valueOf(buyableSpawner.getMoneyRequired()))
              .split(";")).build();
      if (buyableSpawner.isUnlocked(user)) {
        stack = new ItemBuilder(stack).lore(plugin.getLanguageManager().color("Menus.Shop-Menu.Item-Format.Bonus-Lore.Unlocked").split(";")).build();
      } else {
        stack = new ItemBuilder(stack).lore(plugin.getLanguageManager().color("Menus.Shop-Menu.Item-Format.Bonus-Lore.Locked")
            .replace("%level_needed%", String.valueOf(buyableSpawner.getLevelNeeded()))
            .replace("%money%", String.valueOf(buyableSpawner.getMoneyRequired()))
            .split(";")).build();
      }
      pane.addItem(new GuiItem(stack, e -> {
        e.setCancelled(true);
        if (!buyableSpawner.isUnlocked(user)) {
          player.sendMessage(plugin.getLanguageManager().color("Menus.Shop-Menu.No-Level"));
          return;
        }
        double balance = plugin.getEconomy().getBalance(player);
        if (balance < buyableSpawner.getMoneyRequired()) {
          player.sendMessage(plugin.getLanguageManager().color("Menus.Shop-Menu.Cannot-Afford")
              .replace("%more%", String.valueOf(buyableSpawner.getMoneyRequired() - balance)));
          return;
        }
        plugin.getEconomy().withdrawPlayer(player, buyableSpawner.getMoneyRequired());
        Utils.giveSpawner(player, buyableSpawner, 1);
      }), x, y);
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
