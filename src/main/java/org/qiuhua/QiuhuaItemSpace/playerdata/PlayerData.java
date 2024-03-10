package org.qiuhua.QiuhuaItemSpace.playerdata;

import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class PlayerData {

    private static final ConcurrentHashMap<String, ConcurrentHashMap<ItemStack, Integer>> allMaster = new ConcurrentHashMap<>();

    //玩家当前页数
    private Integer pages = 1;

    //玩家当前在看的空间id
    private String SpaceId = null;

    //是否可以使用空间
    //默认不允许使用 进服时数据未加载完成的时候不允许玩家使用  数据库加载完后修改成true
    private Boolean isUseItemSpace = false;



    //检查物品空间是否是空的
    public Boolean isItemSpace(){
        return allMaster.isEmpty();
    }

    //检查有没有指定物品空间
    public Boolean isItemSpace(String key){
        return allMaster.containsKey(key);
    }


    //获取指定的物品空间 没有就创建
    public ConcurrentHashMap<ItemStack, Integer> getItemSpace(String key){
        if(!allMaster.containsKey(key)){
            allMaster.put(key, new ConcurrentHashMap<>());
        }
        return allMaster.get(key);
    }

    //设置指定的物品空间
    public void setItemSpace(String key, ConcurrentHashMap<ItemStack, Integer> data){
        allMaster.put(key, data);
    }




    public Boolean getIsUseItemSpace(){
        return isUseItemSpace;
    }

    public void setIsUseItemSpace(Boolean b){
        this.isUseItemSpace = b;
    }

    public Integer getPages(){
        return this.pages;
    }

    public void setPages(Integer pages){
        this.pages = pages;
    }


    public @Nullable String getSpaceId(){
        return this.SpaceId;
    }
    public void setSpaceId(String spaceId){
        this.SpaceId = spaceId;
    }
}
