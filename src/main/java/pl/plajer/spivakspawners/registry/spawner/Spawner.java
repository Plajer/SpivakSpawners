package pl.plajer.spivakspawners.registry.spawner;

import org.bukkit.entity.EntityType;
import pl.plajer.spivakspawners.user.User;

/**
 * @author Plajer
 * <p>
 * Created at 06.02.2019
 */
public class Spawner {

    private User owner;
    private EntityType type;
    private int level = 1;
    private int stackLevel = 0;

    public Spawner(User owner, EntityType type) {
        this.owner = owner;
        this.type = type;
    }

    public User getOwner() {
        return owner;
    }

    public EntityType getType() {
        return type;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getStackLevel() {
        return stackLevel;
    }

    public void setStackLevel(int stackLevel) {
        this.stackLevel = stackLevel;
    }
}
