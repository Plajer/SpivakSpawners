package pl.plajer.spivakspawners.commands;

import java.util.Arrays;

import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import pl.plajer.spivakspawners.Main;
import pl.plajer.spivakspawners.menus.HeadStorageMenu;
import pl.plajer.spivakspawners.menus.SpawnerShopMenu;
import pl.plajer.spivakspawners.registry.heads.Head;
import pl.plajer.spivakspawners.registry.level.Level;
import pl.plajer.spivakspawners.registry.spawner.buyable.BuyableSpawner;
import pl.plajer.spivakspawners.user.User;
import pl.plajer.spivakspawners.utils.Utils;
import pl.plajerlair.core.utils.ConfigUtils;

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
        if (plugin.getConfig().getBoolean("Unlockable-Spawners-Enabled")) {
          new HeadStorageMenu((Player) sender);
          return true;
        }
        return false;
      case "give":
        if (!sender.hasPermission("spivakspawners.give")) {
          sender.sendMessage(plugin.getLanguageManager().color("Commands.No-Permission"));
          return true;
        }
        if (args.length == 1) {
          sender.sendMessage(plugin.getLanguageManager().color("Commands.Type-Argument")
              .replace("%command%", "/spawner give <spawner type> <amount> (player)"));
          return true;
        }
        String mobType = args[1];
        BuyableSpawner spawner = null;
        for (BuyableSpawner buyableSpawner : plugin.getBuyableSpawnersRegistry().getBuyableSpawners()) {
          if (!buyableSpawner.getHeadTypeRequired().getEntityType().name().equals(mobType.toUpperCase())) {
            continue;
          }
          spawner = buyableSpawner;
          break;
        }
        if (spawner == null) {
          sender.sendMessage(plugin.getLanguageManager().color("Commands.Invalid-Spawner").replace("%types%",
              Arrays.toString(plugin.getBuyableSpawnersRegistry().getBuyableSpawners().stream()
                  .map(spwn -> spwn.getHeadTypeRequired().getEntityType().name().toLowerCase()).toArray())));
          return true;
        }
        if (args.length == 2) {
          Utils.giveSpawner((Player) sender, spawner, 1);
          return true;
        }
        if (!NumberUtils.isNumber(args[2])) {
          sender.sendMessage(plugin.getLanguageManager().color("Commands.Type-Argument")
              .replace("%command%", "/spawner give <spawner type> <valid number> (player)"));
          return true;
        }
        Player spawnerReceiver = (Player) sender;
        if (args.length == 4) {
          Player argTarget = Bukkit.getPlayer(args[3]);
          if (argTarget == null) {
            sender.sendMessage(plugin.getLanguageManager().color("Commands.Invalid-Player"));
            return true;
          }
          spawnerReceiver = argTarget;
        }
        Utils.giveSpawner(spawnerReceiver, spawner, Integer.parseInt(args[2]));
        return true;
      case "givehead":
        if (!plugin.getConfig().getBoolean("Unlockable-Spawners-Enabled")) {
          return false;
        }
        if (!sender.hasPermission("spivakspawners.givehead")) {
          sender.sendMessage(plugin.getLanguageManager().color("Commands.No-Permission"));
          return true;
        }
        if (args.length == 1) {
          sender.sendMessage(plugin.getLanguageManager().color("Commands.Type-Argument")
              .replace("%command%", "/spawner givehead <head type> <amount> (player)"));
          return true;
        }
        String headType = args[1];
        Head head = null;
        for (Head loopHead : plugin.getHeadsRegistry().getHeads()) {
          if (!loopHead.getEntityType().name().equals(headType.toUpperCase())) {
            continue;
          }
          head = loopHead;
          break;
        }
        if (head == null) {
          sender.sendMessage(plugin.getLanguageManager().color("Commands.Invalid-Head").replace("%types%",
              Arrays.toString(plugin.getHeadsRegistry().getHeads().stream()
                  .map(en -> en.getEntityType().name().toLowerCase()).toArray())));
          return true;
        }
        if (args.length == 2) {
          Utils.giveHead((Player) sender, head, 1);
          return true;
        }
        if (!NumberUtils.isNumber(args[2])) {
          sender.sendMessage(plugin.getLanguageManager().color("Commands.Type-Argument")
              .replace("%command%", "/spawner givehead <head type> <valid number> (player)"));
          return true;
        }
        Player headReceiver = (Player) sender;
        if (args.length == 4) {
          Player argTarget = Bukkit.getPlayer(args[3]);
          if (argTarget == null) {
            sender.sendMessage(plugin.getLanguageManager().color("Commands.Invalid-Player"));
            return true;
          }
          headReceiver = argTarget;
        }
        Utils.giveHead(headReceiver, head, Integer.parseInt(args[2]));
        return true;
      case "shop":
        if (!plugin.getConfig().getBoolean("Unlockable-Spawners-Enabled")) {
          return false;
        }
        new SpawnerShopMenu((Player) sender);
        return true;
      case "rankup":
        if (!plugin.getConfig().getBoolean("Unlockable-Spawners-Enabled")) {
          return false;
        }
        User user = plugin.getUserManager().getUser((Player) sender);
        int nextLevelNumber = user.getLevel() + 1;
        Level nextLevel = null;
        for (Level level : plugin.getLevelsRegistry().getLevels()) {
          if (level.getLevel() == nextLevelNumber) {
            nextLevel = level;
          }
        }
        //max level
        if (nextLevel == null) {
          sender.sendMessage(plugin.getLanguageManager().color("Commands.Rank-Up.Max-Level"));
          return true;
        }
        int headsOwned = user.getOwnedHeads(nextLevel.getHead());
        if (headsOwned < nextLevel.getHeadsNeeded()) {
          sender.sendMessage(plugin.getLanguageManager().color("Commands.Rank-Up.No-Heads")
              .replace("%more%", String.valueOf(nextLevel.getHeadsNeeded() - headsOwned)));
          return true;
        }
        double balance = plugin.getEconomy().getBalance((Player) sender);
        if (balance < nextLevel.getMoneyNeeded()) {
          sender.sendMessage(plugin.getLanguageManager().color("Commands.Rank-Up.No-Money")
              .replace("%more%", String.valueOf(nextLevel.getMoneyNeeded() - balance)));
          return true;
        }
        user.setLevel(nextLevelNumber);
        plugin.getEconomy().withdrawPlayer((Player) sender, nextLevel.getMoneyNeeded());
        user.getOwnedHeads().put(nextLevel.getHead(), user.getOwnedHeads(nextLevel.getHead()) - nextLevel.getHeadsNeeded());
        sender.sendMessage(plugin.getLanguageManager().color("Commands.Rank-Up.Success")
            .replace("%level%", String.valueOf(nextLevelNumber)));
        return true;
      case "reload":
        if (!sender.hasPermission("spivakspawners.reload")) {
          sender.sendMessage(plugin.getLanguageManager().color("Commands.No-Permission"));
          return true;
        }
        plugin.reloadConfig();
        plugin.getLanguageManager().setConfig(ConfigUtils.getConfig(plugin, "language"));
        sender.sendMessage(plugin.getLanguageManager().color("Commands.Reloaded-Configs"));
        return true;
      default:
        sender.sendMessage(plugin.getLanguageManager().color("Commands.Help-Command.Header"));
        sender.sendMessage(plugin.getLanguageManager().color("Commands.Help-Command.Description"));
        return true;
    }
  }

}
