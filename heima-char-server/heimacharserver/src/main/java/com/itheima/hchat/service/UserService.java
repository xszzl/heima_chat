package com.itheima.hchat.service;

import com.itheima.hchat.pojo.TbUser;
import com.itheima.hchat.pojo.vo.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    /**
     * 返回数据库中的所有用户
     * @return
     */
    List<TbUser> findAll();

    /**
     * 用来登录检查，检查用户名和密码是否匹配
     * @param username
     * @param password
     * @return 如果登录成功返回用户对象，否则返回none
     */
    User login(String username, String password);

    /**
     * 注册用户，将用户信息保存到数据库中
     * 如果抛出异常则注册失败，否则注册成功
     * @param user
     */
    void register(TbUser user);

    /**
     * 上传头像
     * @param file 客户端上传的文件
     * @param userId 用户id
     * @return 如果上传成功，返回用户信息，否则返回null
     */
    User upload(MultipartFile file, String userId);
}
