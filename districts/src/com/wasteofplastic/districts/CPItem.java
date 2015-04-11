package com.wasteofplastic.districts;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Holds all the control panel item info
 * @author tastybento
 *
 */
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
		description.add(ChatColor.GREEN + Locale.cpallowed);
	    } else {
		description.add(ChatColor.RED + Locale.cpdisallowed);	    
	    }
	    meta.setLore(description);
	    item.setItemMeta(meta);
	    break;
	case BUYBLOCKS:
	    meta.setDisplayName(name);
	    description.add(ChatColor.GOLD + Locale.conversationsenterblocknum.replace("[price]", VaultHelper.econ.format(Settings.blockPrice)));
	    description.add(ChatColor.GREEN + Locale.cpclicktobuy);
	    break;
	case SELL:
	case RENT:
	    meta.setDisplayName(name);
	    description.add(ChatColor.GREEN + Locale.cpclicktoenteramount);
	    break;
	case CLAIM:
	    meta.setDisplayName(name);
	    description.add(ChatColor.GREEN + Locale.cpclicktoenter);
	    break;
	case CANCEL:
	    meta.setDisplayName(name);
	    if (desc != null)
		description = desc;
	    description.add(ChatColor.GREEN + Locale.cpclicktocancel);
	    break;	    
	case TEXT:
	    meta.setDisplayName(name);
	    description.add(ChatColor.GREEN + Locale.cpclicktoenter);
	    break;
	case VISUALIZE:
	    meta.setDisplayName(name);
	    if (flagValue) {
		description.add(ChatColor.GREEN + Locale.cpvisible);
		description.add(ChatColor.RED + Locale.cpclicktotoggle);
	    } else {
		description.add(ChatColor.RED + Locale.cpinvisible);
		description.add(ChatColor.GREEN + Locale.cpclicktotoggle);	    
	    }
	    break;

	case TOGGLE:
	    meta.setDisplayName(name.substring(5));
	    if (flagValue) {
		description.add(ChatColor.GREEN + Locale.cpallowed);
		description.add(ChatColor.RED + Locale.cpclicktotoggle);
	    } else {
		description.add(ChatColor.RED + Locale.cpdisallowed);
		description.add(ChatColor.GREEN + Locale.cpclicktotoggle);	    
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
		description.add(ChatColor.GREEN + Locale.cpallowed);
		description.add(ChatColor.RED + Locale.cpclicktotoggle);
	    } else {
		description.add(ChatColor.RED + Locale.cpdisallowed);
		description.add(ChatColor.GREEN + Locale.cpclicktotoggle);	    
	    }

	    break;
	case VISUALIZE:
	    if (flagValue) {
		description.add(ChatColor.GREEN + Locale.cpvisible);
		description.add(ChatColor.RED + Locale.cpclicktotoggle);
	    } else {
		description.add(ChatColor.RED + Locale.cpinvisible);
		description.add(ChatColor.GREEN + Locale.cpclicktotoggle);	    
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