package org.qiuhua.QiuhuaItemSpace.spacegui;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.qiuhua.QiuhuaItemSpace.Config;
import org.qiuhua.QiuhuaItemSpace.Main;

import java.util.List;

public class Tool {
    //判断界面
    public static boolean isSpaceInventoryHolder (InventoryHolder holder)
    {
        return holder instanceof SpaceInventoryHolder;
    }
    public static boolean isCustomizeInventoryHolder (Inventory inventory)
    {
        return inventory.getHolder() instanceof SpaceInventoryHolder;
    }



    //获取物品空间的翻页物品
    public static ItemStack spawnItemSpaceConfigurationItem(String spaceId, String itemId){
        //将字符串转换为 Material 枚举类型
        ConfigurationSection section = Config.getSpaceConfigurationSection(spaceId);
        Material itemType = Material.getMaterial(section.getString(itemId + ".Type").toUpperCase());
        String itemName = section.getString(itemId + ".Name");
        List<String> itemLore = section.getStringList(itemId + ".Lore");
        int itemModel = section.getInt(itemId + ".Model");
        //创建一个新物品堆
        ItemStack item = new ItemStack(itemType, 1);
        //获取物品属性
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(itemName);
        itemMeta.setLore(itemLore);
        if(itemModel != -1){
            itemMeta.setCustomModelData(itemModel);
        }
        //物品属性设置回去
        item.setItemMeta(itemMeta);
        return item;
    }

    /**
     * 如果返回true就代表这个是玩家物品栏
     * 不是上半部分
     * @param slot
     * @return
     */
    public static boolean isPlayerInventory(int slot){
        return slot < 0 || slot >= 54;
    }


}
