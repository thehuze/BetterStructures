package com.magmaguy.betterstructures.config.treasures;

import com.magmaguy.betterstructures.MetadataHandler;
import com.magmaguy.betterstructures.config.CustomConfigFields;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.List;

public class TreasureConfigFields extends CustomConfigFields {

    @Getter
    @Setter
    private List<String> rawLoot = null;

    public TreasureConfigFields(String filename, boolean isEnabled) {
        super(filename, isEnabled);
    }

    @Override
    public void processConfigFields() {
        this.isEnabled = processBoolean("isEnabled", isEnabled, true, true);
        this.rawLoot = processStringList("loot", rawLoot, null, true);
    }

    public void addChestEntry(String entry, Player player) {
        rawLoot.add(entry);
        fileConfiguration.set("loot", rawLoot);
        try {
            fileConfiguration.save(file);
        } catch (Exception ex) {
            player.sendMessage("[BetterStructures] Failed to save entry to file! Report this to the developer.");
            return;
        }
        MetadataHandler.PLUGIN.onDisable();
        MetadataHandler.PLUGIN.onLoad();
        MetadataHandler.PLUGIN.onEnable();
        player.sendMessage("[BetterStructures] Reloaded plugin to add chest entry! It should now be live.");
    }
}