package pl.plajer.spivakspawners.registry.heads;

import org.bukkit.entity.EntityType;

/**
 * @author Plajer
 * <p>
 * Created at 06.02.2019
 * <p>
 * Class that surrounds EntityType
 */
public class Head {

  private EntityType entityType;

  public Head(EntityType entityType) {
    this.entityType = entityType;
  }

  public EntityType getEntityType() {
    return entityType;
  }

}
