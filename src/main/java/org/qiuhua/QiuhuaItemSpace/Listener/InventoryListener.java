package org.qiuhua.QiuhuaItemSpace.Listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.qiuhua.QiuhuaItemSpace.Main;
import org.qiuhua.QiuhuaItemSpace.playerdata.PlayerDataController;
import org.qiuhua.QiuhuaItemSpace.spacecontroller.ItemOperate;
import org.qiuhua.QiuhuaItemSpace.spacecontroller.SpaceController;
import org.qiuhua.QiuhuaItemSpace.spacegui.SpaceGui;
import org.qiuhua.QiuhuaItemSpace.spacegui.Tool;

import java.util.Set;

public class InventoryListener implements Listener {
    //当玩家点击物品栏中的格子时触发事件事件
    @EventHandler
    public void onInventoryClickEvent (InventoryClickEvent event){
        Player player = (Player) event.getWhoClicked();
        Inventory inv = event.getInventory();
        //界面判断
        if(!Tool.isCustomizeInventoryHolder(inv)){
            return;
        }
        //如果点的格子是最后一行 那就不进行下面的操作
        int slot = event.getRawSlot();
        //当前页数
        int pages = PlayerDataController.getPlayerData(player).getPages();
        //当前界面
        String spaceId = PlayerDataController.getPlayerData(player).getSpaceId();
        if (slot >= 45 && slot <= 53) {
            //上一页
            if(slot == 45 && pages != 1){
                PlayerDataController.getPlayerData(player).setPages(pages -1);
                Bukkit.getScheduler().runTaskAsynchronously(Main.getMainPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        SpaceGui.loadGuiItem(player, spaceId, inv);
                    }
                });
            }
            //下一页
            int size = PlayerDataController.getPlayerData(player).getItemSpace(spaceId).size();
            int totalPages = (int) Math.ceil((double) size / 45);
            if(slot == 53 && pages < totalPages){
                PlayerDataController.getPlayerData(player).setPages(pages + 1);
                Bukkit.getScheduler().runTaskAsynchronously(Main.getMainPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        SpaceGui.loadGuiItem(player, spaceId, inv);
                    }
                });
            }
            event.setCancelled(true);
            return;
        }
        if(!Tool.isPlayerInventory(event.getRawSlot())){
            //检测是放下还是拿取  拿取时光标上没有物品
            ItemStack item = event.getCursor(); //光标物品
            // 如果物品不是null并且也不是空气 那就是放下物品
            if(item != null && item.getType() != Material.AIR){
                Bukkit.getScheduler().runTaskAsynchronously(Main.getMainPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        ItemOperate.putDownItem(event);
                    }
                });
            }else{
                //否则就是拿取物品
                Bukkit.getScheduler().runTaskAsynchronously(Main.getMainPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        ItemOperate.TakeItem(event);
                    }
                });
            }
            event.setCancelled(true);
        }else if(event.isShiftClick()) {
            if(event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR){
                ItemStack item = event.getCurrentItem().clone();
                if(SpaceController.addItem(player, spaceId, item)){
                    Bukkit.getScheduler().runTaskAsynchronously(Main.getMainPlugin(), new Runnable() {
                        @Override
                        public void run() {
                            SpaceGui.loadGuiItem(player ,spaceId, event.getInventory());
                        }
                    });
                    return;
                }
                event.setCancelled(true);
            }
        }

    }

    //拖拽物品事件
    @EventHandler
    public void onInventoryDragEvent(InventoryDragEvent event){
        Inventory inv = event.getInventory();
        //界面判断
        if(Tool.isCustomizeInventoryHolder(inv)){
            event.setCancelled(true);
        }
    }
}
