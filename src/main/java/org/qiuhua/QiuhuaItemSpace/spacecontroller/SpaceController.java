package org.qiuhua.QiuhuaItemSpace.spacecontroller;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.qiuhua.QiuhuaItemSpace.Config;
import org.qiuhua.QiuhuaItemSpace.Main;
import org.qiuhua.QiuhuaItemSpace.playerdata.PlayerData;
import org.qiuhua.QiuhuaItemSpace.playerdata.PlayerDataController;
import org.qiuhua.QiuhuaItemSpace.spacegui.SpaceGui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.regex.Pattern;

public class SpaceController {


    //尝试往全部空间添加物品 成功会返回空间id 失败会返回null
    public static String tryAllSpaceAddItem(Player player, ItemStack itemStack){
        //获取空间id列表
        Set<String> spaceList = Config.getSpaceList();
        if (spaceList == null) return null;
        for(String spaceId : spaceList){
            //获取是否打开了这个空间的自动拾取
            boolean autoPickUp = Config.getSpaceConfigurationSection(spaceId).getBoolean("AutoPickUp.Enable");
            //获取需要的权限
            String permissions = Config.getSpaceConfigurationSection(spaceId).getString("AutoPickUp.Permissions");
            //如果没有开自动拾取 或者玩家没权限 就跳过本次
            if(!autoPickUp || !player.hasPermission(permissions)){
                continue;
            }
            String itemName = itemStack.getItemMeta().getDisplayName();
            if(itemName == null || itemName.equals("")){
                itemName = itemStack.getType().name();
            }
            Integer amount = itemStack.getAmount();
            //如果成功放入 那就返回本次空间的id
            if(addItem(player, spaceId, itemStack)){
                String meg = Config.getString("Lang.pickUpItem").replaceAll("<itemId>", itemName).replaceAll("<Amount>", String.valueOf(amount)).replaceAll("<spaceId>", spaceId);
                player.sendMessage(meg);
                return spaceId;
            }
        }
        return null;
    }





    //往指定物品空间添加物品
    public static boolean addItem(Player player, String spaceId, ItemStack item){
        //检查这个物品是否可以放入这个物品空间
        if(!loreCondition(spaceId, item)) return false;
        Integer quantity = item.getAmount();
        //获取玩家数据
        PlayerData data = PlayerDataController.getPlayerData(player);
        //获取对应的物品合集map
        ConcurrentHashMap<ItemStack, Integer> itemSpace = data.getItemSpace(spaceId);
        if(itemSpace == null){
            return false;
        }
        int size = Config.getSpaceConfigurationSection(spaceId).getInt("Size");
        //如果已经有这个物品 就更新数量
        item.setAmount(1);
        if(itemSpace.containsKey(item)){
            quantity = itemSpace.get(item) + quantity;
            itemSpace.put(item, quantity);
            return true;
        }
        if(size != -1 && itemSpace.size() >= size){
            player.sendMessage(Config.getString("Lang.spaceIsFull").replaceAll("<spaceId>", spaceId));
            return false;
        }
        itemSpace.put(item, quantity);
        return true;
    }


    //用下标删除
    public static boolean takeItem(Player player, String spaceId, Integer slot, Integer delQuantity){
        if(delQuantity == null) return false;
        //加载物品空间物品
        ConcurrentHashMap<ItemStack, Integer> itemSpace = PlayerDataController.getPlayerData(player).getItemSpace(spaceId);
        //获取当前页数
        Integer pages = PlayerDataController.getPlayerData(player).getPages();
        int index = slot + ((pages-1) * 45) + 1;
        int count = 0;
        for (ItemStack item : itemSpace.keySet()){
            count++;
            if(index == count){
                //数量
                Integer quantity = itemSpace.get(item);
                ItemStack itemStack = item.clone();
                //如果数量不够扣的 就全部删除
                if(delQuantity >= quantity){
                    itemStack.setAmount(quantity);
                    item.clone().setAmount(quantity);
                    HashMap<Integer,ItemStack> items = player.getInventory().addItem(itemStack);
                    itemSpace.remove(item);
                    for(Integer integer : items.keySet()){
                        item = items.get(integer);
                        SpaceController.addItem(player, spaceId, item);
                    }
                }else{
                    itemStack.setAmount(delQuantity);
                    HashMap<Integer,ItemStack> items = player.getInventory().addItem(itemStack);
                    itemSpace.put(item, (quantity - delQuantity));
                    for(Integer integer : items.keySet()){
                        item = items.get(integer);
                        SpaceController.addItem(player, spaceId, item);
                    }
                }
                return true;
            }
        }
        return false;
    }



    //判断物品是否满足这个物品空间放入的要求
    public static boolean loreCondition(String spaceId, ItemStack item){
        //获取条件lore
        List<String> conditionList = Config.getSpaceConfigurationSection(spaceId).getStringList("Condition");
        //如果条件列表为空 则可以放入
        if(conditionList.isEmpty()){
            return true;
        }
        ItemMeta itemMeta = item.getItemMeta();
        //如果物品没有数据 那就不给在往下走了 因为设置了条件 然后物品又没有lore
        if(itemMeta == null){
            return false;
        }
        //获取物品lore
        List<String> itemLoreList = item.getItemMeta().getLore();
        if(itemLoreList == null){
            return false;
        }
        itemLoreList.forEach((originalString) -> {
            // 去除每行的颜色代码
            String modifiedString = originalString.replaceAll("§[0-9a-fA-F]", "");
            // 将修改后的内容存回列表
            int index = itemLoreList.indexOf(originalString);
            itemLoreList.set(index, modifiedString);
        });
        //条件列循环
        for (String condition : conditionList) {
            if (itemLoreList.contains(condition)) {
                return true;
            }
        }
        return false;
    }


}
