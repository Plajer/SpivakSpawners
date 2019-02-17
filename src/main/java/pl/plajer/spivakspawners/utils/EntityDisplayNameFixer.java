package pl.plajer.spivakspawners.utils;

import org.bukkit.entity.EntityType;

/**
 * @author Plajer
 * <p>
 * Created at 15.02.2019
 */
public class EntityDisplayNameFixer {

  //1.8 has some weird names for some of the entities
  public static String fixDisplayName(EntityType entityType) {
    switch (entityType) {
      case OCELOT:
        return "Ocelot";
      case IRON_GOLEM:
        return "Iron Golem";
      case MUSHROOM_COW:
        return "Mushroom Cow";
      case MAGMA_CUBE:
        return "Magma Cube";
      case CAVE_SPIDER:
        return "Cave Spider";
      case PIG_ZOMBIE:
        return "Pigman";
      case HORSE:
        return "Horse";
      default:
        return entityType.getName();
    }
  }

  public static String fromFixedDisplayName(String displayName) {
    switch (displayName) {
      case "Ocelot":
        return "Ozelot";
      case "Iron Golem":
        return "VillagerGolem";
      case "Mushroom Cow":
        return "MushroomCow";
      case "Magma Cube":
        return "LavaSlime";
      case "Cave Spider":
        return "CaveSpider";
      case "Pigman":
        return "PigZombie";
      case "Horse":
        return "EntityHorse";
      default:
        return displayName;
    }
  }

}
