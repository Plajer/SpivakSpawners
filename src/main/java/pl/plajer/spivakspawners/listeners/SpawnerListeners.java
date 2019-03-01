package pl.plajer.spivakspawners.listeners;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.TileEntityMobSpawner;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
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
import pl.plajer.spivakspawners.registry.spawner.data.CustomDrop;
import pl.plajer.spivakspawners.registry.spawner.data.SpawnerData;
import pl.plajer.spivakspawners.registry.spawner.data.SpawnerPerk;
import pl.plajer.spivakspawners.registry.spawner.living.Spawner;
import pl.plajer.spivakspawners.utils.EntityDisplayNameFixer;
import pl.plajer.spivakspawners.utils.Utils;
import pl.plajerlair.core.utils.ConfigUtils;
import pl.plajerlair.core.utils.ItemBuilder;

/**
 * @author Plajer
 * <p>
 * Created at 12.02.2019
 */
public class SpawnerListeners implements Listener {

  private Map<EntityType, List<CustomDrop>> customDrops = new HashMap<>();
  private Main plugin;

  public SpawnerListeners(Main plugin) {
    this.plugin = plugin;
    plugin.getServer().getPluginManager().registerEvents(this, plugin);
    FileConfiguration config = ConfigUtils.getConfig(plugin, "spawners");
    for (String key : config.getConfigurationSection("Spawners").getKeys(false)) {
      customDrops.put(EntityType.valueOf(config.getString("Spawners." + key + ".Type").toUpperCase()),
          config.getStringList("Spawners." + key + ".Custom-Drops").stream()
              .map(drop -> new CustomDrop(new ItemStack(Material.matchMaterial(drop.split(":")[0])),
                  Double.parseDouble(drop.split(":")[1]))).collect(Collectors.toList()));
    }
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

    int handSpawnerLevel = 1;
    for (String lore : e.getItemInHand().getItemMeta().getLore()) {
      if (!lore.matches(plugin.getLanguageManager().color("Drop-Item.Level-Lore").replace("%level%", ".*?"))) {
        continue;
      }
      handSpawnerLevel = Integer.parseInt(lore.replaceAll("[^0-9]", ""));
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
      if (spawner.getSpawnerData().getSpawnerLevel() + handSpawnerLevel > SpawnerData.MAX_UPGRADE_LEVEL) {
        continue;
      }
      e.setCancelled(true);
      if (e.getItemInHand().getAmount() > 1) {
        e.getItemInHand().setAmount(e.getItemInHand().getAmount() - 1);
      } else {
        e.getPlayer().getInventory().remove(e.getItemInHand());
      }
      spawner.getSpawnerData().setSpawnerLevel(spawner.getSpawnerData().getSpawnerLevel() + handSpawnerLevel);
      if (spawner.shouldApplyPerk()) {
        spawner.addPerk(SpawnerPerk.values()[(spawner.getSpawnerData().getSpawnerLevel() / 4) - 1]);
      }
      if (spawner.getSpawnerData().getSpawnerLevel() == SpawnerData.MAX_UPGRADE_LEVEL) {
        e.getPlayer().sendMessage(plugin.getLanguageManager().color("Messages.Spawner-Maxed"));
        return;
      }
      e.getPlayer().sendMessage(plugin.getLanguageManager().color("Messages.Spawner-Merged")
          .replace("%mob%", EntityDisplayNameFixer.fixDisplayName(spawner.getSpawnerData().getEntityType())));
      return;
    }
    CreatureSpawner creatureSpawner = (CreatureSpawner) e.getBlockPlaced().getState();
    creatureSpawner.setCreatureTypeByName(mob);
    Spawner spawner = new Spawner(e.getPlayer().getUniqueId(), e.getBlockPlaced().getLocation(), creatureSpawner.getSpawnedType(),
        customDrops.get(creatureSpawner.getSpawnedType()));
    spawner.getSpawnerData().setSpawnerLevel(handSpawnerLevel);
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
    if (mergeableWith != null) {
      int mergedEntities = mergeableWith.getMetadata("SpivakSpawnersEntitiesMerged").get(0).asInt();
      mergeableWith.setCustomName(plugin.getLanguageManager().color("Merged.Entity-Name")
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
    plugin.getSpawnersStorage().getSpawnerEntities().add(e.getEntity());

    if (e.getEntity() instanceof Slime) {
      ((Slime) e.getEntity()).setSize(2);
    }
    if (e.getEntity() instanceof Ageable) {
      ((Ageable) e.getEntity()).setAdult();
    }
  }

  private void dropSpawnerItem(CreatureSpawner spawner, int amount) {
    String type = EntityDisplayNameFixer.fixDisplayName(spawner.getSpawnedType());
    spawner.getLocation().getWorld().dropItemNaturally(spawner.getLocation(), new ItemBuilder(new ItemStack(Material.MOB_SPAWNER))
        .name(plugin.getLanguageManager().color("Drop-Item.Name").replace("%mob%", type))
        .lore(plugin.getLanguageManager().color("Drop-Item.Lore")
            .replace("%mob%", type)
            .split(";"))
        .lore(plugin.getLanguageManager().color("Drop-Item.Level-Lore")
            .replace("%level%", String.valueOf(amount))
            .split(";"))
        .build());
  }

  public Map<EntityType, List<CustomDrop>> getCustomDrops() {
    return customDrops;
  }
}
