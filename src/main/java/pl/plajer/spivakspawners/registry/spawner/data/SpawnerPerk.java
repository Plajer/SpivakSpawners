package pl.plajer.spivakspawners.registry.spawner.data;

import org.bukkit.plugin.java.JavaPlugin;

import pl.plajer.spivakspawners.Main;

/**
 * @author Plajer
 * <p>
 * Created at 12.02.2019
 */
public enum SpawnerPerk {

  BONUS_LOOT_10(4, "BonusLoot10"), BONUS_LOOT_20(8, "BonusLoot20"), BONUS_HEADS_1(12, "BonusHeads1"),
  BONUS_XP(16, "BonusXP"), BONUS_HEADS_2(20, "BonusHeads2"), BONUS_LOOT_50(24, "BonusLoot50"),
  DOUBLE_XP(28, "DoubleXP");

  private static Main plugin = JavaPlugin.getPlugin(Main.class);
  private int level;
  private String metadataAccessor;

  static {
    BONUS_LOOT_10.formattedName = plugin.getLanguageManager().color("Perk-Rewards.Bonus-Loot-10");
    BONUS_LOOT_20.formattedName = plugin.getLanguageManager().color("Perk-Rewards.Bonus-Loot-20");
    BONUS_HEADS_1.formattedName = plugin.getLanguageManager().color("Perk-Rewards.Bonus-Heads-1");
    BONUS_XP.formattedName = plugin.getLanguageManager().color("Perk-Rewards.Bonus-Xp");
    BONUS_HEADS_2.formattedName = plugin.getLanguageManager().color("Perk-Rewards.Bonus-Heads-2");
    BONUS_LOOT_50.formattedName = plugin.getLanguageManager().color("Perk-Rewards.Bonus-Loot-50");
    DOUBLE_XP.formattedName = plugin.getLanguageManager().color("Perk-Rewards.Double-Xp");
  }

  private String formattedName;

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

  public String getFormattedName() {
    return formattedName;
  }

}
