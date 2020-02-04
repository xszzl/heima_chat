package com.itheima.hchat.service;

import com.itheima.hchat.pojo.TbChatRecord;

import java.util.List;

public interface ChatRecordService {
    /**
     * 将聊天记录保存到数据库中
     * @param chatRecord
     */
    void insert(TbChatRecord chatRecord);

    /**
     * 根据用户id和好友id查询出聊天记录
     * @param userid
     * @param friendid
     * @return
     */
    List<TbChatRecord> findByUserIdAndFriendId(String userid, String friendid);

    /**
     * 根据用户id查询未读消息记录
     * @param userid
     * @return
     */
    List<TbChatRecord> findUnreadByUserid(String userid);

    /**
     * 设置消息为已读
     * @param id
     */
    void updateStatusHasRead(String id);
}
