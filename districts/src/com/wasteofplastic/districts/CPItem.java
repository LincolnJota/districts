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
    public enum Type { TOGGLE, TEXT };
    private Type type;

    /**
     * @param item
     * @param material
     * @param name
     * @param b
     * @param nextSection
     */
    public CPItem(Material material, String name, boolean flagValue, int slot, Type type) {
	this.flagValue = flagValue;
	this.slot = slot;
	this.name = name;
	this.type = type;
	description.clear();
	item = new ItemStack(material);
	ItemMeta meta = item.getItemMeta();
	switch (type) {
	case TEXT:
	    meta.setDisplayName(name);
	    description.add(ChatColor.GREEN + "Click to enter text");
	    break;
	case TOGGLE:
	    meta.setDisplayName(name.substring(5));
	    if (flagValue) {
		description.add(ChatColor.GREEN + "Allowed");
		description.add(ChatColor.RED + "Click to disallow");
	    } else {
		description.add(ChatColor.RED + "Disallowed");
		description.add(ChatColor.GREEN + "Click to allow");	    
	    }
	    break;
	default:
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
	if (flagValue) {
	    description.add(ChatColor.GREEN + "Allowed");
	    description.add(ChatColor.RED + "Click to disallow");
	} else {
	    description.add(ChatColor.RED + "Disallowed");
	    description.add(ChatColor.GREEN + "Click to allow");	    
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