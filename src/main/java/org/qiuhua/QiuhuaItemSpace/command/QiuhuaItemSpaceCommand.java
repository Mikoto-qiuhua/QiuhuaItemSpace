package org.qiuhua.QiuhuaItemSpace.command;

import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.qiuhua.QiuhuaItemSpace.Config;
import org.qiuhua.QiuhuaItemSpace.Main;
import org.qiuhua.QiuhuaItemSpace.playerdata.PlayerDataController;
import org.qiuhua.QiuhuaItemSpace.spacegui.SpaceGui;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class QiuhuaItemSpaceCommand implements CommandExecutor, TabExecutor {

    public void register() {
        Bukkit.getPluginCommand("QiuhuaItemSpace").setExecutor(this);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            switch (args[0]){
                case "reload":
                    if(player.hasPermission("qiuhuaitemspace.reload")){
                        Config.reload();
                        player.sendMessage(Config.getString("Lang.reload"));
                    }else {
                        player.sendMessage(Config.getString("Lang.permission"));
                    }
                    break;
                case "open":
                    if(!PlayerDataController.getPlayerData(player).getIsUseItemSpace()){
                        player.sendMessage(Config.getString("Lang.itemSpaceLock"));
                        return true;
                    }
                    if(args.length == 2 && player.hasPermission("qiuhuaitemspace.open." + args[1])){
                        Bukkit.getScheduler().runTaskAsynchronously(Main.getMainPlugin(), new Runnable() {
                            @Override
                            public void run() {
                                SpaceGui.openSpace(player, args[1]);
                            }
                        });
                    }else {
                        player.sendMessage(Config.getString("Lang.permission"));
                    }
                    break;
            }
            return true;
        }else if (sender instanceof ConsoleCommandSender){
            if(args[0].equals("reload")) {
                Main.getMainPlugin().reloadConfig();
                return true;
            }
        }
        return false;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            List<String> result = new ArrayList<>();
            if(args.length == 1){
                if (player.hasPermission("qiuhuaitemspace.reload")){
                    result.add("reload");
                }
                result.add("open");
                return  result;
            }
            if(args.length == 2){
                Set<String> spaceList = Config.getSpaceList();
                if (spaceList == null) {
                    return null;
                }
                for(String key : spaceList){
                    if (player.hasPermission("qiuhuaitemspace.open." + key)){
                        result.add(key);
                    }
                }
                return result;
            }
        }
        return null;
    }

}
