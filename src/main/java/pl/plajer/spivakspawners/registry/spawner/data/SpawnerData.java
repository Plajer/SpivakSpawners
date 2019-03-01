package pl.plajer.spivakspawners.registry.spawner.data;

import java.io.Serializable;
import java.util.List;

import org.bukkit.entity.EntityType;

/**
 * @author Plajer
 * <p>
 * Created at 12.02.2019
 */
public class SpawnerData implements Serializable {

  public static final int MAX_UPGRADE_LEVEL = 28;
  private int spawnerLevel;
  private EntityType entityType;
  private List<CustomDrop> customDrops;

  public SpawnerData(int spawnerLevel, EntityType entityType, List<CustomDrop> customDrops) {
    this.spawnerLevel = spawnerLevel;
    this.entityType = entityType;
    this.customDrops = customDrops;
  }

  public int getSpawnerLevel() {
    return spawnerLevel;
  }

  public void setSpawnerLevel(int spawnerLevel) {
    this.spawnerLevel = spawnerLevel;
  }

  public List<CustomDrop> getCustomDrops() {
    return customDrops;
  }

  public EntityType getEntityType() {
    return entityType;
  }
}
