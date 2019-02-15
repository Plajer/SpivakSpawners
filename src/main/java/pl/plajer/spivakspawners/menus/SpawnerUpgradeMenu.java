package pl.plajer.spivakspawners.menus;

import com.github.stefvanschie.inventoryframework.Gui;
import com.github.stefvanschie.inventoryframework.GuiItem;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import pl.plajer.spivakspawners.Main;
import pl.plajer.spivakspawners.registry.spawner.data.SpawnerData;
import pl.plajer.spivakspawners.registry.spawner.data.SpawnerPerk;
import pl.plajer.spivakspawners.registry.spawner.living.Spawner;
import pl.plajer.spivakspawners.utils.EntityDisplayNameFixer;
import pl.plajerlair.core.utils.ItemBuilder;

/**
 * @author Plajer
 * <p>
 * Created at 12.02.2019
 */
public class SpawnerUpgradeMenu {

  private Main plugin = JavaPlugin.getPlugin(Main.class);

  public SpawnerUpgradeMenu(Spawner spawner, Player player) {
    Gui gui = new Gui(plugin, 6, plugin.getLanguageManager().color("Menus.Spawner-Overview.Inventory-Name"));
    StaticPane pane = new StaticPane(1, 1, 7, 4);

    int x = 0;
    int y = 3;
    int perkOrdinal = 0;
    for (int i = 1; i < SpawnerData.MAX_UPGRADE_LEVEL + 1; i++) {
      ItemStack item;
      boolean perkUpgrade = i % 4 == 0;
      if (spawner.getSpawnerData().getSpawnerLevel() >= i) {
        if (perkUpgrade) {
          item = new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 4))
              .name(plugin.getLanguageManager().color("Menus.Spawner-Overview.Perk-Unlocked-Level.Name")
                  .replace("%level%", String.valueOf(i)))
              .lore(plugin.getLanguageManager().color("Menus.Spawner-Overview.Perk-Unlocked-Level.Lore"
                  .replace("%reward%", SpawnerPerk.values()[perkOrdinal].getFormattedName()))
                  .split(";"))
              .build();
        } else {
          item = new ItemBuilder(new ItemStack(Material.INK_SACK, 1, (short) 10))
              .name(plugin.getLanguageManager().color("Menus.Spawner-Overview.Unlocked-Level.Name")
                  .replace("%level%", String.valueOf(i)))
              .lore(plugin.getLanguageManager().color("Menus.Spawner-Overview.Unlocked-Level.Lore").split(";"))
              .build();
        }
      } else {
        if (perkUpgrade) {
          item = new ItemBuilder(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 7))
              .name(plugin.getLanguageManager().color("Menus.Spawner-Overview.Perk-Locked-Level.Name")
                  .replace("%level%", String.valueOf(i)))
              .lore(plugin.getLanguageManager().color("Menus.Spawner-Overview.Perk-Locked-Level.Lore")
                  .replace("%mob%", EntityDisplayNameFixer.fixDisplayName(spawner.getSpawnerData().getEntityType()))
                  .replace("%current_level%", String.valueOf(spawner.getSpawnerData().getSpawnerLevel()))
                  .replace("%reward%", SpawnerPerk.values()[perkOrdinal].getFormattedName())
                  .split(";"))
              .build();
        } else {
          item = new ItemBuilder(new ItemStack(Material.INK_SACK, 1, (short) 8))
              .name(plugin.getLanguageManager().color("Menus.Spawner-Overview.Locked-Level.Name")
                  .replace("%level%", String.valueOf(i)))
              .lore(plugin.getLanguageManager().color("Menus.Spawner-Overview.Locked-Level.Lore")
                  .replace("%mob%", EntityDisplayNameFixer.fixDisplayName(spawner.getSpawnerData().getEntityType()))
                  .replace("%current_level%", String.valueOf(spawner.getSpawnerData().getSpawnerLevel())).split(";"))
              .build();
        }
        if (perkUpgrade) {
          perkOrdinal++;
        }
      }
      pane.addItem(new GuiItem(item, event -> event.setCancelled(true)), x, y);
      y--;
      if (y == -1) {
        y = 3;
        x++;
      }
    }
    gui.addPane(pane);
    gui.show(player);
  }

}
