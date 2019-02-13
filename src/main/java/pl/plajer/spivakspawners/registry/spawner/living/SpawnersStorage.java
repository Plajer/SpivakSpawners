package pl.plajer.spivakspawners.registry.spawner.living;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;
import pl.plajer.spivakspawners.Main;
import pl.plajerlair.core.utils.ConfigUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * @author Plajer
 * <p>
 * Created at 12.02.2019
 */
public class SpawnersStorage {

    private List<SpawnerEntity> spawnerEntities = new ArrayList<>();
    private List<Spawner> spawnedSpawners = new ArrayList<>();
    private Main plugin;

    public SpawnersStorage(Main plugin) {
        this.plugin = plugin;
        loadSpawnersData();
    }

    private void loadSpawnersData() {
        FileConfiguration config = ConfigUtils.getConfig(plugin, "spawners_data");
        for(String key : config.getStringList("Data")) {
            Spawner spawner = Spawner.deserialize(key);
            if(!validate(spawner)) {
                plugin.getLogger().log(Level.INFO, "Invalid spawner loaded, did somebody removed it after plugin disable?");
                continue;
            }
            spawnedSpawners.add(spawner);
        }
        config.set("Data", null);
        ConfigUtils.saveConfig(plugin, config, "spawners_data");
    }

    /**
     * Attempts to save spawner into spawners_data.yml file
     * @param spawner spawner to serialize and save
     */
    public void saveSpawnerData(Spawner spawner) {
        //do not save invalid spawner
        if(!validate(spawner)) {
            return;
        }
        FileConfiguration config = ConfigUtils.getConfig(plugin, "spawners_data");
        List<String> datum = new ArrayList<>(config.getStringList("Data"));
        datum.add(spawner.serialize());
        config.set("Data", datum);
        ConfigUtils.saveConfig(plugin, config, "spawners_data");
    }

    /**
     * Get spawner by location
     *
     * @param loc location
     * @return Spawner or null if no spawner in location
     */
    @Nullable
    public Spawner getByLocation(Location loc) {
        for(Spawner spawner : spawnedSpawners) {
            if(spawner.getLocation().equals(loc)) {
                return spawner;
            }
        }
        return null;
    }

    private boolean validate(Spawner spawner) {
        return spawner.getLocation().getBlock().getType() == Material.MOB_SPAWNER;
    }

    public List<Spawner> getSpawnedSpawners() {
        return spawnedSpawners;
    }

    public List<SpawnerEntity> getSpawnerEntities() {
        return spawnerEntities;
    }

    @Nullable
    public SpawnerEntity getSpawnerEntity(Entity en) {
        for(SpawnerEntity spawnerEntity : spawnerEntities) {
            if(spawnerEntity.getEntity().equals(en)) {
                return spawnerEntity;
            }
        }
        return null;
    }

}
