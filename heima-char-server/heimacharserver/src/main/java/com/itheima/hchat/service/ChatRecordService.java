package com.itheima.hchat.service;

import com.itheima.hchat.pojo.TbChatRecord;

public interface ChatRecordService {
    /**
     * 将聊天记录保存到数据库中
     * @param chatRecord
     */
    void insert(TbChatRecord chatRecord);
}
