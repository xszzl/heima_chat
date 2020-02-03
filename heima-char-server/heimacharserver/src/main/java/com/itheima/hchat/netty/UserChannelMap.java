package com.itheima.hchat.netty;

import io.netty.channel.Channel;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 建立用户ID与通道的关联
 */
public class UserChannelMap {
    // 用来保存用户id与channel的映射
    private static Map<String, Channel> userChannelMap;

    static {
        userChannelMap = new HashMap<String, Channel>();
    }

    public static void put(String userid, Channel channel){
        userChannelMap.put(userid,channel);
    }

    public static void remove(String userid){
        userChannelMap.remove(userid);
    }

    // 根据通道Id移除映射
    public static void removeByChannelId(String channelId){
        if (!StringUtils.isNotBlank(channelId)){
            return;
        }
        for (Map.Entry<String, Channel> map : userChannelMap.entrySet()){
            if (map.getValue().id().asLongText().equals(channelId)){
                System.out.println("客户端连接断开，取消用户" + map.getKey() +"与通道"+channelId+"的关联");
                userChannelMap.remove(map.getKey());
                break;
            }
        }
    }

    public static void print(){
        for (String s : userChannelMap.keySet()){
            System.out.println("用户id:" + s + " 通道：" + userChannelMap.get(s).id());
        }
    }

    /**
     * 根据好友id获得对应的通道
     * @param friendid
     * @return
     */
    public static Channel get(String friendid) {
        return userChannelMap.get(friendid);
    }
}
