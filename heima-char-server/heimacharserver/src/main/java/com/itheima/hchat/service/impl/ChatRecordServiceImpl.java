package com.itheima.hchat.service.impl;

import com.itheima.hchat.mapper.TbChatRecordMapper;
import com.itheima.hchat.pojo.TbChatRecord;
import com.itheima.hchat.pojo.TbChatRecordExample;
import com.itheima.hchat.service.ChatRecordService;
import com.itheima.hchat.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ChatRecordServiceImpl implements ChatRecordService {

    @Autowired
    private TbChatRecordMapper chatRecordMapper;

    @Autowired
    private IdWorker idWorker;

    @Override
    public void insert(TbChatRecord chatRecord) {
        chatRecord.setId(idWorker.nextId());
        chatRecord.setHasRead(0);
        chatRecord.setCreatetime(new Date());
        chatRecord.setHasDelete(0);
        chatRecordMapper.insert(chatRecord);
    }

    @Override
    public void updateStatusHasRead(String id) {
        TbChatRecord chatRecord = chatRecordMapper.selectByPrimaryKey(id);
        chatRecord.setHasRead(1);

        chatRecordMapper.updateByPrimaryKey(chatRecord);
    }

    @Override
    public List<TbChatRecord> findUnreadByUserid(String userid) {
        TbChatRecordExample example = new TbChatRecordExample();
        TbChatRecordExample.Criteria criteria = example.createCriteria();

        criteria.andFriendidEqualTo(userid);
        criteria.andHasReadEqualTo(0);
        criteria.andHasDeleteEqualTo(0);

        return chatRecordMapper.selectByExample(example);
    }

    @Override
    public List<TbChatRecord> findByUserIdAndFriendId(String userid, String friendid) {
        // 需要将userid -> friendid的聊天记录查询出来
        // 将friendid -> userid的聊天记录查询出来
        TbChatRecordExample example = new TbChatRecordExample();
        TbChatRecordExample.Criteria criteria1 = example.createCriteria();
        TbChatRecordExample.Criteria criteria2 = example.createCriteria();

        criteria1.andUseridEqualTo(userid);
        criteria1.andFriendidEqualTo(friendid);
        criteria1.andHasDeleteEqualTo(0);

        criteria2.andUseridEqualTo(friendid);
        criteria2.andFriendidEqualTo(userid);
        criteria2.andHasDeleteEqualTo(0);

        example.or(criteria1);
        example.or(criteria2);

        // 将所有发给userid的聊天记录的状态设置为已读
        TbChatRecordExample exampleQuerySendToMe = new TbChatRecordExample();
        TbChatRecordExample.Criteria criteria = exampleQuerySendToMe.createCriteria();
        criteria.andFriendidEqualTo(userid);
        criteria.andHasReadEqualTo(0);
        List<TbChatRecord> chatRecordList = chatRecordMapper.selectByExample(exampleQuerySendToMe);
        for (TbChatRecord tbChatRecord : chatRecordList){
            tbChatRecord.setHasRead(1);
            chatRecordMapper.updateByPrimaryKey(tbChatRecord);
        }
        return chatRecordMapper.selectByExample(example);

    }
}
