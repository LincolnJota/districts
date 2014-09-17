package com.wasteofplastic.districts;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CPItem {
    private ItemStack item;
    private List<String> description = new ArrayList<String>();
    private String name;
    private int slot;
    private boolean flagValue;
    public enum Type { TOGGLE, TEXT, INFO, REMOVE, BUYBLOCKS, SELL, RENT, CANCEL, TRUST, UNTRUST, VISUALIZE, CLAIM, TOGGLEINFO };
    private Type type;

    /**
     * @param item
     * @param material
     * @param i 
     * @param name
     * @param b
     * @param nextSection
     */
    public CPItem(Material material, int i, String name, boolean flagValue, int slot, List<String> desc, Type type) {
	this.flagValue = flagValue;
	this.slot = slot;
	this.name = name;
	this.type = type;
	description.clear();
	item = new ItemStack(material);
	item.setDurability((short) i);
	ItemMeta meta = item.getItemMeta();
	switch (type) {
	case TOGGLEINFO:
	    meta.setDisplayName(name.substring(5));
	    if (flagValue) {
		description.add(ChatColor.GREEN + "Allowed by anyone");
	    } else {
		description.add(ChatColor.RED + "Disallowed for outsiders");	    
	    }
	    meta.setLore(description);
	    item.setItemMeta(meta);
	    break;
	case BUYBLOCKS:
	    meta.setDisplayName(name);
	    description.add(ChatColor.GOLD + "Blocks cost " + VaultHelper.econ.format(Settings.blockPrice) + " each");
	    description.add(ChatColor.GREEN + "Click to enter how many you want to buy");
	    break;
	case SELL:
	case RENT:
	    meta.setDisplayName(name);
	    description.add(ChatColor.GREEN + "Click to enter amount");
	    break;
	case CLAIM:
	    meta.setDisplayName(name);
	    description.add(ChatColor.GREEN + "Click to enter the box radius");
	    break;
	case CANCEL:
	    meta.setDisplayName(name);
	    if (desc != null)
		description = desc;
	    description.add(ChatColor.GREEN + "Click to cancel");
	    break;	    
	case TEXT:
	    meta.setDisplayName(name);
	    description.add(ChatColor.GREEN + "Click to enter text");
	    break;
	case VISUALIZE:
	    meta.setDisplayName(name);
	    if (flagValue) {
		description.add(ChatColor.GREEN + "Visible");
		description.add(ChatColor.RED + "Click to make invisible");
	    } else {
		description.add(ChatColor.RED + "Invisible");
		description.add(ChatColor.GREEN + "Click to make visible");	    
	    }
	    break;

	case TOGGLE:
	    meta.setDisplayName(name.substring(5));
	    if (flagValue) {
		description.add(ChatColor.GREEN + "Allowed by anyone");
		description.add(ChatColor.RED + "Click to disallow");
	    } else {
		description.add(ChatColor.RED + "Disallowed for outsiders");
		description.add(ChatColor.GREEN + "Click to allow");	    
	    }
	    break;
	default:
	    meta.setDisplayName(name);
	    if (desc != null)
		description = desc;
	    break;
	}
	meta.setLore(description);
	item.setItemMeta(meta);
    }

    public void setLore(List<String> lore) {
	ItemMeta meta = item.getItemMeta();
	meta.setLore(lore);
	item.setItemMeta(meta);
    }

    public ItemStack getItem() {
	return item;
    }

    /**
     * @return the slot
     */
    public int getSlot() {
	return slot;
    }

    /**
     * @return the flagValue
     */
    public boolean isFlagValue() {
	return flagValue;
    }

    /**
     * @param flagValue the flagValue to set
     */
    public void setFlagValue(boolean flagValue) {
	this.flagValue = flagValue;
	description.clear();
	ItemMeta meta = item.getItemMeta();
	switch (type) {
	case TOGGLE:
	    if (flagValue) {
		description.add(ChatColor.GREEN + "Allowed by anyone");
		description.add(ChatColor.RED + "Click to disallow");
	    } else {
		description.add(ChatColor.RED + "Disallowed for outsiders");
		description.add(ChatColor.GREEN + "Click to allow");	    
	    }

	    break;
	case VISUALIZE:
	    if (flagValue) {
		description.add(ChatColor.GREEN + "Visible");
		description.add(ChatColor.RED + "Click to make invisible");
	    } else {
		description.add(ChatColor.RED + "Invisible");
		description.add(ChatColor.GREEN + "Click to make visible");	    
	    }
	    break;
	default:
	    break;

	}
	meta.setLore(description);
	item.setItemMeta(meta);
    }




    public String getName() {
	return name;
    }

    /**
     * @return the type
     */
    public Type getType() {
	return type;
    }

}