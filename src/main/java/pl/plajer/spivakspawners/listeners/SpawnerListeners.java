package pl.plajer.spivakspawners.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import pl.plajer.spivakspawners.Main;
import pl.plajer.spivakspawners.menus.SpawnerUpgradeMenu;
import pl.plajer.spivakspawners.registry.spawner.living.Spawner;
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
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if(e.getClickedBlock() == null || e.getClickedBlock().getType() != Material.MOB_SPAWNER) {
            return;
        }
        for(Spawner spawner : plugin.getSpawnersStorage().getSpawnedSpawners()) {
            if(!e.getClickedBlock().getLocation().equals(spawner.getLocation())) {
                continue;
            }
            //open up spawner menu
            new SpawnerUpgradeMenu(spawner, e.getPlayer());
        }
    }

    @EventHandler
    public void onSpawnerBreak(BlockBreakEvent e) {
        if(e.getBlock().getType() != Material.MOB_SPAWNER) {
            return;
        }
        for(Spawner spawner : plugin.getSpawnersStorage().getSpawnedSpawners()) {
            if(!e.getBlock().getLocation().equals(spawner.getLocation())) {
                continue;
            }
            if(!spawner.getOwner().equals(e.getPlayer().getUniqueId())) {
                e.getPlayer().sendMessage(plugin.getLanguageManager().color("Messages.Not-Your-Spawner"));
                e.setCancelled(true);
                return;
            }
            e.setExpToDrop(0);
            if(e.getPlayer().getItemInHand().getEnchantments().containsKey(Enchantment.SILK_TOUCH)) {
                //todo support more than one amount if merged spawner
                CreatureSpawner creatureSpawner = (CreatureSpawner) e.getBlock().getState();
                e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), new ItemBuilder(new ItemStack(e.getBlock().getType()))
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
        if(e.getBlockPlaced() == null || e.getBlockPlaced().getType() != Material.MOB_SPAWNER) {
            return;
        }
        if(!e.getItemInHand().hasItemMeta() || !e.getItemInHand().getItemMeta().hasDisplayName()) {
            return;
        }
        //todo lore check for valid and real spawner!
        String mob = ChatColor.stripColor(e.getPlayer().getItemInHand().getItemMeta().getDisplayName());
        mob = mob.replace(" Spawner", "");
        //todo not all spawners are plugin spawners!
        CreatureSpawner creatureSpawner = (CreatureSpawner) e.getBlockPlaced().getState();
        creatureSpawner.setCreatureTypeByName(mob);
        Spawner spawner = new Spawner(e.getPlayer().getUniqueId(), e.getBlockPlaced().getLocation(), creatureSpawner.getSpawnedType());
        e.getBlockPlaced().getWorld().strikeLightningEffect(e.getBlockPlaced().getLocation());
        plugin.getSpawnersStorage().getSpawnedSpawners().add(spawner);
        e.getPlayer().sendMessage(plugin.getLanguageManager().color("Messages.Spawner-Placed")
                .replace("%mob%", spawner.getSpawnerData().getEntityType().getName()));
    }

}
