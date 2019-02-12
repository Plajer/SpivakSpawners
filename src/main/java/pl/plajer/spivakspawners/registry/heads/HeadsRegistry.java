package pl.plajer.spivakspawners.registry.heads;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import pl.plajer.spivakspawners.Main;
import pl.plajerlair.core.utils.ConfigUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Plajer
 * <p>
 * Created at 06.02.2019
 */
public class HeadsRegistry {

    private List<Head> heads = new ArrayList<>();
    private Main plugin;

    public HeadsRegistry(Main plugin) {
        this.plugin = plugin;
        registerHeads();
    }

    private void registerHeads() {
        FileConfiguration config = ConfigUtils.getConfig(plugin, "spawners");

        for(String key : config.getConfigurationSection("Spawners").getKeys(false)) {
            heads.add(new Head(EntityType.valueOf(config.getString("Spawners." + key + ".Type").toUpperCase())));
        }
    }

    public Head getByEntityType(EntityType type) {
        for(Head head : heads) {
            if(head.getEntityType() == type) {
                return head;
            }
        }
        //return base head in case of null
        return getByEntityType(EntityType.PIG);
    }

    public List<Head> getHeads() {
        return heads;
    }
}
