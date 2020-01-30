package com.itheima.hchat.controller;

import com.itheima.hchat.pojo.TbUser;
import com.itheima.hchat.pojo.vo.Result;
import com.itheima.hchat.pojo.vo.User;
import com.itheima.hchat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/findAll")
    public List<TbUser> findAll(){
        return userService.findAll();
    }

    @RequestMapping("/login")
    public Result login(@RequestBody TbUser user){
        try{
            System.out.println(user.getUsername());
            User _user = userService.login(user.getUsername(),user.getPassword());
            if (_user == null){
                return new Result(false, "请检查用户名或密码是否正确！");
            } else {
                return new Result(true,"登录成功！",_user);
            }
        } catch (Exception e){
            e.printStackTrace();
            return new Result(false, "登录错误");
        }
    }

    @RequestMapping("/register")
    public Result register(@RequestBody TbUser user){
        // 注册成功，不抛出异常，注册失败抛出异常
        try{
            userService.register(user);
            return new Result(true,"注册成功");
        } catch (RuntimeException e){
            e.printStackTrace();
            return new Result(false,e.getMessage());
        }
    }

    @RequestMapping("/upload")
    public Result upload(MultipartFile file, String userid){
        try {
            User user = userService.upload(file,userid);
            if (user != null){
                System.out.println(user);
                return new Result(true,"上传成功",user);
            } else {
                return new Result(false, "上传失败");
            }
        } catch (Exception e){
            e.printStackTrace();
            return new Result(false,"上传错误");
        }
    }
}
