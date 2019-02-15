package pl.plajer.spivakspawners.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.server.v1_8_R3.NBTTagCompound;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

/**
 * @author Plajer
 * <p>
 * Created at 13.02.2019
 */
public class Utils {

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
  public static void setNoAI(Entity bukkitEntity) {
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

}
