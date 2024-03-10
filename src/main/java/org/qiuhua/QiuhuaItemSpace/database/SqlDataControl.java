package org.qiuhua.QiuhuaItemSpace.database;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.qiuhua.QiuhuaItemSpace.Config;
import org.qiuhua.QiuhuaItemSpace.Main;
import org.qiuhua.QiuhuaItemSpace.playerdata.PlayerData;
import org.qiuhua.QiuhuaItemSpace.playerdata.PlayerDataController;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SqlDataControl {


    //存储指定玩家的全部数据
    public static void savePlayerAllData(Player player) {
        //获取空间id列表
        Set<String> spaceList = Config.getSpaceList();
        //获取uuid
        String uuid = player.getUniqueId().toString();
        if(spaceList == null) return;
        for(String spaceId : spaceList){
            //清空对应数据
            SqlControl.cleanTheData(spaceId, uuid);
            //获取当前空间的物品map
            ConcurrentHashMap<ItemStack, Integer> data = PlayerDataController.getPlayerData(player).getItemSpace(spaceId);
            //变成json字符串
            String dataJson = SqlTool.convertMapToJson(data);
            //存入数据库
            SqlControl.insert(spaceId, uuid, dataJson);
        }
    }


    //存储指定玩家的全部数据
    public static void savePlayerAllData(UUID uuid) {
        //获取空间id列表
        Set<String> spaceList = Config.getSpaceList();
        if(spaceList == null) return;
        for(String spaceId : spaceList){
            //清空对应数据
            SqlControl.cleanTheData(spaceId, uuid.toString());
            //获取当前空间的物品map
            ConcurrentHashMap<ItemStack, Integer> data = PlayerDataController.getPlayerData(uuid).getItemSpace(spaceId);
            //变成json字符串
            String dataJson = SqlTool.convertMapToJson(data);
            //存入数据库
            SqlControl.insert(spaceId, uuid.toString(), dataJson);
        }
    }



    //加载指定玩家的全部数据
    public static void loadPlayerAllData(Player player) {
        //获取空间id列表
        Set<String> spaceList = Config.getSpaceList();
        //获取uuid
        String uuid = player.getUniqueId().toString();
        if(spaceList == null) return;
        for(String spaceId : spaceList){
            //获取当前空间的json数据
            String dataJson = SqlControl.getDataByUUID(spaceId, uuid);
            if(dataJson == null){
                continue;
            }
            //把它变成map
            ConcurrentHashMap<ItemStack, Integer> playerData = SqlTool.convertJsonToMap(dataJson);
            //写入对应的空间
            PlayerDataController.getPlayerData(player).setItemSpace(spaceId, playerData);
        }
    }



    //不管玩家是否在线
    public static void saveAllPlayerData(){
        ConcurrentHashMap<UUID, PlayerData> allMaster = PlayerDataController.getAllPlayerData();
        Main.getMainPlugin().getLogger().info(Config.getString("Lang.dataSave"));
        for(UUID uuid : allMaster.keySet()){
            savePlayerAllData(uuid);
        }
    }




}
