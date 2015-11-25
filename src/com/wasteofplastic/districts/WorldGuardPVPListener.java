package com.wasteofplastic.districts;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.events.DisallowedPVPEvent;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

/**
 * Allows WorldGuard to trump District settings
 * @author tastybento
 *
 */
public class WorldGuardPVPListener implements Listener {
    private Districts plugin;

    /**
     * @param plugin
     */
    public WorldGuardPVPListener(Districts plugin) {
	this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void allowPVPEvent(DisallowedPVPEvent event) {
	Utils.logger(3, event.getEventName());
	Player defender = event.getDefender();
	// We only care about the defender
	// Find out where the defender is and if they are in a district that allows PVP then allow it
	DistrictRegion d = plugin.players.getInDistrict(defender.getUniqueId());
	if (d != null && d.getFlag("allowPVP")) {
	    Utils.logger(3, "Overriding WorldGuard PVP!");
	    // Override WorldGuard settings
	    event.setCancelled(true);
	}
    }

    /**
     * Checks if the positions create an area that overlaps a WG region
     * @param pos1
     * @param pos2
     * @return true if it does, otherwise false
     */
    public boolean checkRegion(Location pos1, Location pos2) {
	// Check if the district overlaps a worldguard region
	RegionContainer container = getWorldGuard().getRegionContainer();
	RegionManager regions = container.get(pos1.getWorld());
	BlockVector min = new BlockVector(pos1.getBlockX(), 0, pos1.getBlockZ());
	BlockVector max = new BlockVector(pos2.getBlockX(), pos2.getWorld().getMaxHeight(), pos2.getBlockZ());
	ProtectedRegion test = new ProtectedCuboidRegion("dummy", min, max);
	ApplicableRegionSet intersecting = regions.getApplicableRegions(test);
	if (!intersecting.getRegions().isEmpty()) {
	    // District will overlap a worldguard region
	    Utils.logger(2, "District overlaps WG region");
	    return true;
	}
	//getLogger().info("DEBUG: Regions are empty");
	return false;
    }

    /**
     * @return the worldGuard
     */
    private WorldGuardPlugin getWorldGuard() {
	Plugin WGplugin = plugin.getServer().getPluginManager().getPlugin("WorldGuard");

	// WorldGuard may not be loaded
	if (WGplugin == null || !(WGplugin instanceof WorldGuardPlugin)) {
	    return null; // Maybe you want throw an exception instead
	}

	return (WorldGuardPlugin) WGplugin;
    }

}
