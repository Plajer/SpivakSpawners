package pl.plajer.spivakspawners.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.server.v1_8_R3.GenericAttributes;
import net.minecraft.server.v1_8_R3.NBTTagCompound;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import pl.plajer.spivakspawners.Main;
import pl.plajer.spivakspawners.registry.heads.Head;
import pl.plajer.spivakspawners.registry.spawner.buyable.BuyableSpawner;
import pl.plajerlair.core.utils.ItemBuilder;

/**
 * @author Plajer
 * <p>
 * Created at 13.02.2019
 */
public class Utils {

  private static Main plugin = JavaPlugin.getPlugin(Main.class);

  public static List<Block> getNearbyBlocks(Location location, int radius) {
    List<Block> blocks = new ArrayList<>();
    for (int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
      for (int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
        for (int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
          blocks.add(location.getWorld().getBlockAt(x, y, z));
        }
      }
    }
    return blocks;
  }

  /**
   * Disables AI for target entity
   *
   * @param bukkitEntity entity to disable ai
   */
  public static void setNoAI(LivingEntity bukkitEntity) {
    ((CraftLivingEntity) bukkitEntity).getHandle().getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0);
    ((CraftLivingEntity) bukkitEntity).getHandle().getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(0);
    try {
      ((CraftLivingEntity) bukkitEntity).getHandle().getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(0);
    } catch (Exception ignored) {
    }
    bukkitEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 255, false, false));
    if (bukkitEntity.getType() == EntityType.GUARDIAN) {
      setRealNoAI(bukkitEntity);
    }
  }

  private static void setRealNoAI(LivingEntity bukkitEntity) {
    net.minecraft.server.v1_8_R3.Entity nmsEntity = ((CraftEntity) bukkitEntity).getHandle();
    NBTTagCompound tag = nmsEntity.getNBTTag();
    if (tag == null) {
      tag = new NBTTagCompound();
    }
    nmsEntity.c(tag);
    tag.setInt("NoAI", 1);
    nmsEntity.f(tag);
  }

  public static ItemStack getSkull(String url) {
    ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
    if (url.isEmpty()) {
      return head;
    }

    SkullMeta headMeta = (SkullMeta) head.getItemMeta();
    GameProfile profile = new GameProfile(UUID.randomUUID(), null);
    profile.getProperties().put("textures", new Property("textures", url));
    Field profileField;
    try {
      profileField = headMeta.getClass().getDeclaredField("profile");
      profileField.setAccessible(true);
      profileField.set(headMeta, profile);

    } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException ignored) {
    }

    head.setItemMeta(headMeta);
    return head;
  }

  public static void giveSpawner(Player player, BuyableSpawner spawner, int amount) {
    String type = EntityDisplayNameFixer.fixDisplayName(spawner.getType());
    player.getInventory().addItem(new ItemBuilder(new ItemStack(Material.MOB_SPAWNER, amount))
        .name(plugin.getLanguageManager().color("Drop-Item.Name")
            .replace("%mob%", type))
        .lore(plugin.getLanguageManager().color("Drop-Item.Lore")
            .replace("%mob%", type)
            .split(";"))
        .lore(plugin.getLanguageManager().color("Drop-Item.Level-Lore")
            .replace("%level%", "1")
            .split(";"))
        .build());
    player.sendMessage(plugin.getLanguageManager().color("Commands.Received-Spawner")
        .replace("%amount%", String.valueOf(1))
        .replace("%mob%", type));
  }

  public static void giveHead(Player player, Head head, int amount) {
    ItemStack stack = head.getItemStack().clone();
    stack.setAmount(amount);
    player.getInventory().addItem(stack);
    String type = EntityDisplayNameFixer.fixDisplayName(head.getEntityType());
    player.sendMessage(plugin.getLanguageManager().color("Commands.Received-Head")
        .replace("%amount%", String.valueOf(1))
        .replace("%mob%", type));
  }

}
