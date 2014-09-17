package com.wasteofplastic.districts;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

public class ConversationNaming implements Prompt {

    private Districts plugin;

    /**
     * @param plugin
     */
    public ConversationNaming(Districts plugin) {
	this.plugin = plugin;
    }

    @Override
    public String getPromptText(ConversationContext context) {
	return ChatColor.AQUA + "Enter the name of this district, 'none' or 'default' to use the default";
    }

    @Override
    public boolean blocksForInput(ConversationContext context) {
	return true;
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
	DistrictRegion d = (DistrictRegion) context.getSessionData("District");
	if (d != null) {
	    UUID o = d.getOwner();
	    UUID r = d.getRenter();
	    // Find out if these guys are online
	    Player owner = plugin.getServer().getPlayer(o);
	    Player renter = plugin.getServer().getPlayer(r);
	    if (input.equalsIgnoreCase("default")) {
		// No renter, just owner
		if (r == null) {
		    context.getForWhom().sendRawMessage(ChatColor.GOLD + "Setting to the default owner's message");
		    if (owner != null) {
			d.setEnterMessage("Entering " + owner.getDisplayName() + "'s district!");
			d.setFarewellMessage("Now leaving " + owner.getDisplayName() + "'s district.");
		    } else {
			d.setEnterMessage("Entering " + plugin.players.getName(o) + "'s district!");
			d.setFarewellMessage("Now leaving " + plugin.players.getName(o)  + "'s district.");			
		    }
		} else {
		    context.getForWhom().sendRawMessage(ChatColor.GOLD + "Setting to the default renter's message");
		    if (renter != null) {
			d.setEnterMessage("Entering " + renter.getDisplayName() + "'s rented district!");
			d.setFarewellMessage("Now leaving " + renter.getDisplayName() + "'s rented district.");
		    } else {
			d.setEnterMessage("Entering " + plugin.players.getName(r) + "'s rented district!");
			d.setFarewellMessage("Now leaving " + plugin.players.getName(r)  + "'s rented district.");			
		    }   
		}
	    } else if (input.equalsIgnoreCase("none")) {
		context.getForWhom().sendRawMessage(ChatColor.GOLD + "No messsage will be shown when entering or leaving.");
		d.setEnterMessage("");
		d.setFarewellMessage("");
	    } else {
		context.getForWhom().sendRawMessage(ChatColor.GOLD + "Setting to: " + input);
		d.setEnterMessage(ChatColor.GOLD + "Entering \"" + ChatColor.WHITE + input + ChatColor.GOLD +"\"");
		d.setFarewellMessage(ChatColor.GOLD + "Leaving \""+ ChatColor.WHITE + input + ChatColor.GOLD + "\"");
	    }
	}
	return END_OF_CONVERSATION;
    }

}
