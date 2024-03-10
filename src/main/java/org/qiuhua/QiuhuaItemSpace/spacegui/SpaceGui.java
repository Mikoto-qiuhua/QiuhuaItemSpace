package org.qiuhua.QiuhuaItemSpace.spacegui;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.qiuhua.QiuhuaItemSpace.Config;
import org.qiuhua.QiuhuaItemSpace.Main;
import org.qiuhua.QiuhuaItemSpace.playerdata.PlayerDataController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class SpaceGui {

    public static void openSpace(Player player, String spaceId){
        ConfigurationSection section = Config.getSpaceConfigurationSection(spaceId);
        if(section == null){
            return;
        }
        String title = section.getString("Title");
        if(title == null){
            return;
        }
        Inventory inventory = Bukkit.createInventory(new SpaceInventoryHolder(), 54, title);
        //设置当前查看的空间
        PlayerDataController.getPlayerData(player).setSpaceId(spaceId);
        //设置当前页数
        PlayerDataController.getPlayerData(player).setPages(1);
        //加载界面物品
        loadGuiItem(player, spaceId, inventory);
        Bukkit.getScheduler().runTask(Main.getMainPlugin(), new Runnable() {
            @Override
            public void run() {
                player.openInventory(inventory);
            }
        });
    }


    //加载界面物品数据
    public static void loadGuiItem(Player player, String spaceId, Inventory inventory){
        inventory.clear();
        //加载翻页物品
        inventory.setItem(45 ,Tool.spawnItemSpaceConfigurationItem(spaceId, "UpPage"));
        inventory.setItem(53 ,Tool.spawnItemSpaceConfigurationItem(spaceId, "DownPage"));
        //加载物品空间物品
        ConcurrentHashMap<ItemStack, Integer> itemSpace = PlayerDataController.getPlayerData(player).getItemSpace(spaceId);
        if(itemSpace.isEmpty()) return;
        //获取当前页数
        Integer pages = PlayerDataController.getPlayerData(player).getPages();
        // 计算当前页的起始索引和结束索引
        int startIndex = (pages - 1) * 45 + 1; //开始
        int endIndex = pages * 45; //结束
        // 遍历并打印指定范围内的内容
        int count = 0;
        int slot = 0;
        for (ItemStack item : itemSpace.keySet()) {
            count++;
            if (count < startIndex)
                continue;
            if (count > endIndex)
                break;
            ItemStack newItem = item.clone();
            ItemMeta itemMeta = newItem.getItemMeta();
            List<String> addLoreInfo = Config.getList("AddLoreInfo");
            addLoreInfo.forEach((originalString) -> {
                // 每行尝试替换数量
                String modifiedString = originalString.replaceAll("<Amount>", String.valueOf(itemSpace.get(item)));
                // 将修改后的内容存回列表
                int index = addLoreInfo.indexOf(originalString);
                addLoreInfo.set(index, modifiedString);
            });
            if(itemMeta.getLore() == null){
                //没有lore
                itemMeta.setLore(addLoreInfo);
            }else {
                //有lore
                List<String> a = itemMeta.getLore();
                a.addAll(addLoreInfo);
                itemMeta.setLore(a);
            }
            newItem.setItemMeta(itemMeta);
            inventory.setItem(slot,newItem);
            slot++;
        }

    }



}
