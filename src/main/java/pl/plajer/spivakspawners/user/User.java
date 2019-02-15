package pl.plajer.spivakspawners.user;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import pl.plajer.spivakspawners.registry.heads.Head;

/**
 * @author Plajer
 * <p>
 * Created at 12.02.2019
 */
public class User {

  private Player player;
  private Map<Head, Integer> ownedHeads = new HashMap<>();
  private int level;

  public User(Player player) {
    this.player = player;
  }

  public int getLevel() {
    return level;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  public void addLevel(int level) {
    this.level += level;
  }

  public Player getPlayer() {
    return player;
  }

  public Map<Head, Integer> getOwnedHeads() {
    return ownedHeads;
  }

  public void addHeadsAmount(Head head, int amount) {
    ownedHeads.put(head, ownedHeads.getOrDefault(head, 0) + amount);
  }

  public int getOwnedHeads(Head head) {
    return ownedHeads.getOrDefault(head, 0);
  }

  public void loadOwnedHead(Head head, int value) {
    ownedHeads.put(head, value);
  }

}
