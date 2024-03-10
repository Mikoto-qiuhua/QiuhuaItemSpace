package org.qiuhua.QiuhuaItemSpace.neigeItems;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.qiuhua.QiuhuaItemSpace.Config;
import org.qiuhua.QiuhuaItemSpace.playerdata.PlayerDataController;
import org.qiuhua.QiuhuaItemSpace.spacecontroller.SpaceController;
import pers.neige.neigeitems.event.ItemPackGiveEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemPackGiveListener implements Listener {


    //物品包给予事件, /ni givePack指令触发.
    //数量决定触发的次数
    @EventHandler
    public void onItemPackGiveEvent (ItemPackGiveEvent event){
        Player player = event.getPlayer();
        //检查空间是否解锁
        if(!PlayerDataController.getPlayerData(player).getIsUseItemSpace()){
            player.sendMessage(Config.getString("Lang.itemSpaceLock"));
            return;
        }
        List<ItemStack> itemStackList = event.getItemStacks();
        for(ItemStack item : itemStackList){
            String spaceId = SpaceController.tryAllSpaceAddItem(player, item);
            //如果这个物品无法放入物品空间
            if(spaceId == null){
                HashMap<Integer, ItemStack> items = player.getInventory().addItem(item);
                //处理添加失败的物品
                if(!items.isEmpty()){
                    for (Map.Entry<Integer, ItemStack> entry : items.entrySet()) {
                        ItemStack itemA = entry.getValue();
                        // 检查item是否为空以及是否为AIR
                        if (item != null && item.getType() != Material.AIR) {
                            // 将添加失败的物品掉落到地面上
                            player.getWorld().dropItemNaturally(player.getLocation(), itemA);
                        }
                    }
                }
            }
        }
        event.setCancelled(true);
    }


}
