package pl.plajer.spivakspawners.listeners;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import pl.plajer.spivakspawners.Main;
import pl.plajer.spivakspawners.registry.spawner.data.CustomDrop;
import pl.plajer.spivakspawners.registry.spawner.data.SpawnerPerk;
import pl.plajer.spivakspawners.utils.EntityDisplayNameFixer;
import pl.plajer.spivakspawners.utils.Utils;

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
    if (!e.getEntity().hasMetadata("SpivakSpawnersEntity")) {
      return;
    }
    if (plugin.getConfig().getBoolean("Mob-Merge-Enabled")) {
      int merged = e.getEntity().getMetadata("SpivakSpawnersEntitiesMerged").get(0).asInt();
      if (merged - 1 > 0) {
        LivingEntity en = (LivingEntity) e.getEntity().getWorld().spawnEntity(e.getEntity().getLocation(), e.getEntityType());
        Utils.setNoAI(en);

        if (e.getEntity() instanceof Slime) {
          ((Slime) en).setSize(2);
        }
        if (e.getEntity() instanceof Ageable) {
          ((Ageable) en).setAdult();
        }

        en.setCustomName(plugin.getLanguageManager().color("Merged.Entity-Name")
            .replace("%mob%", EntityDisplayNameFixer.fixDisplayName(e.getEntityType()))
            .replace("%number%", String.valueOf(merged - 1)));
        en.setCustomNameVisible(true);
        plugin.getSpawnersStorage().getSpawnerEntities().add(en);

        //pass all metadata values to the new entity
        en.setMetadata("SpivakSpawnersEntity", new FixedMetadataValue(plugin, true));
        en.setMetadata("SpivakSpawnersEntitiesMerged", new FixedMetadataValue(plugin, merged - 1));
        for (SpawnerPerk perk : SpawnerPerk.values()) {
          if (!e.getEntity().hasMetadata(perk.getMetadataAccessor())) {
            continue;
          }
          en.setMetadata(perk.getMetadataAccessor(), new FixedMetadataValue(plugin, true));
        }
      }
    }
    applyDeathLoot(e);
    plugin.getSpawnersStorage().getSpawnerEntities().remove(e.getEntity());
  }

  private void applyDeathLoot(EntityDeathEvent e) {
    ThreadLocalRandom rand = ThreadLocalRandom.current();
    if (rand.nextInt(0, 100) <= 10) {
      e.getDrops().add(plugin.getHeadsRegistry().getByEntityType(e.getEntityType()).getItemStack());
    }
    Entity en = e.getEntity();
    List<ItemStack> drops = e.getDrops();
    if (plugin.getConfig().getBoolean("Spawners-Merge-Enabled")) {
      for (SpawnerPerk perk : SpawnerPerk.values()) {
        if (!en.hasMetadata(perk.getMetadataAccessor())) {
          continue;
        }
        switch (perk) {
          case BONUS_LOOT_10:
            if (rand.nextInt(0, 100) > 10) {
              break;
            }
            //drop one item more from list of drops
            en.getWorld().dropItemNaturally(en.getLocation(), drops.get(rand.nextInt(drops.size())));
            break;
          case BONUS_LOOT_20:
            if (rand.nextInt(0, 100) > 20) {
              break;
            }
            en.getWorld().dropItemNaturally(en.getLocation(), drops.get(rand.nextInt(drops.size())));
            break;
          case BONUS_HEADS_1:
            if (rand.nextInt(0, 100) <= 10) {
              e.getDrops().add(plugin.getHeadsRegistry().getByEntityType(e.getEntityType()).getItemStack());
            }
            break;
          case BONUS_XP:
            e.setDroppedExp((int) (e.getDroppedExp() * rand.nextDouble(1.0, 1.5)));
            break;
          case BONUS_HEADS_2:
            if (rand.nextInt(0, 100) <= 20) {
              e.getDrops().add(plugin.getHeadsRegistry().getByEntityType(e.getEntityType()).getItemStack());
            }
            break;
          case BONUS_LOOT_50:
            //higher chance of drop here
            if (rand.nextInt(0, 100) > 50) {
              break;
            }
            //drop one item more from list of drops
            en.getWorld().dropItemNaturally(en.getLocation(), drops.get(rand.nextInt(drops.size())));
            break;
          case DOUBLE_XP:
            e.setDroppedExp(e.getDroppedExp() * 2);
            break;
        }
      }
    }
    for (CustomDrop drop : plugin.getSpawnerListeners().getCustomDrops().get(en.getType())) {
      if (drop.getChance() <= rand.nextInt(0, 100)) {
        e.getDrops().add(drop.getDrop());
      }
    }
  }

}
