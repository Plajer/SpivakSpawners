package pl.plajer.spivakspawners.listeners;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import pl.plajer.spivakspawners.Main;
import pl.plajer.spivakspawners.menus.SpawnerUpgradeMenu;
import pl.plajer.spivakspawners.registry.spawner.data.SpawnerData;
import pl.plajer.spivakspawners.registry.spawner.data.SpawnerPerk;
import pl.plajer.spivakspawners.registry.spawner.living.Spawner;
import pl.plajer.spivakspawners.registry.spawner.living.SpawnerEntity;
import pl.plajer.spivakspawners.utils.Utils;
import pl.plajerlair.core.utils.ItemBuilder;

/**
 * @author Plajer
 * <p>
 * Created at 12.02.2019
 */
public class SpawnerListeners implements Listener {

  private Main plugin;

  public SpawnerListeners(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
  }

  @EventHandler
  public void onSpawnerClick(PlayerInteractEvent e) {
    if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }
    if (e.getClickedBlock() == null || e.getClickedBlock().getType() != Material.MOB_SPAWNER) {
      return;
    }
    for (Spawner spawner : plugin.getSpawnersStorage().getSpawnedSpawners()) {
      if (!e.getClickedBlock().getLocation().equals(spawner.getLocation())) {
        continue;
      }
      //open up spawner menu
      new SpawnerUpgradeMenu(spawner, e.getPlayer());
    }
  }

  @EventHandler
  public void onSpawnerBreak(BlockBreakEvent e) {
    if (e.getBlock().getType() != Material.MOB_SPAWNER) {
      return;
    }
    for (Spawner spawner : plugin.getSpawnersStorage().getSpawnedSpawners()) {
      if (!e.getBlock().getLocation().equals(spawner.getLocation())) {
        continue;
      }
      if (!spawner.getOwner().equals(e.getPlayer().getUniqueId())) {
        e.getPlayer().sendMessage(plugin.getLanguageManager().color("Messages.Not-Your-Spawner"));
        e.setCancelled(true);
        return;
      }
      e.setExpToDrop(0);
      if (e.getPlayer().getItemInHand().getEnchantments().containsKey(Enchantment.SILK_TOUCH)) {
        CreatureSpawner creatureSpawner = (CreatureSpawner) e.getBlock().getState();
        e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), new ItemBuilder(new ItemStack(e.getBlock().getType(),
            spawner.getSpawnerData().getSpawnerLevel()))
            .name(plugin.getLanguageManager().color("Drop-Item.Name").replace("%mob%", creatureSpawner.getCreatureTypeName()))
            .lore(plugin.getLanguageManager().color("Drop-Item.Lore").split(";"))
            .build());
        return;
      }
      e.getPlayer().sendMessage(plugin.getLanguageManager().color("Messages.Only-Silk-Touch"));
      return;
    }
  }

  @EventHandler
  public void onSpawnerPlace(BlockPlaceEvent e) {
    if (e.getBlockPlaced() == null || e.getBlockPlaced().getType() != Material.MOB_SPAWNER) {
      return;
    }
    if (!e.getItemInHand().hasItemMeta() || !e.getItemInHand().getItemMeta().hasDisplayName() || !e.getItemInHand().getItemMeta().hasLore()) {
      return;
    }
    if (!e.getItemInHand().getItemMeta().getLore().equals(Arrays.asList(plugin.getLanguageManager().color("Drop-Item.Lore").split(";")))) {
      return;
    }
    String mob = ChatColor.stripColor(e.getPlayer().getItemInHand().getItemMeta().getDisplayName());
    mob = mob.replace(" Spawner", "");
    for (Block block : Utils.getNearbyBlocks(e.getBlockPlaced().getLocation(), 3)) {
      if (block.getType() != Material.MOB_SPAWNER) {
        continue;
      }
      CreatureSpawner creatureSpawner = (CreatureSpawner) block.getState();
      if (!creatureSpawner.getCreatureTypeName().equals(mob)) {
        continue;
      }
      Spawner spawner = plugin.getSpawnersStorage().getByLocation(block.getLocation());
      if (spawner == null || spawner.getSpawnerData().getSpawnerLevel() == SpawnerData.MAX_UPGRADE_LEVEL) {
        continue;
      }
      e.setCancelled(true);
      e.getItemInHand().setAmount(e.getItemInHand().getAmount() - 1);
      spawner.getSpawnerData().setSpawnerLevel(spawner.getSpawnerData().getSpawnerLevel() + 1);
      e.getPlayer().sendMessage(plugin.getLanguageManager().color("Messages.Spawner-Merged")
          .replace("%mob%", spawner.getSpawnerData().getEntityType().getName()));
      return;
    }
    CreatureSpawner creatureSpawner = (CreatureSpawner) e.getBlockPlaced().getState();
    creatureSpawner.setCreatureTypeByName(mob);
    creatureSpawner.setDelay(50);
    Spawner spawner = new Spawner(e.getPlayer().getUniqueId(), e.getBlockPlaced().getLocation(), creatureSpawner.getSpawnedType());
    e.getBlockPlaced().getWorld().strikeLightningEffect(e.getBlockPlaced().getLocation());
    plugin.getSpawnersStorage().getSpawnedSpawners().add(spawner);
    e.getPlayer().sendMessage(plugin.getLanguageManager().color("Messages.Spawner-Placed")
        .replace("%mob%", spawner.getSpawnerData().getEntityType().getName()));
  }

  @EventHandler
  public void onSpawnerSpawn(SpawnerSpawnEvent e) {
    if (e.getSpawner() == null) {
      //still spawned via spawner but 1.8 is buggy so it's not in the event...
      //just recompensate this with higher spawn delay
      e.setCancelled(true);
      return;
    }
    Spawner spawner = plugin.getSpawnersStorage().getByLocation(e.getSpawner().getLocation());
    if (spawner == null) {
      return;
    }
    e.getSpawner().setDelay(100);
    Utils.setNoAI(e.getEntity());
    Entity mergeableWith = plugin.getMergeHandler().getNearbyMergeable(e.getEntity());
    if (mergeableWith != null) {
      int mergedEntities = mergeableWith.getMetadata("SpivakSpawnersEntitiesMerged").get(0).asInt();
      SpawnerEntity spawnerEntity = plugin.getSpawnersStorage().getSpawnerEntity(mergeableWith);
      spawnerEntity.getHologram().clearLines();
      spawnerEntity.getHologram().appendTextLine(plugin.getLanguageManager().color("Merged.Entity-Name")
          .replace("%mob%", mergeableWith.getType().getName())
          .replace("%number%", String.valueOf(mergedEntities + 1)));
      mergeableWith.setMetadata("SpivakSpawnersEntitiesMerged", new FixedMetadataValue(plugin, mergedEntities + 1));
      e.setCancelled(true);
      return;
    }
    e.getEntity().setMetadata("SpivakSpawnersEntity", new FixedMetadataValue(plugin, true));
    e.getEntity().setMetadata("SpivakSpawnersEntitiesMerged", new FixedMetadataValue(plugin, 1));
    for (SpawnerPerk perk : spawner.getPerks()) {
      e.getEntity().setMetadata(perk.getMetadataAccessor(), new FixedMetadataValue(plugin, true));
    }
    e.getEntity().setCustomNameVisible(true);
    Hologram hologram = HologramsAPI.createHologram(plugin, e.getEntity().getLocation().add(0, 2, 0));
    plugin.getSpawnersStorage().getSpawnerEntities().add(new SpawnerEntity(hologram, e.getEntity()));
  }

}
