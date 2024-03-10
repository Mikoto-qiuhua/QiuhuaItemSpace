package org.qiuhua.QiuhuaItemSpace;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.qiuhua.QiuhuaItemSpace.Listener.InventoryListener;
import org.qiuhua.QiuhuaItemSpace.Listener.PlayerListener;
import org.qiuhua.QiuhuaItemSpace.command.QiuhuaItemSpaceCommand;
import org.qiuhua.QiuhuaItemSpace.database.SqlControl;
import org.qiuhua.QiuhuaItemSpace.database.SqlDataControl;
import org.qiuhua.QiuhuaItemSpace.database.SqlTool;
import org.qiuhua.QiuhuaItemSpace.neigeItems.RegisterNeigeItemsEvents;

public class Main extends JavaPlugin {
    private static Main mainPlugin;
    public static Main getMainPlugin(){
        return mainPlugin;
    }
    //启动时运行
    @Override
    public void onEnable(){
        //设置主插件
        mainPlugin = this;
        Config.saveAllConfig();
        Config.reload();
        new QiuhuaItemSpaceCommand().register();
        Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        new RegisterNeigeItemsEvents();
        SqlControl.loadSQL();
        SqlControl.createTable();
        SqlTool.autoSave();
    }


    //关闭时运行
    @Override
    public void onDisable(){
        SqlDataControl.saveAllPlayerData();

    }

    //执行重载命令时运行
    @Override
    public void reloadConfig(){
        Config.reload();
        this.getLogger().info(Config.getString("Lang.reload"));
    }


}