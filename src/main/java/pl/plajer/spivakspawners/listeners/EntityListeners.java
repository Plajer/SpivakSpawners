package pl.plajer.spivakspawners.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;
import pl.plajer.spivakspawners.Main;
import pl.plajer.spivakspawners.registry.spawner.data.SpawnerPerk;
import pl.plajer.spivakspawners.utils.Utils;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Plajer
 * <p>
 * Created at 13.02.2019
 */
public class EntityListeners implements Listener {

    private Main plugin;

    public EntityListeners(Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        if(!e.getEntity().hasMetadata("SpivakSpawnersEntity")) {
            return;
        }
        int merged = e.getEntity().getMetadata("SpivakSpawnersEntitiesMerged").get(0).asInt();
        String displayName = plugin.getLanguageManager().color("Merged.Entity-Name")
                .replace("%mob%", e.getEntity().getType().getName())
                .replace("%number%", String.valueOf(merged - 1));
        if(merged - 1 > 0) {
            //update died entity display name cause it's visible for few seconds after death
            //and we want to display user real amount, visibility disable doesn't work
            e.getEntity().setCustomName(displayName);

            Entity en = e.getEntity().getWorld().spawnEntity(e.getEntity().getLocation(), e.getEntityType());
            Utils.noAI(en);
            en.setCustomName(displayName);
            en.setCustomNameVisible(true);
            //pass all metadata values to the new entity
            en.setMetadata("SpivakSpawnersEntity", new FixedMetadataValue(plugin, true));
            en.setMetadata("SpivakSpawnersEntitiesMerged", new FixedMetadataValue(plugin, merged - 1));
            for(SpawnerPerk perk : SpawnerPerk.values()) {
                if(!e.getEntity().hasMetadata(perk.getMetadataAccessor())) {
                    continue;
                }
                en.setMetadata(perk.getMetadataAccessor(), new FixedMetadataValue(plugin, true));
            }
        }
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        if(rand.nextInt(0, 100) <= 10) {
            //todo drop the mob head
        }
        for(SpawnerPerk perk : SpawnerPerk.values()) {
            if(!e.getEntity().hasMetadata(perk.getMetadataAccessor())) {
                continue;
            }
            switch(perk) {
                case BONUS_LOOT_10:
                    if(rand.nextInt(0, 100) > 10) {
                        break;
                    }
                    //drop one item more from list of drops
                    e.getEntity().getWorld().dropItemNaturally(e.getEntity().getLocation(), e.getDrops().get(rand.nextInt(e.getDrops().size())));
                    break;
                case BONUS_LOOT_20:
                    if(rand.nextInt(0, 100) > 15) {
                        break;
                    }
                    //drop one item more from list of drops but do this twice
                    e.getEntity().getWorld().dropItemNaturally(e.getEntity().getLocation(), e.getDrops().get(rand.nextInt(e.getDrops().size())));
                    e.getEntity().getWorld().dropItemNaturally(e.getEntity().getLocation(), e.getDrops().get(rand.nextInt(e.getDrops().size())));
                    break;
                case BONUS_HEADS_1:
                    if(rand.nextInt(0, 100) <= 10) {
                        //todo drop the mob head
                    }
                    break;
                case BONUS_XP:
                    e.setDroppedExp((int) (e.getDroppedExp() * rand.nextDouble(1.0, 1.5)));
                    break;
                case BONUS_HEADS_2:
                    if(rand.nextInt(0, 100) <= 20) {
                        //todo drop the mob head
                    }
                    break;
                case BONUS_LOOT_50:
                    //higher chance of drop here
                    if(rand.nextInt(0, 100) > 50) {
                        break;
                    }
                    //drop one item more from list of drops
                    e.getEntity().getWorld().dropItemNaturally(e.getEntity().getLocation(), e.getDrops().get(rand.nextInt(e.getDrops().size())));
                    break;
                case DOUBLE_XP:
                    e.setDroppedExp(e.getDroppedExp() * 2);
                    break;
            }
        }
    }

}
