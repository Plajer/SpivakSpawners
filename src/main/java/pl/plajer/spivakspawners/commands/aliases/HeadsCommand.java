package pl.plajer.spivakspawners.commands.aliases;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import pl.plajer.spivakspawners.Main;

/**
 * @author Plajer
 * <p>
 * Created at 17.02.2019
 */
public class HeadsCommand implements CommandExecutor {

  private Main plugin;

  public HeadsCommand(Main plugin) {
    this.plugin = plugin;
    plugin.getCommand("heads").setExecutor(this);
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage(plugin.getLanguageManager().color("Commands.Only-Player"));
      return true;
    }
    ((Player) sender).performCommand("spawners heads");
    return true;
  }

}
