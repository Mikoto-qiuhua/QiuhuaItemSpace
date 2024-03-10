package org.qiuhua.QiuhuaItemSpace.neigeItems;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.qiuhua.QiuhuaItemSpace.Config;
import org.qiuhua.QiuhuaItemSpace.Main;

public class RegisterNeigeItemsEvents {

    public RegisterNeigeItemsEvents(){
        Plugin plugin = Bukkit.getPluginManager().getPlugin("NeigeItems");
        if(plugin != null && plugin.isEnabled()){
            if(Config.getBoolean("NeigeItems")){
                Bukkit.getPluginManager().registerEvents(new ItemGiveListener(), Main.getMainPlugin());
                Bukkit.getPluginManager().registerEvents(new ItemPackGiveListener(), Main.getMainPlugin());
            }
        }
    }


}
