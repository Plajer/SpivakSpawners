package pl.plajer.spivakspawners.registry.heads;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import pl.plajer.spivakspawners.Main;
import pl.plajerlair.core.utils.ItemBuilder;

/**
 * @author Plajer
 * <p>
 * Created at 06.02.2019
 * <p>
 * Class that surrounds EntityType
 */
public class Head {

  private Main plugin = JavaPlugin.getPlugin(Main.class);
  private EntityType entityType;
  private ItemStack itemStack;

  public Head(EntityType entityType) {
    this.entityType = entityType;
    ItemStack stack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
    SkullMeta meta = (SkullMeta) stack.getItemMeta();
    meta.setOwner("MHF_" + entityType.getName().replace("_", ""));
    stack.setItemMeta(meta);
    this.itemStack = new ItemBuilder(stack).name(plugin.getLanguageManager().color("Drop-Head.Name")
        .replace("%mob%", entityType.getName()))
        .lore(plugin.getLanguageManager().color("Drop-Head.Lore").split(";")).build();
  }

  public EntityType getEntityType() {
    return entityType;
  }

  public ItemStack getItemStack() {
    return itemStack;
  }

}
