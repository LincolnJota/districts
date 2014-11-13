package com.wasteofplastic.districts;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

/**
 * Obtains trusted or untrusted players
 * @author ben
 *
 */
public class GetPlayers implements Prompt {
    public enum Type { TRUST, UNTRUST };

    private Districts plugin;
    private Type type;

    /**
     * @param plugin
     */
    public GetPlayers(Districts plugin, Type type) {
	this.plugin = plugin;
	this.type = type;
    }

    @Override
    public String getPromptText(ConversationContext context) {
	if (type.equals(Type.TRUST))
	    return ChatColor.AQUA + "Enter player names one by one or END";
	else
	    return ChatColor.AQUA + "Enter player names one by one, ALL or END";
    }

    @Override
    public boolean blocksForInput(ConversationContext context) {
	return true;
    }

    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
	if (input.isEmpty() || input.equalsIgnoreCase("END")) {
	    context.getForWhom().sendRawMessage(ChatColor.RED + "Ended.");
	    return END_OF_CONVERSATION;
	}
	DistrictRegion d = (DistrictRegion) context.getSessionData("District");
	if (d == null) {
	    context.getForWhom().sendRawMessage(ChatColor.RED + "Move to a district you own or rent first.");
	    return END_OF_CONVERSATION;
	}
	UUID playerUUID = ((Player)context.getForWhom()).getUniqueId();
	if (type.equals(Type.UNTRUST)) {
	    if (input.equalsIgnoreCase("ALL")) {
		((Player)context.getForWhom()).performCommand("district untrustall");
		context.getForWhom().sendRawMessage(ChatColor.GREEN + "All trusted players removed!");
		return END_OF_CONVERSATION;
		/*
		if (d.getOwner().equals(playerUUID) || d.getRenter().equals(playerUUID)) {
		    if (d.getOwner().equals(playerUUID)) {
			if (!d.getOwnerTrusted().isEmpty()) {
			    // Tell everyone
			    for (UUID n : d.getOwnerTrustedUUID()) {
				Player p = plugin.getServer().getPlayer(n);
				if (p != null) {
				    p.sendMessage(ChatColor.RED + ((Player)context.getForWhom()).getDisplayName() + " untrusted you in a district.");
				}
			    }
			    // Blank it out
			    d.setOwnerTrusted(new ArrayList<UUID>());
			}

		    } else {
			if (!d.getRenterTrusted().isEmpty()) {
			    for (UUID n : d.getRenterTrustedUUID()) {
				Player p = plugin.getServer().getPlayer(n);
				if (p != null) {
				    p.sendMessage(ChatColor.RED + ((Player)context.getForWhom()).getDisplayName() + " untrusted you in a district.");
				}
			    }
			    // Blank it out
			    d.setRenterTrusted(new ArrayList<UUID>());
			}
		    }
		}
		context.getForWhom().sendRawMessage(ChatColor.GREEN + "All trusted players removed!");
		return END_OF_CONVERSATION;
		 */
	    } else {
		//((Player)context.getForWhom()).performCommand("district untrust " + input);
		//return this;
		// Untrust individual players
		if (d.getOwner().equals(playerUUID) || (d.getRenter() != null && d.getRenter().equals(playerUUID))) {
		    // Check that we know this person
		    UUID trusted = plugin.players.getUUID(input);
		    if (trusted == null) {
			context.getForWhom().sendRawMessage(ChatColor.RED + "Unknown player.");
			return this;			
		    }

		    if (d.getOwner().equals(playerUUID)) {
			if (d.getOwnerTrusted().isEmpty()) {
			    context.getForWhom().sendRawMessage(ChatColor.RED + "No one is trusted in this district.");
			} else {
			    // Remove trusted player
			    d.removeOwnerTrusted(trusted);
			    Player p = plugin.getServer().getPlayer(trusted);
			    if (p != null) {
				p.sendMessage(ChatColor.RED + ((Player)context.getForWhom()).getDisplayName() + " untrusted you in a district.");
			    }


			}
		    } else {
			if (d.getRenterTrusted().isEmpty()) {
			    context.getForWhom().sendRawMessage(ChatColor.RED + "No one is trusted in this district.");
			} else {
			    Player p = plugin.getServer().getPlayer(trusted);
			    if (p != null) {
				p.sendMessage(ChatColor.RED + ((Player)context.getForWhom()).getDisplayName() + " untrusted you in a district.");
			    }
			    // Blank it out
			    d.removeRenterTrusted(trusted);
			}
		    }
		    plugin.players.save(d.getOwner());
		    context.getForWhom().sendRawMessage(ChatColor.GOLD + "[District Trusted Players]");
		    context.getForWhom().sendRawMessage(ChatColor.GREEN + "[Owner's]");
		    if (d.getOwnerTrusted().isEmpty()) {
			context.getForWhom().sendRawMessage("None");
		    } else for (String name : d.getOwnerTrusted()) {
			context.getForWhom().sendRawMessage(name);
		    }
		    context.getForWhom().sendRawMessage(ChatColor.GREEN + "[Renter's]");
		    if (d.getRenterTrusted().isEmpty()) {
			context.getForWhom().sendRawMessage("None");
		    } else for (String name : d.getRenterTrusted()) {
			context.getForWhom().sendRawMessage(name);
		    }	
		    return this;
		} else {
		    context.getForWhom().sendRawMessage(ChatColor.RED + "You must be the owner or renter of this district to do that.");
		    return END_OF_CONVERSATION;
		}
	    }

	} else if (type.equals(Type.TRUST)) {
	    //Wish I could do this...
	    //((Player)context.getForWhom()).performCommand("district trust " + input);
	    //return this;
	    if ((d.getOwner() != null && d.getOwner().equals(playerUUID)) || (d.getRenter() != null && d.getRenter().equals(playerUUID))) {
		// Check that we know this person
		UUID trusted = plugin.players.getUUID(input);
		if (trusted != null) {
		    if (d.getOwner() != null && d.getOwner().equals(trusted)) {
			context.getForWhom().sendRawMessage(ChatColor.RED + "That player is the owner and so trusted already.");
			return this;
		    }
		    if (d.getRenter() != null && d.getRenter().equals(trusted)) {
			context.getForWhom().sendRawMessage(ChatColor.RED + "That player is the renter and so trusted already.");
			return this;
		    }			
		    // This is a known player, name is OK
		    if (d.getOwner().equals(playerUUID)) {
			if (!d.addOwnerTrusted(trusted)) {
			    context.getForWhom().sendRawMessage(ChatColor.RED + "That player is already trusted.");
			    return this;
			}
		    } else {
			if (!d.addRenterTrusted(trusted)) {
			    context.getForWhom().sendRawMessage(ChatColor.RED + "That player is already trusted.");
			    return this;
			} 			    
		    }
		    Player p = plugin.getServer().getPlayer(trusted);
		    if (p != null) {
			p.sendMessage(ChatColor.RED + ((Player)context.getForWhom()).getDisplayName() + " trusts you in a district.");
		    }
		    plugin.players.save(d.getOwner());
		    context.getForWhom().sendRawMessage(ChatColor.GOLD + "[District Info]");
		    context.getForWhom().sendRawMessage(ChatColor.GREEN + "[Owner's trusted players]");
		    if (d.getOwnerTrusted().isEmpty()) {
			context.getForWhom().sendRawMessage("None");
		    } else for (String name : d.getOwnerTrusted()) {
			context.getForWhom().sendRawMessage(name);
		    }
		    context.getForWhom().sendRawMessage(ChatColor.GREEN + "[Renter's trusted players]");
		    if (d.getRenterTrusted().isEmpty()) {
			context.getForWhom().sendRawMessage("None");
		    } else for (String name : d.getRenterTrusted()) {
			context.getForWhom().sendRawMessage(name);
		    }
		    return this;
		} else {
		    context.getForWhom().sendRawMessage(ChatColor.RED + "Unknown player.");
		    return this;
		}
	    } else {
		context.getForWhom().sendRawMessage(ChatColor.RED + "You must be the owner or renter to add them to this district.");
		return this;
	    }
	}
	return END_OF_CONVERSATION;
    }
}
