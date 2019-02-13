package pl.plajer.spivakspawners;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import pl.plajer.spivakspawners.handlers.LanguageManager;
import pl.plajer.spivakspawners.handlers.MergeHandler;
import pl.plajer.spivakspawners.listeners.EntityListeners;
import pl.plajer.spivakspawners.listeners.JoinQuitListener;
import pl.plajer.spivakspawners.listeners.SpawnerListeners;
import pl.plajer.spivakspawners.registry.heads.HeadsRegistry;
import pl.plajer.spivakspawners.registry.level.LevelsRegistry;
import pl.plajer.spivakspawners.registry.spawner.buyable.BuyableSpawnersRegistry;
import pl.plajer.spivakspawners.registry.spawner.living.Spawner;
import pl.plajer.spivakspawners.registry.spawner.living.SpawnerEntity;
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
    private MergeHandler mergeHandler;

    @Override
    public void onEnable() {
        generateFiles();
        this.languageManager = new LanguageManager(this);
        headsRegistry = new HeadsRegistry(this);
        levelsRegistry = new LevelsRegistry(this);
        buyableSpawnersRegistry = new BuyableSpawnersRegistry(this);
        spawnersStorage = new SpawnersStorage(this);
        userManager = new UserManager(this);
        mergeHandler = new MergeHandler();
        new JoinQuitListener(this);
        new SpawnerListeners(this);
        new EntityListeners(this);
        hologramUpdateTask();
    }

    @Override
    public void onDisable() {
        for(User user : userManager.getUsers()) {
            userManager.saveData(user);
        }
        for(Spawner spawner : spawnersStorage.getSpawnedSpawners()) {
            spawnersStorage.saveSpawnerData(spawner);
        }
        for(World world : Bukkit.getWorlds()) {
            for(Entity en : world.getEntities()) {
                if(en.hasMetadata("SpivakSpawnersEntity")) {
                    en.remove();
                }
            }
        }
        for(Hologram hologram : HologramsAPI.getHolograms(this)) {
            hologram.delete();
        }
    }

    private void generateFiles() {
        String[] files = new String[]{"language", "levels", "spawners", "spawners_data", "userdata"};
        for(String file : files) {
            ConfigUtils.getConfig(this, file);
        }
     }

    private void hologramUpdateTask() {
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for(SpawnerEntity spawnerEntity : spawnersStorage.getSpawnerEntities()) {
                spawnerEntity.getHologram().teleport(spawnerEntity.getEntity().getLocation().add(0, 2, 0));
            }
        }, 20 * 7, 20 * 7);
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

    public UserManager getUserManager() {
        return userManager;
    }
}
