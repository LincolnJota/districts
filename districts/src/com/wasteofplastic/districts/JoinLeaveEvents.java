package com.wasteofplastic.districts;

import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinLeaveEvents implements Listener {
    private Districts plugin;
    private PlayerCache players;

    public JoinLeaveEvents(Districts districts, PlayerCache onlinePlayers) {
	this.plugin = districts;
	this.players = onlinePlayers;
    }

    /**
     * @param event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(final PlayerJoinEvent event) {
	final Player p = event.getPlayer();
	final UUID playerUUID = p.getUniqueId();
	players.addPlayer(playerUUID);
	// Set the player's name (it may have changed)
	players.setPlayerName(playerUUID, p.getName());
	players.save(playerUUID);
	plugin.getLogger().info("Cached " + p.getName());
	// TODO: Check leases and expire any old ones.
	// Check to see if the player is in a district - one may have cropped up around them while they were logged off
	for (DistrictRegion d: plugin.getDistricts()) {
	    if (d.intersectsDistrict(p.getLocation())) {
		//plugin.getLogger().info(p.getName() + " is in a known district");
		if (players.getInDistrict(playerUUID) == null || !players.getInDistrict(playerUUID).equals(d)) {
		    players.setInDistrict(playerUUID, d);
		    p.sendMessage(d.getEnterMessage());
		}
		final DistrictRegion dr = d;
		if (plugin.players.getVisualize(p.getUniqueId())) {
		    plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {

			@Override
			public void run() {
			    //plugin.getLogger().info("visualizing tick");
			    plugin.visualize(dr, p);

			}},20L);
		}
		// }

		//players.setVisualize(playerUUID, true);
		break;
	    }
	}
	// Load any messages for the player
	final List<String> messages = plugin.getMessages(playerUUID);
	if (!messages.isEmpty()) {
	    plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
		@Override
		public void run() {
		    event.getPlayer().sendMessage(ChatColor.AQUA + Locale.newsHeadline);
		    int i = 1;
		    for (String message : messages) {
			event.getPlayer().sendMessage(i++ + ": " + message);
		    }
		}
	    }, 40L);
	}
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(final PlayerQuitEvent event) {
	// Remove any lingering positions
	plugin.getPos1s().remove(event.getPlayer().getUniqueId());
	//plugin.setMessage(event.getPlayer().getUniqueId(), "Hello! This is a test. You logged out");
	players.removeOnlinePlayer(event.getPlayer().getUniqueId());
    }
}