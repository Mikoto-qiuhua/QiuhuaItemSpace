package org.qiuhua.QiuhuaItemSpace.neigeItems;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.qiuhua.QiuhuaItemSpace.Config;
import org.qiuhua.QiuhuaItemSpace.playerdata.PlayerDataController;
import org.qiuhua.QiuhuaItemSpace.spacecontroller.SpaceController;
import pers.neige.neigeitems.event.ItemGiveEvent;

public class ItemGiveListener implements Listener {

    //物品给予事件, /ni give指令触发.
    //数量决定触发的次数
    @EventHandler
    public void onItemGiveEvent (ItemGiveEvent event){
        Player player = event.getPlayer();
        ItemStack itemStack = event.getItemStack();
        //检查空间是否解锁
        if(!PlayerDataController.getPlayerData(player).getIsUseItemSpace()){
            player.sendMessage(Config.getString("Lang.itemSpaceLock"));
            return;
        }
        String spaceId = SpaceController.tryAllSpaceAddItem(player, itemStack);
        if(spaceId == null){
            return;
        }
        event.setCancelled(true);
    }



}
