package pl.plajer.spivakspawners;

import org.bukkit.plugin.java.JavaPlugin;
import pl.plajer.spivakspawners.handlers.LanguageManager;
import pl.plajer.spivakspawners.listeners.JoinQuitListener;
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

    private LanguageManager languageManager;
    private LevelsRegistry levelsRegistry;
    private HeadsRegistry headsRegistry;
    private BuyableSpawnersRegistry buyableSpawnersRegistry;
    private SpawnersStorage spawnersStorage;
    private UserManager userManager;

    @Override
    public void onEnable() {
        generateFiles();
        this.languageManager = new LanguageManager(this);
        headsRegistry = new HeadsRegistry(this);
        levelsRegistry = new LevelsRegistry(this);
        buyableSpawnersRegistry = new BuyableSpawnersRegistry(this);
        spawnersStorage = new SpawnersStorage(this);
        userManager = new UserManager(this);
        new JoinQuitListener(this);
        new SpawnerListeners(this);
    }

    @Override
    public void onDisable() {
        for(User user : userManager.getUsers()) {
            userManager.saveData(user);
        }
        for(Spawner spawner : spawnersStorage.getSpawnedSpawners()) {
            spawnersStorage.saveSpawnerData(spawner);
        }
    }

    private void generateFiles() {
        String[] files = new String[]{"language", "levels", "spawners", "spawners_data", "userdata"};
        for(String file : files) {
            ConfigUtils.getConfig(this, file);
        }
     }

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    public LevelsRegistry getLevelsRegistry() {
        return levelsRegistry;
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

    public UserManager getUserManager() {
        return userManager;
    }
}
