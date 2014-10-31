package com.wasteofplastic.districts;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

/**
 * Implements a negotiation over how many blocks to buy or claim
 * @author ben
 *
 */
public class ConversationBlocks implements Prompt {
    public enum Type { BUY, CLAIM };

    private Districts plugin;
    private Type type;

    /**
     * @param plugin
     */
    public ConversationBlocks(Districts plugin, Type type) {
	this.plugin = plugin;
	this.type = type;
    }

    @Override
    public String getPromptText(ConversationContext context) {
	switch (type) {
	case BUY:
	    return ChatColor.AQUA + "Enter the number of blocks to buy (" + VaultHelper.econ.format(Settings.blockPrice) + " each)";
	case CLAIM:
	    return ChatColor.AQUA + "Enter the radius to claim";
	default:
	}
	return ChatColor.AQUA + "Enter the number of blocks";
    }

    @Override
    public boolean blocksForInput(ConversationContext context) {
	return true;
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
	if (input.isEmpty()) {
	    context.getForWhom().sendRawMessage(ChatColor.RED + "Ended.");
	    return END_OF_CONVERSATION;
	}
	DistrictRegion d = (DistrictRegion) context.getSessionData("District");
	if (d != null && type.equals(Type.CLAIM)) {
	    context.getForWhom().sendRawMessage(ChatColor.RED + "Move out of a district to claim an area");
	    return END_OF_CONVERSATION;
	}
	Player player = ((Player)context.getForWhom());
	UUID playerUUID = player.getUniqueId();
	// Get the input
	int blocks = 0;
	try {
	    blocks = Integer.parseInt(input);
	} catch (Exception e) {
	    context.getForWhom().sendRawMessage(ChatColor.RED + "How many?");
	    return this;
	}
	if (blocks == 0) {
	    context.getForWhom().sendRawMessage(ChatColor.RED + "Ended.");
	    return END_OF_CONVERSATION;
	}
	switch (type) {
	case BUY:
	    
	    // Player wants to buy blocks
	    if (Settings.blockPrice > 0D && VaultHelper.checkPerm((Player)context.getForWhom(), "districts.buyblocks")) {
		double cost = Settings.blockPrice * (double)blocks;
		double balance = VaultHelper.econ.getBalance(player);
		if (balance >= cost) {
		    VaultHelper.econ.withdrawPlayer(player, cost);
		    plugin.players.addBlocks(player.getUniqueId(), blocks);
		    context.getForWhom().sendRawMessage(ChatColor.YELLOW + "You bought " + blocks + " blocks for " + VaultHelper.econ.format(cost));
		    return END_OF_CONVERSATION;
		}
		context.getForWhom().sendRawMessage(ChatColor.RED + Locale.errortooexpensive.replace("[price]",VaultHelper.econ.format(cost)));
		context.getForWhom().sendRawMessage(ChatColor.RED + "Blocks cost " + VaultHelper.econ.format(Settings.blockPrice));
		context.getForWhom().sendRawMessage(ChatColor.RED + "You have " + VaultHelper.econ.format(balance));
		return this;
	    } else {
		player.sendMessage(ChatColor.RED + Locale.errornoPermission);
		return END_OF_CONVERSATION;
	    }
	case CLAIM:
	    // Check if they have enough blocks	
	    int blocksRequired = (blocks*2+1)*(blocks*2+1);
	    if (blocksRequired > plugin.players.getBlockBalance(playerUUID)) {
		context.getForWhom().sendRawMessage(ChatColor.RED + "You do not have enough blocks!");
		context.getForWhom().sendRawMessage(ChatColor.RED + "Blocks available: " + plugin.players.getBlockBalance(playerUUID));
		context.getForWhom().sendRawMessage(ChatColor.RED + "Blocks required: " + blocksRequired);
		return this;  
	    }
	    if (blocks < 2) {
		context.getForWhom().sendRawMessage(ChatColor.RED + "The minimum radius is 2 blocks");
		return this;		    
	    }
	    // Find the corners of this district
	    Location pos1 = new Location(player.getWorld(),player.getLocation().getBlockX()-blocks,0,player.getLocation().getBlockZ()-blocks);
	    Location pos2 = new Location(player.getWorld(),player.getLocation().getBlockX()+blocks,0,player.getLocation().getBlockZ()+blocks);
	    if (!plugin.checkDistrictIntersection(pos1, pos2)) {
		plugin.createNewDistrict(pos1, pos2, player);
		plugin.players.removeBlocks(playerUUID, blocksRequired);
		context.getForWhom().sendRawMessage(ChatColor.GOLD + "District created!");
		context.getForWhom().sendRawMessage(ChatColor.GOLD + "You now have " + plugin.players.getBlockBalance(playerUUID) + " blocks left.");
		plugin.players.save(playerUUID);
		return END_OF_CONVERSATION;
	    } else {
		context.getForWhom().sendRawMessage(ChatColor.RED + "That size would overlap another district");		    		    
		return this;
	    }
	default:
	    break;
	}
	return END_OF_CONVERSATION;
    }
}
