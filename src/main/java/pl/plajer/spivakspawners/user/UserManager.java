package pl.plajer.spivakspawners.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import pl.plajer.spivakspawners.Main;
import pl.plajer.spivakspawners.registry.heads.Head;
import pl.plajerlair.core.utils.ConfigUtils;

/**
 * @author Plajer
 * <p>
 * Created at 12.02.2019
 */
public class UserManager {

  private Main plugin;
  private List<User> users = new ArrayList<>();
  private FileConfiguration config;

  public UserManager(Main plugin) {
    this.plugin = plugin;
    config = ConfigUtils.getConfig(plugin, "userdata");
    loadOnlinePlayers();
  }

  private void loadOnlinePlayers() {
    for (Player player : Bukkit.getServer().getOnlinePlayers()) {
      loadData(getUser(player));
    }
  }

  public User getUser(Player player) {
    for (User user : users) {
      if (user.getPlayer().equals(player)) {
        return user;
      }
    }
    User user = new User(player);
    users.add(user);
    return user;
  }

  public void saveData(User user) {
    config.set(user.getPlayer().getUniqueId() + ".Level", user.getLevel());
    for (Map.Entry<Head, Integer> entry : user.getOwnedHeads().entrySet()) {
      config.set(user.getPlayer().getUniqueId() + ".Head." + entry.getKey().getEntityType().getName(), entry.getValue());
    }
    ConfigUtils.saveConfig(plugin, config, "userdata");
  }

  public void loadData(User user) {
    for (Head head : plugin.getHeadsRegistry().getHeads()) {
      if (!config.isSet(user.getPlayer().getUniqueId() + ".Head." + head.getEntityType().getName())) {
        continue;
      }
      user.loadOwnedHead(head, config.getInt(user.getPlayer().getUniqueId() + ".Head." + head.getEntityType().getName()));
    }
    user.setLevel(config.getInt(user.getPlayer().getUniqueId() + ".Level", 1));
  }

  public List<User> getUsers() {
    return users;
  }

  public void removeUser(User user) {
    users.remove(user);
  }

}
