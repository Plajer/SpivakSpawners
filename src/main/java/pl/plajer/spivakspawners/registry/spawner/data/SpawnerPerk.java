package pl.plajer.spivakspawners.registry.spawner.data;

/**
 * @author Plajer
 * <p>
 * Created at 12.02.2019
 */
public enum SpawnerPerk {

  BONUS_LOOT_10(4, "BonusLoot10"), BONUS_LOOT_20(8, "BonusLoot20"), BONUS_HEADS_1(12, "BonusHeads1"),
  BONUS_XP(16, "BonusXP"), BONUS_HEADS_2(20, "BonusHeads2"), BONUS_LOOT_50(24, "BonusLoot50"), DOUBLE_XP(28, "DoubleXP");

  private int level;
  private String metadataAccessor;

  SpawnerPerk(int level, String metadataAccessor) {
    this.level = level;
    this.metadataAccessor = metadataAccessor;
  }

  public String getMetadataAccessor() {
    return "SpivakSpawners" + metadataAccessor;
  }

  public int getLevel() {
    return level;
  }

}
