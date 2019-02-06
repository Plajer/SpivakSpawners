package pl.plajer.spivakspawners.user;

import org.bukkit.entity.Player;

import java.util.List;

/**
 * @author Plajer
 * <p>
 * Created at 06.02.2019
 */
public class User {

    private Player player;
    private int tokens = 0;

    public User(Player player) {
        this.player = player;
    }

    public void setCurrentPrefix(String currentPrefix) {
        this.currentPrefix = currentPrefix;
    }

    public String getCurrentPrefix() {
        return currentPrefix;
    }

    public boolean hasPrefix() {
        return currentPrefix != null && !currentPrefix.equals("");
    }

    public void addCustomTag(String tag) {
        customTags.add(tag);
    }

    public List<String> getCustomTags() {
        return customTags;
    }

    public int getTokens() {
        return tokens;
    }

    public void setTokens(int tokens) {
        this.tokens = tokens;
    }

    public void addTokens(int tokens) {
        this.tokens += tokens;
    }

    public Player getPlayer() {
        return player;
    }
}
