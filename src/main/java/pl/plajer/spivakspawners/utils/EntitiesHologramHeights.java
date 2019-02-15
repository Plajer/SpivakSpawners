package pl.plajer.spivakspawners.utils;

/**
 * @author Plajer
 * <p>
 * Created at 14.02.2019
 */
public enum EntitiesHologramHeights {

  PIG(1.3), WOLF(1.35), OCELOT(1.2), RABBIT(1.0), SHEEP(1.8), CHICKEN(1.0), COW(1.9), MUSHROOM_COW(1.9), HORSE(2.1), ENDERMITE(0.8),
  CAVE_SPIDER(1.0), SPIDER(1.4), SQUID(1.3), ZOMBIE(2.45), SKELETON(2.49), GUARDIAN(1.35), CREEPER(2.2), MAGMA_CUBE(1.52), SLIME(1.52),
  ENDERMAN(3.4), BLAZE(2.3), WITCH(2.45), IRON_GOLEM(3.2), PIG_ZOMBIE(1.475), SILVERFISH(0.8), VILLAGER(1.475), GHAST(4.5);

  private double height;

  EntitiesHologramHeights(double height) {
    this.height = height;
  }

  public double getHeight() {
    return height;
  }

}