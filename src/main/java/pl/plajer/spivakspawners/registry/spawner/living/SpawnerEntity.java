package pl.plajer.spivakspawners.registry.spawner.living;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import org.bukkit.entity.Entity;

/**
 * @author Plajer
 * <p>
 * Created at 13.02.2019
 */
public class SpawnerEntity {

    private Hologram hologram;
    private Entity entity;

    public SpawnerEntity(Hologram hologram, Entity entity) {
        this.hologram = hologram;
        this.entity = entity;
    }

    public Hologram getHologram() {
        return hologram;
    }

    public Entity getEntity() {
        return entity;
    }

}
