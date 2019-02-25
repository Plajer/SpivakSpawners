package pl.plajer.spivakspawners.handlers;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import pl.plajer.spivakspawners.Main;
import pl.plajerlair.core.utils.ConfigUtils;

/**
 * @author Plajer
 * <p>
 * Created at 12.02.2019
 */
public class LanguageManager {

  private FileConfiguration config;

  public LanguageManager(Main plugin) {
    this.config = ConfigUtils.getConfig(plugin, "language");
  }

  public FileConfiguration getConfig() {
    return config;
  }

  public void setConfig(FileConfiguration config) {
    this.config = config;
  }

  public String color(String accessor) {
    return ChatColor.translateAlternateColorCodes('&', config.getString(accessor));
  }

  public String colorRaw(String message) {
    return ChatColor.translateAlternateColorCodes('&', message);
  }

}
