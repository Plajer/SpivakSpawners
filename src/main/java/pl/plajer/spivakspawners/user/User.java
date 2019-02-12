package pl.plajer.spivakspawners.user;

import org.bukkit.entity.Player;

/**
 * @author Plajer
 * <p>
 * Created at 12.02.2019
 */
public class User {

    private Player player;
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

}
