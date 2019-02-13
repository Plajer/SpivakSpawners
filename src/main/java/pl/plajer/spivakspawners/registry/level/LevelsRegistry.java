package pl.plajer.spivakspawners.registry.level;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;

import pl.plajer.spivakspawners.Main;
import pl.plajer.spivakspawners.registry.heads.Head;
import pl.plajerlair.core.utils.ConfigUtils;

/**
 * @author Plajer
 * <p>
 * Created at 12.02.2019
 */
public class LevelsRegistry {

  private List<Level> levels = new ArrayList<>();
  private Main plugin;

  public LevelsRegistry(Main plugin) {
    this.plugin = plugin;
    registerLevels();
  }

  private void registerLevels() {
    FileConfiguration config = ConfigUtils.getConfig(plugin, "levels");
    Head baseHead = null;
    for (Head head : plugin.getHeadsRegistry().getHeads()) {
      //pig is level 1 spawner
      if (head.getEntityType() == EntityType.PIG) {
        baseHead = head;
        break;
      }
    }
    //register basic level 1, users always start with this level
    levels.add(new Level(1, baseHead, 0, 0));
    for (String key : config.getConfigurationSection("Levels").getKeys(false)) {
      String access = "Levels." + key + ".";
      levels.add(new Level(Integer.parseInt(key), plugin.getHeadsRegistry().getByEntityType(EntityType.valueOf(config.getString(access + "Head-Type").toUpperCase())),
          config.getInt(access + "Heads-Needed"), config.getInt(access + "Money-Needed")));
    }
  }

  public List<Level> getLevels() {
    return levels;
  }
}
