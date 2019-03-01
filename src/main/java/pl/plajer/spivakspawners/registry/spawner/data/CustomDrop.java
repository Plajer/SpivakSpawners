package pl.plajer.spivakspawners.registry.spawner.data;

import org.bukkit.inventory.ItemStack;

/**
 * @author Plajer
 * <p>
 * Created at 01.03.2019
 */
public class CustomDrop {

  private ItemStack drop;
  private double chance;

  public CustomDrop(ItemStack drop, double chance) {
    this.drop = drop;
    this.chance = chance;
  }

  public ItemStack getDrop() {
    return drop;
  }

  public double getChance() {
    return chance;
  }
}
