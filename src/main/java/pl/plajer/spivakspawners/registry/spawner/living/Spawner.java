package pl.plajer.spivakspawners.registry.spawner.living;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import pl.plajer.spivakspawners.registry.spawner.data.SpawnerData;
import pl.plajer.spivakspawners.registry.spawner.data.SpawnerPerk;

/**
 * @author Plajer
 * <p>
 * Created at 06.02.2019
 * <p>
 * Represents real spawned spawner block somewhere in the world
 */
public class Spawner implements Serializable {

  private static final long serialVersionUID = 123456789;
  private transient Location location;
  //serializable object that can replace non serializable location object
  private SpawnerLocation spawnerLocation;
  private UUID owner;
  private List<SpawnerPerk> perks = new ArrayList<>();
  private SpawnerData spawnerData;

  public Spawner(UUID owner, Location location, EntityType type) {
    this.owner = owner;
    this.location = location;
    this.spawnerLocation = new SpawnerLocation(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    this.spawnerData = new SpawnerData(1, type);
  }

  /**
   * Deserializes Base64 string to Spawner object
   *
   * @param str base64 string to deserialize
   * @return deserialized Spawner object
   */
  public static Spawner deserialize(String str) {
    try {
      byte[] data = Base64.getDecoder().decode(str);
      ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(data));
      Spawner spawner = (Spawner) objectInputStream.readObject();
      objectInputStream.close();
      spawner.location = spawner.spawnerLocation.asLocation();
      return spawner;
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
      return null;
    }
  }

  public UUID getOwner() {
    return owner;
  }

  public Location getLocation() {
    return location;
  }

  public List<SpawnerPerk> getPerks() {
    return perks;
  }

  public boolean shouldApplyPerk() {
    return getSpawnerData().getSpawnerLevel() % 4 == 0;
  }

  public void addPerk(SpawnerPerk perk) {
    perks.add(perk);
  }

  public SpawnerData getSpawnerData() {
    return spawnerData;
  }

  /**
   * Serializes Spawner into Base64 string
   *
   * @return serialized object
   */
  public String serialize() {
    try {
      ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream);
      objectOutputStream.writeObject(this);
      objectOutputStream.close();
      return Base64.getEncoder().encodeToString(byteOutputStream.toByteArray());
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  private void writeObject(ObjectOutputStream stream)
      throws IOException {
    stream.defaultWriteObject();
  }

  private void readObject(ObjectInputStream stream)
      throws IOException, ClassNotFoundException {
    stream.defaultReadObject();
  }

}
