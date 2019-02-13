package pl.plajer.spivakspawners.registry.spawner.data;

import org.bukkit.entity.EntityType;

import java.io.Serializable;

/**
 * @author Plajer
 * <p>
 * Created at 12.02.2019
 */
public class SpawnerData implements Serializable {

    public static final int MAX_UPGRADE_LEVEL = 28;
    private int spawnerLevel;
    private EntityType entityType;

    public SpawnerData(int spawnerLevel, EntityType entityType) {
        this.spawnerLevel = spawnerLevel;
        this.entityType = entityType;
    }

    public int getSpawnerLevel() {
        return spawnerLevel;
    }

    public void setSpawnerLevel(int spawnerLevel) {
        this.spawnerLevel = spawnerLevel;
    }

    public EntityType getEntityType() {
        return entityType;
    }
}
