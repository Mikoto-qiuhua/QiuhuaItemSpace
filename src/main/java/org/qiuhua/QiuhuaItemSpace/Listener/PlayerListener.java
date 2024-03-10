package org.qiuhua.QiuhuaItemSpace.Listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.qiuhua.QiuhuaItemSpace.Config;
import org.qiuhua.QiuhuaItemSpace.Main;
import org.qiuhua.QiuhuaItemSpace.database.SqlDataControl;
import org.qiuhua.QiuhuaItemSpace.playerdata.PlayerDataController;
import org.qiuhua.QiuhuaItemSpace.spacecontroller.SpaceController;

public class PlayerListener implements Listener {

    //玩家进服事件
    @EventHandler
    public void onPlayerJoinEvent (PlayerJoinEvent event){
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLaterAsynchronously(Main.getMainPlugin(), new Runnable() {
            @Override
            public void run() {
                //加载玩家数据 玩家数据为空的情况下才加载
                if(PlayerDataController.getPlayerData(player).isItemSpace()){
                    //玩家必须在线才加载
                    if(Bukkit.getPlayer(player.getUniqueId()) != null){
                        SqlDataControl.loadPlayerAllData(player);
                        //加载完成后 解锁玩家仓库
                        PlayerDataController.getPlayerData(player).setIsUseItemSpace(true);
                        player.sendMessage(Config.getString("Lang.itemSpaceUnlock"));
                    }
                }
            }
        },40L);
    }


    //玩家退出事件
    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event){
        Player player = event.getPlayer();
        //只有在mysql的情况并且玩家有物品空间的信息  才保存并且删除  单纯进服 数据未读取完成的时候玩家是没有空间数据的 只有读取完成后才有
        //如果玩家进服后立即退出 数据未加载完成那就没有空间数据 也就不进行保存  并且当加载数据的时候  玩家不在线也是不会加载的
        if(Config.getString("Database.type").equalsIgnoreCase("mysql") && !PlayerDataController.getPlayerData(player).isItemSpace()){
            SqlDataControl.savePlayerAllData(player);
            PlayerDataController.removePlayerData(player.getUniqueId());
        }
    }


    //实体捡起物品事件
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerPickupItem(PlayerPickupItemEvent event){

        //如果被取消 那就不执行了
        if(event.isCancelled()){
            return;
        }
        Player player = event.getPlayer();
        //检查空间是否解锁
        if(!PlayerDataController.getPlayerData(player).getIsUseItemSpace()){
            player.sendMessage(Config.getString("Lang.itemSpaceLock"));
            return;
        }
        ItemStack itemStack = event.getItem().getItemStack().clone();
        String spaceId = SpaceController.tryAllSpaceAddItem(player, itemStack);
        if(spaceId == null){
            return;
        }
        event.setCancelled(true);
        event.getItem().remove();

    }

}
