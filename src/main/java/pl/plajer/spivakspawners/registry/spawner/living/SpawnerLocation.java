package pl.plajer.spivakspawners.registry.spawner.living;

import java.io.Serializable;

import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * @author Plajer
 * <p>
 * Created at 12.02.2019
 */
public class SpawnerLocation implements Serializable {

  private String worldName;
  private int x;
  private int y;
  private int z;

  public SpawnerLocation(String worldName, int x, int y, int z) {
    this.worldName = worldName;
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public String getWorldName() {
    return worldName;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public int getZ() {
    return z;
  }

  public Location asLocation() {
    return new Location(Bukkit.getWorld(worldName), x, y, z);
  }

}
