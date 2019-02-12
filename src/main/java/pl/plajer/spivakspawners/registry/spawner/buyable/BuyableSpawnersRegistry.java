package pl.plajer.spivakspawners.registry.spawner.buyable;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import pl.plajer.spivakspawners.Main;
import pl.plajer.spivakspawners.registry.heads.Head;
import pl.plajer.spivakspawners.registry.spawner.buyable.BuyableSpawner;
import pl.plajerlair.core.utils.ConfigUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

/**
 * @author Plajer
 * <p>
 * Created at 10.02.2019
 */
public class BuyableSpawnersRegistry {

    private List<BuyableSpawner> buyableSpawners = new ArrayList<>();
    private Main plugin;

    public BuyableSpawnersRegistry(Main plugin) {
        this.plugin = plugin;
        registerBuyableSpawners();
    }

    /**
     * Registers all buyable spawner types from spawners.yml
     */
    private void registerBuyableSpawners() {
        FileConfiguration config = ConfigUtils.getConfig(plugin, "spawners");

        for(String key : config.getConfigurationSection("Spawners").getKeys(false)) {
            String access = "Spawners." + key + ".";
            EntityType entity = EntityType.valueOf(config.getString(access + "Type"));
            Head relatedHead = null;
            for(Head head : plugin.getHeadsRegistry().getHeads()) {
                if(head.getEntityType().equals(entity)) {
                    relatedHead = head;
                    break;
                }
            }
            if(relatedHead == null) {
                plugin.getLogger().log(Level.WARNING, "No related head type found for spawner " + key);
                continue;
            }
            buyableSpawners.add(new BuyableSpawner(entity, config.getInt(access + "Level"), config.getInt(access + "Cost"), relatedHead));
        }
    }

    public List<BuyableSpawner> getBuyableSpawners() {
        return buyableSpawners;
    }
}
