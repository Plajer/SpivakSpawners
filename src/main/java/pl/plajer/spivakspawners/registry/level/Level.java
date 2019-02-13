package pl.plajer.spivakspawners.registry.level;

import pl.plajer.spivakspawners.registry.heads.Head;

/**
 * @author Plajer
 * <p>
 * Created at 12.02.2019
 */
public class Level {

  private int level;
  private Head head;
  private int headsNeeded;
  private int moneyNeeded;

  public Level(int level, Head head, int headsNeeded, int moneyNeeded) {
    this.level = level;
    this.head = head;
    this.headsNeeded = headsNeeded;
    this.moneyNeeded = moneyNeeded;
  }

  public int getLevel() {
    return level;
  }

  public Head getHead() {
    return head;
  }

  public int getHeadsNeeded() {
    return headsNeeded;
  }

  public int getMoneyNeeded() {
    return moneyNeeded;
  }
}
