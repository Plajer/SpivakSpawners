package pl.plajer.spivakspawners.handlers;

import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

/**
 * @author Plajer
 * <p>
 * Created at 13.02.2019
 */
public class MergeHandler {

  /**
   * Returns nearest entity with livingEntity can merge into
   *
   * @param en entity that will seek for mergeable similar entity
   * @return Entity with that entity en can be merged or null if not found
   */
  @Nullable
  public Entity getNearbyMergeable(Entity en) {
    for (Entity entity : en.getNearbyEntities(3, 3, 3)) {
      if (entity.getType() != en.getType()) {
        continue;
      }
      if (!entity.hasMetadata("SpivakSpawnersEntity")) {
        continue;
      }
      return entity;
    }
    return null;
  }

}
