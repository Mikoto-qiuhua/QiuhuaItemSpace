package org.qiuhua.QiuhuaItemSpace.spacecontroller;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.qiuhua.QiuhuaItemSpace.playerdata.PlayerDataController;
import org.qiuhua.QiuhuaItemSpace.spacegui.SpaceGui;

import java.util.Objects;

public class ItemOperate {


    //放入物品
    public static void putDownItem(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        ItemStack item = Objects.requireNonNull(event.getCursor()).clone(); //光标物品
        String spaceId = PlayerDataController.getPlayerData(player).getSpaceId();
        if(spaceId == null){
            return;
        }
        if(SpaceController.addItem(player, spaceId, item)){
            player.setItemOnCursor(null);
            SpaceGui.loadGuiItem(player ,spaceId, event.getInventory());
        }
    }

    //拿物品
    public static void TakeItem(InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        int slot = event.getRawSlot();
        String spaceId = PlayerDataController.getPlayerData(player).getSpaceId();
        Integer quantity = null;
        //如果是左键
        if(event.isLeftClick()){
            //如果是潜行左键
            if(event.isShiftClick()){
                quantity = 32;
            }else {
                quantity = 1;
            }
        }
        //如果是右键
        if(event.isRightClick()){
            //如果是潜行左键
            if(event.isShiftClick()){
                quantity = 64;
            }else {
                quantity = 16;
            }
        }
        SpaceController.takeItem(player, spaceId, slot, quantity);
        SpaceGui.loadGuiItem(player ,spaceId, event.getInventory());
    }


}
