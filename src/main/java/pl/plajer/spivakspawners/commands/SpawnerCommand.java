package pl.plajer.spivakspawners.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import pl.plajer.spivakspawners.Main;
import pl.plajer.spivakspawners.menus.HeadStorageMenu;

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
        return true;
      default:
        //todo invalid args
        return true;
    }
  }
}
