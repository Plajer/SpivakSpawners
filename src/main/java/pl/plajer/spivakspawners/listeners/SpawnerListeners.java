package pl.plajer.spivakspawners.listeners;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import java.util.Arrays;

import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.TileEntityMobSpawner;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
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
import pl.plajer.spivakspawners.utils.EntitiesHologramHeights;
import pl.plajer.spivakspawners.utils.EntityDisplayNameFixer;
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
  public void onSpawnerExplode(BlockExplodeEvent e) {
    if (e.getBlock().getType() != Material.MOB_SPAWNER) {
      return;
    }
    for (Spawner spawner : plugin.getSpawnersStorage().getSpawnedSpawners()) {
      if (!e.getBlock().getLocation().equals(spawner.getLocation())) {
        continue;
      }
      e.setCancelled(true);
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
      /* can be kept for future
      if (!spawner.getOwner().equals(e.getPlayer().getUniqueId())) {
        e.getPlayer().sendMessage(plugin.getLanguageManager().color("Messages.Not-Your-Spawner"));
        e.setCancelled(true);
        return;
      }*/
      boolean fullDestroy = e.getPlayer().isSneaking();
      boolean hasSilkTouch = e.getPlayer().getItemInHand().getEnchantments().containsKey(Enchantment.SILK_TOUCH);
      CreatureSpawner creatureSpawner = (CreatureSpawner) e.getBlock().getState();
      int spawnerLevel = spawner.getSpawnerData().getSpawnerLevel();
      if (spawnerLevel - 1 > 0 && !fullDestroy) {
        e.setCancelled(true);
        spawner.getSpawnerData().setSpawnerLevel(spawnerLevel - 1);
        if (hasSilkTouch) {
          dropSpawnerItem(creatureSpawner, 1);
        } else {
          e.getPlayer().sendMessage(plugin.getLanguageManager().color("Messages.Only-Silk-Touch"));
        }
        return;
      } else {
        if (hasSilkTouch) {
          dropSpawnerItem(creatureSpawner, spawnerLevel);
        } else {
          e.getPlayer().sendMessage(plugin.getLanguageManager().color("Messages.Only-Silk-Touch"));
        }
        plugin.getSpawnersStorage().getSpawnedSpawners().remove(spawner);
        return;
      }
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
    mob = EntityDisplayNameFixer.fromFixedDisplayName(mob.replace(" Spawner", ""));
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
      if (e.getItemInHand().getAmount() > 1) {
        e.getItemInHand().setAmount(e.getItemInHand().getAmount() - 1);
      } else {
        e.getPlayer().getInventory().remove(e.getItemInHand());
      }
      spawner.getSpawnerData().setSpawnerLevel(spawner.getSpawnerData().getSpawnerLevel() + 1);
      if (spawner.shouldApplyPerk()) {
        spawner.addPerk(SpawnerPerk.values()[(spawner.getSpawnerData().getSpawnerLevel() / 4) - 1]);
      }
      e.getPlayer().sendMessage(plugin.getLanguageManager().color("Messages.Spawner-Merged")
          .replace("%mob%", EntityDisplayNameFixer.fixDisplayName(spawner.getSpawnerData().getEntityType())));
      return;
    }
    CreatureSpawner creatureSpawner = (CreatureSpawner) e.getBlockPlaced().getState();
    creatureSpawner.setCreatureTypeByName(mob);
    Spawner spawner = new Spawner(e.getPlayer().getUniqueId(), e.getBlockPlaced().getLocation(), creatureSpawner.getSpawnedType());
    plugin.getSpawnersStorage().getSpawnedSpawners().add(spawner);
    e.getPlayer().sendMessage(plugin.getLanguageManager().color("Messages.Spawner-Placed")
        .replace("%mob%", EntityDisplayNameFixer.fixDisplayName(spawner.getSpawnerData().getEntityType())));
  }

  @EventHandler
  public void onSpawnerSpawn(SpawnerSpawnEvent e) {
    Location spawnerLocation = null;
    if (e.getSpawner() == null) {
      //still spawned via spawner but 1.8 is buggy so it's not in the event...
      //we must look for nearest spawner of same type
      for (Block block : Utils.getNearbyBlocks(e.getLocation(), 5)) {
        if (block.getType() != Material.MOB_SPAWNER) {
          continue;
        }
        Spawner spawner = plugin.getSpawnersStorage().getByLocation(block.getLocation());
        if (spawner == null || spawner.getSpawnerData().getEntityType() != e.getEntityType()) {
          continue;
        }
        spawnerLocation = block.getLocation();
      }
      //no similar spawners found in the area
      if (spawnerLocation == null) {
        return;
      }
    } else {
      spawnerLocation = e.getSpawner().getLocation();
    }
    Spawner spawner = plugin.getSpawnersStorage().getByLocation(spawnerLocation);
    if (spawner == null) {
      return;
    }

    BlockPosition blockPos = new BlockPosition(spawner.getLocation().getBlockX(), spawner.getLocation().getBlockY(), spawner.getLocation().getBlockZ());
    TileEntityMobSpawner tileSpawner = (TileEntityMobSpawner) ((CraftWorld) spawner.getLocation().getWorld()).getHandle().getTileEntity(blockPos);
    NBTTagCompound tag = new NBTTagCompound();
    tag.setString("EntityId", tileSpawner.getSpawner().getMobName());
    tag.setShort("Delay", (short) 100);
    tag.setShort("MinSpawnDelay", (short) 100);
    tag.setShort("MaxSpawnDelay", (short) 160);
    tag.setShort("SpawnCount", (short) 4);
    tag.setShort("MaxNearbyEntities", (short) 10);
    tag.setShort("RequiredPlayerRange", (short) 20);
    tag.setShort("SpawnRange", (short) 5);
    tileSpawner.getSpawner().a(tag);

    Utils.setNoAI((LivingEntity) e.getEntity());
    Entity mergeableWith = plugin.getMergeHandler().getNearbyMergeable(e.getEntity());
    SpawnerEntity spawnerEntity = plugin.getSpawnersStorage().getSpawnerEntity(mergeableWith);
    if (mergeableWith != null && spawnerEntity != null) {
      int mergedEntities = mergeableWith.getMetadata("SpivakSpawnersEntitiesMerged").get(0).asInt();
      spawnerEntity.getHologram().clearLines();
      spawnerEntity.getHologram().appendTextLine(plugin.getLanguageManager().color("Merged.Entity-Name")
          .replace("%mob%", EntityDisplayNameFixer.fixDisplayName(mergeableWith.getType()))
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
    Hologram hologram = HologramsAPI.createHologram(plugin, e.getEntity().getLocation().add(0,
        EntitiesHologramHeights.valueOf(e.getEntityType().name()).getHeight(), 0));
    plugin.getSpawnersStorage().getSpawnerEntities().add(new SpawnerEntity(hologram, e.getEntity()));

    if (e.getEntity() instanceof Slime) {
      ((Slime) e.getEntity()).setSize(2);
    }
    if (e.getEntity() instanceof Ageable) {
      ((Ageable) e.getEntity()).setAdult();
    }
  }

  private void dropSpawnerItem(CreatureSpawner spawner, int amount) {
    spawner.getLocation().getWorld().dropItemNaturally(spawner.getLocation(), new ItemBuilder(new ItemStack(Material.MOB_SPAWNER,
        amount))
        .name(plugin.getLanguageManager().color("Drop-Item.Name").replace("%mob%", EntityDisplayNameFixer.fixDisplayName(spawner.getSpawnedType())))
        .lore(plugin.getLanguageManager().color("Drop-Item.Lore").split(";"))
        .build());
  }

}
