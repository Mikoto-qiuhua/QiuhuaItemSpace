package org.qiuhua.QiuhuaItemSpace.database;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.qiuhua.QiuhuaItemSpace.Config;
import org.qiuhua.QiuhuaItemSpace.Main;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class SqlTool {
    public static void autoSave() {
        int autoTime = Config.getInt("Database.autoSave");
        Bukkit.getScheduler().runTaskTimerAsynchronously(Main.getMainPlugin(), new Runnable() {
            @Override
            public void run() {
                //存储全部玩家数据
                SqlDataControl.saveAllPlayerData();
            }
        }, (autoTime * 60L) * 20L,   (autoTime * 60L) * 20L);
    }




    //将map序列化成json
    public static String convertMapToJson(ConcurrentHashMap<ItemStack, Integer> playerItemMap) {
        //新建一个map
        HashMap<String, Integer> map = new HashMap<>();
        for(ItemStack item : playerItemMap.keySet()){
            String stringItem = itemStackSave(item);
            map.put(stringItem, playerItemMap.get(item));
        }
        Gson gson = new Gson();
        return gson.toJson(map);
    }

    //将json变回map
    public static ConcurrentHashMap<ItemStack, Integer> convertJsonToMap(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, Integer>>() {}.getType();
        HashMap<String, Integer> map = gson.fromJson(json, type);

        ConcurrentHashMap<ItemStack, Integer> playerItemMap = new ConcurrentHashMap<>();
        for (String stringItem : map.keySet()) {
            ItemStack item = itemStackLoad(stringItem);
            playerItemMap.put(item, map.get(stringItem));
        }
        return playerItemMap;
    }



    //序列化物品
    public static String itemStackSave(ItemStack itemStack) {
        YamlConfiguration yml = new YamlConfiguration();
        yml.set("item", itemStack);
        return yml.saveToString();
    }
    //反序列化
    public static ItemStack itemStackLoad(String str) {
        YamlConfiguration yml = new YamlConfiguration();
        try {
            if(str != null){
                yml.loadFromString(str);
            }
        } catch (InvalidConfigurationException var3) {
            Main.getMainPlugin().getLogger().severe("无法加载物品");
            throw new RuntimeException(var3);
        }
        return yml.getItemStack("item");
    }



}
