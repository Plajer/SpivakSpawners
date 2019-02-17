package pl.plajer.spivakspawners.commands;

import java.util.Arrays;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import pl.plajer.spivakspawners.Main;
import pl.plajer.spivakspawners.menus.HeadStorageMenu;
import pl.plajer.spivakspawners.menus.SpawnerShopMenu;
import pl.plajer.spivakspawners.registry.spawner.buyable.BuyableSpawner;
import pl.plajer.spivakspawners.utils.EntityDisplayNameFixer;
import pl.plajerlair.core.utils.ItemBuilder;

/**
 * @author Plajer
 * <p>
 * Created at 14.02.2019
 */
//todo
public class SpawnerCommand implements CommandExecutor {

  private Main plugin;

  public SpawnerCommand(Main plugin) {
    this.plugin = plugin;
    plugin.getCommand("spawners").setExecutor(this);
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage(plugin.getLanguageManager().color("Commands.Only-Player"));
      return true;
    }
    if (args.length == 0) {
      sender.sendMessage(plugin.getLanguageManager().color("Commands.Help-Command.Header"));
      sender.sendMessage(plugin.getLanguageManager().color("Commands.Help-Command.Description"));
      return true;
    }
    switch (args[0].toLowerCase()) {
      case "heads":
        new HeadStorageMenu((Player) sender);
        return true;
      case "give":
        if (!sender.hasPermission("spivakspawners.give")) {
          sender.sendMessage(plugin.getLanguageManager().color("Commands.No-Permission"));
          return true;
        }
        if (args.length == 1) {
          sender.sendMessage(plugin.getLanguageManager().color("Commands.Type-Argument").replace("%command%", "/spawner give <spawner type> <amount> (player)"));
          return true;
        }
        String mobType = args[1];
        BuyableSpawner spawner = null;
        for (BuyableSpawner buyableSpawner : plugin.getBuyableSpawnersRegistry().getBuyableSpawners()) {
          if (!buyableSpawner.getHeadTypeRequired().getEntityType().getName().equals(mobType.toUpperCase())) {
            continue;
          }
          spawner = buyableSpawner;
        }
        if (spawner == null) {
          sender.sendMessage(plugin.getLanguageManager().color("Commands.Invalid-Spawner").replace("%types%",
              Arrays.toString(plugin.getBuyableSpawnersRegistry().getBuyableSpawners().stream()
                  .map(spwn -> spwn.getHeadTypeRequired().getEntityType().getName()).toArray())));
          return true;
        }
        if (args.length == 2) {
          giveSpawner((Player) sender, spawner, 1);
          return true;
        }
        if (!NumberUtils.isNumber(args[2])) {
          sender.sendMessage(plugin.getLanguageManager().color("Commands.Type-Argument").replace("%command%", "/spawner give <spawner type> <valid number> (player)"));
          return true;
        }
        Player target = (Player) sender;
        if (args.length == 4) {
          Player argTarget = Bukkit.getPlayer(args[3]);
          if (argTarget == null) {
            sender.sendMessage(plugin.getLanguageManager().color("Commands.Invalid-Player"));
            return true;
          }
          target = argTarget;
        }
        giveSpawner(target, spawner, Integer.valueOf(args[2]));
        return true;
      case "shop":
        new SpawnerShopMenu((Player) sender);
        return true;
      case "rankup":
        return true;
      default:
        sender.sendMessage(plugin.getLanguageManager().color("Commands.Help-Command.Header"));
        sender.sendMessage(plugin.getLanguageManager().color("Commands.Help-Command.Description"));
        return true;
    }
  }

  private void giveSpawner(Player player, BuyableSpawner spawner, int amount) {
    String type = EntityDisplayNameFixer.fixDisplayName(spawner.getType());
    player.getInventory().addItem(new ItemBuilder(new ItemStack(Material.MOB_SPAWNER, amount))
        .name(plugin.getLanguageManager().color("Drop-Item.Name")
            .replace("%mob%", type))
        .lore(plugin.getLanguageManager().color("Drop-Item.Lore").split(";"))
        .build());
    player.sendMessage(plugin.getLanguageManager().color("Commands.Received-Spawner")
        .replace("%amount%", String.valueOf(1))
        .replace("%mob%", type));
  }

}
