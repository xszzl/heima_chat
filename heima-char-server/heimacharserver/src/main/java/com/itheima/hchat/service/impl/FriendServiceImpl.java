package com.itheima.hchat.service.impl;

import com.itheima.hchat.mapper.TbFriendMapper;
import com.itheima.hchat.mapper.TbFriendReqMapper;
import com.itheima.hchat.mapper.TbUserMapper;
import com.itheima.hchat.pojo.*;
import com.itheima.hchat.pojo.vo.FriendReq;
import com.itheima.hchat.pojo.vo.User;
import com.itheima.hchat.service.FriendService;
import com.itheima.hchat.util.IdWorker;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class FriendServiceImpl implements FriendService {

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private TbUserMapper userMapper;

    @Autowired
    private TbFriendMapper tbFriendMapper;

    @Autowired
    private TbFriendReqMapper tbFriendReqMapper;

    @Override
    public void sendRequest(String fromUserid, String toUserid) {
        // 检查是否允许添加好友
        TbUser friend = userMapper.selectByPrimaryKey(toUserid);
        checkAllowToAddFriend(fromUserid,friend);

        // 添加好友请求
        TbFriendReq friendReq = new TbFriendReq();
        friendReq.setId(idWorker.nextId());
        friendReq.setFromUserid(fromUserid);
        friendReq.setToUserid(toUserid);
        friendReq.setCreatetime(new Date());
        friendReq.setStatus(0);
        tbFriendReqMapper.insert(friendReq);
    }

    @Override
    public List<FriendReq> findFriendReqByUserid(String userid) {
        // 根据用户id查询对应的好友请求
        TbFriendReqExample example = new TbFriendReqExample();
        TbFriendReqExample.Criteria criteria = example.createCriteria();

        criteria.andToUseridEqualTo(userid);
        criteria.andStatusEqualTo(0);

        List<TbFriendReq> friendReqList = tbFriendReqMapper.selectByExample(example);
        List<FriendReq> friendUserList = new ArrayList<>();
        // 根据好友请求，将发起好友请求的用户信息返回
        for (TbFriendReq friendReq: friendReqList) {
            TbUser tbUser = userMapper.selectByPrimaryKey(friendReq.getFromUserid());
            FriendReq user = new FriendReq();
            BeanUtils.copyProperties(tbUser,user);
            user.setId(friendReq.getId());
            friendUserList.add(user);
        }
        return friendUserList;
    }

    @Override
    public void acceptFriendReq(String reqid) {
        // 将Status设置为1
        TbFriendReq friendReq = tbFriendReqMapper.selectByPrimaryKey(reqid);
        friendReq.setStatus(1);
        tbFriendReqMapper.updateByPrimaryKey(friendReq);

        // 将互相添加好友
        TbFriend friend1 = new TbFriend();
        friend1.setId(idWorker.nextId());
        friend1.setUserid(friendReq.getFromUserid());
        friend1.setFriendsId(friendReq.getToUserid());
        friend1.setCreatetime(new Date());

        TbFriend friend2 = new TbFriend();
        friend2.setId(idWorker.nextId());
        friend2.setFriendsId(friendReq.getFromUserid());
        friend2.setUserid(friendReq.getToUserid());
        friend2.setCreatetime(new Date());

        tbFriendMapper.insert(friend1);
        tbFriendMapper.insert(friend2);
    }

    @Override
    public void ignoreFriendReq(String reqid) {
        TbFriendReq friendReq = tbFriendReqMapper.selectByPrimaryKey(reqid);
        friendReq.setStatus(1);
        tbFriendReqMapper.updateByPrimaryKey(friendReq);
    }

    @Override
    public List<User> findFriendByUserid(String userid) {
        TbFriendExample frindExample = new TbFriendExample();
        TbFriendExample.Criteria criteria = frindExample.createCriteria();
        criteria.andUseridEqualTo(userid);
        List<TbFriend> tbFriendList = tbFriendMapper.selectByExample(frindExample);

        List<User> friendList = new ArrayList<>();

        for (TbFriend friend : tbFriendList) {
            TbUser user = userMapper.selectByPrimaryKey(friend.getFriendsId());
            User myFriend = new User();
            BeanUtils.copyProperties(user,myFriend);
            friendList.add(myFriend);
        }
        return friendList;
    }

    /**
     * 检查是否允许添加好友
     * @param userid
     * @param friend
     */
    private void checkAllowToAddFriend(String userid, TbUser friend){
        // 用户不能添加自己
        if (friend.getId().equals(userid)){
            throw new RuntimeException("不能添加自己为好友");
        }
        // 用户不能重复添加
        TbFriendExample friendExample = new TbFriendExample();
        TbFriendExample.Criteria friendCriteria = friendExample.createCriteria();
        friendCriteria.andUseridEqualTo(userid);
        friendCriteria.andFriendsIdEqualTo(friend.getId());
        List<TbFriend> friendList = tbFriendMapper.selectByExample(friendExample);
        if (friendList != null && friendList.size() > 0){
            throw new RuntimeException(friend.getUsername() + "已经是您的好友了！");
        }

        // 判断是否已经提交过该好友的申请
        TbFriendReqExample friendReqExample = new TbFriendReqExample();
        TbFriendReqExample.Criteria friendReqCriteria = friendReqExample.createCriteria();
        friendReqCriteria.andFromUseridEqualTo(userid);
        friendReqCriteria.andToUseridEqualTo(friend.getId());
        // 请求未被处理
        friendReqCriteria.andStatusEqualTo(0);
        List<TbFriendReq> friendReqList = tbFriendReqMapper.selectByExample(friendReqExample);
        if (friendReqList != null && friendReqList.size() > 0){
            throw new RuntimeException("您已经对该好友发送过请求");
        }
    }
}
