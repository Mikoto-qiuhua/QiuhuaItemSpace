package org.qiuhua.QiuhuaItemSpace;


import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.annotation.Nullable;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Config {

    private static FileConfiguration config;

    private static final HashMap<String, ConfigurationSection> allSpace = new HashMap<>();  //全部空间的容器





    public static void saveAllConfig(){
        //创建一个插件文件夹路径为基础的 并追加下一层。所以此时的文件应该是Config.yml
        //exists 代表是否存在
        if (!(new File(Main.getMainPlugin().getDataFolder() ,"\\Config.yml").exists()))
            Main.getMainPlugin().saveResource("Config.yml", false);
    }

    public static void reload () {

        config = load(new File(Main.getMainPlugin().getDataFolder(),"\\config.yml"));
        allSpace.clear();
        //获取全部key
        Set<String> spaceList = getSpaceList();
        if(spaceList == null){
            Main.getMainPlugin().getLogger().warning("未配置物品空间");
            return;
        }
        for(String spaceId : spaceList){
            ConfigurationSection a = (ConfigurationSection) config.get("Space." + spaceId);
            if(allSpace.containsKey(spaceId)){
                Main.getMainPlugin().getLogger().warning("出现重复物品空间 => " + spaceId);
                Main.getMainPlugin().getLogger().warning("此空间不会加载 注意检查配置文件");
            }else {
                allSpace.put(spaceId, a);
                Main.getMainPlugin().getLogger().info("加载物品空间 => " + spaceId);
            }
        }
    }

    public static ConfigurationSection getSpaceConfigurationSection(String val){
        return allSpace.get(val);
    }



    public static YamlConfiguration load (File file)
    {
        return YamlConfiguration.loadConfiguration(file);
    }

    public static String getString(String val){
        return config.getString(val);
    }

    public static int getInt(String val){
        return config.getInt(val);
    }

    public static List<String> getList(String val){
        return config.getStringList(val);
    }

    public static Boolean getBoolean(String val){
        return config.getBoolean(val);
    }






    // 获取Space下的节点目录
    public static @Nullable Set<String> getSpaceList(){
        ConfigurationSection spaceSection = config.getConfigurationSection("Space");
        if(spaceSection != null){
            return spaceSection.getKeys(false);
        }
        return null;
    }
}
