package pl.plajer.spivakspawners.registry.spawner.data;

/**
 * @author Plajer
 * <p>
 * Created at 12.02.2019
 */
public enum SpawnerPerk {

    BONUS_LOOT_10(4), BONUS_LOOT_20(8), BONUS_HEADS_1(12), BONUS_XP(16), BONUS_HEADS_2(20), BONUS_LOOT_50(24), DOUBLE_XP(28);

    private int level;

    SpawnerPerk(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }

}
