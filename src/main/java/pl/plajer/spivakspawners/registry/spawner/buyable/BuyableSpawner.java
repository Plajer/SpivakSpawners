package pl.plajer.spivakspawners.registry.spawner.buyable;

import org.bukkit.entity.EntityType;
import pl.plajer.spivakspawners.registry.heads.Head;

/**
 * @author Plajer
 * <p>
 * Created at 12.02.2019
 */
public class BuyableSpawner {

    private EntityType type;
    private int levelNeeded;
    private int moneyRequired;
    private Head headTypeRequired;

    public BuyableSpawner(EntityType type, int levelNeeded, int moneyRequired, Head headTypeRequired) {
        this.type = type;
        this.levelNeeded = levelNeeded;
        this.moneyRequired = moneyRequired;
        this.headTypeRequired = headTypeRequired;
    }

    public EntityType getType() {
        return type;
    }

    public int getLevelNeeded() {
        return levelNeeded;
    }

    public int getMoneyRequired() {
        return moneyRequired;
    }

    public Head getHeadTypeRequired() {
        return headTypeRequired;
    }

}
