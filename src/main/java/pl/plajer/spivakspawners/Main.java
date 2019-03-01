package pl.plajer.spivakspawners;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import pl.plajer.spivakspawners.commands.SpawnerCommand;
import pl.plajer.spivakspawners.commands.aliases.HeadsCommand;
import pl.plajer.spivakspawners.handlers.LanguageManager;
import pl.plajer.spivakspawners.handlers.MergeHandler;
import pl.plajer.spivakspawners.listeners.EntityListeners;
import pl.plajer.spivakspawners.listeners.FixesListeners;
import pl.plajer.spivakspawners.listeners.InteractListener;
import pl.plajer.spivakspawners.listeners.JoinQuitListeners;
import pl.plajer.spivakspawners.listeners.SpawnerListeners;
import pl.plajer.spivakspawners.registry.heads.HeadsRegistry;
import pl.plajer.spivakspawners.registry.level.LevelsRegistry;
import pl.plajer.spivakspawners.registry.spawner.buyable.BuyableSpawnersRegistry;
import pl.plajer.spivakspawners.registry.spawner.living.Spawner;
import pl.plajer.spivakspawners.registry.spawner.living.SpawnersStorage;
import pl.plajer.spivakspawners.user.User;
import pl.plajer.spivakspawners.user.UserManager;
import pl.plajerlair.core.utils.ConfigUtils;

/**
 * @author Plajer
 * <p>
 * Created at 06.02.2019
 */
public class Main extends JavaPlugin {

  private Economy economy = null;
  private LanguageManager languageManager;
  private LevelsRegistry levelsRegistry;
  private HeadsRegistry headsRegistry;
  private BuyableSpawnersRegistry buyableSpawnersRegistry;
  private SpawnersStorage spawnersStorage;
  private UserManager userManager;
  private MergeHandler mergeHandler;
  private SpawnerListeners spawnerListeners;

  @Override
  public void onEnable() {
    saveDefaultConfig();
    generateFiles();
    setupEconomy();
    this.languageManager = new LanguageManager(this);
    headsRegistry = new HeadsRegistry(this);
    levelsRegistry = new LevelsRegistry(this);
    buyableSpawnersRegistry = new BuyableSpawnersRegistry(this);
    spawnersStorage = new SpawnersStorage(this);
    userManager = new UserManager(this);
    mergeHandler = new MergeHandler();
    new JoinQuitListeners(this);
    spawnerListeners = new SpawnerListeners(this);
    new EntityListeners(this);
    new InteractListener(this);
    new FixesListeners(this);
    new SpawnerCommand(this);
    new HeadsCommand(this);
  }

  @Override
  public void onDisable() {
    for (User user : userManager.getUsers()) {
      userManager.saveData(user);
    }
    for (Spawner spawner : spawnersStorage.getSpawnedSpawners()) {
      spawnersStorage.saveSpawnerData(spawner);
    }
    for (World world : Bukkit.getWorlds()) {
      for (Entity en : world.getEntities()) {
        if (en.hasMetadata("SpivakSpawnersEntity")) {
          en.remove();
        }
      }
    }
  }

  private void generateFiles() {
    String[] files = new String[] {"language", "levels", "spawners", "spawners_data", "userdata"};
    for (String file : files) {
      ConfigUtils.getConfig(this, file);
    }
  }


  private void setupEconomy() {
    if (getServer().getPluginManager().getPlugin("Vault") == null) {
      return;
    }
    RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
    if (rsp == null) {
      return;
    }
    economy = rsp.getProvider();
  }

  public Economy getEconomy() {
    return economy;
  }

  public LanguageManager getLanguageManager() {
    return languageManager;
  }

  public LevelsRegistry getLevelsRegistry() {
    return levelsRegistry;
  }

  public MergeHandler getMergeHandler() {
    return mergeHandler;
  }

  public HeadsRegistry getHeadsRegistry() {
    return headsRegistry;
  }

  public BuyableSpawnersRegistry getBuyableSpawnersRegistry() {
    return buyableSpawnersRegistry;
  }

  public SpawnersStorage getSpawnersStorage() {
    return spawnersStorage;
  }

  public SpawnerListeners getSpawnerListeners() {
    return spawnerListeners;
  }

  public UserManager getUserManager() {
    return userManager;
  }
}
