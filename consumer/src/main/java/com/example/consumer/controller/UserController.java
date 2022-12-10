package com.example.consumer.controller;

import com.example.consumer.client.anno.RpcReference;
import com.example.srpc.api.IUserService;
import com.example.srpc.pojo.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @RpcReference
    IUserService userService;

    @RequestMapping("/getUserById")
    public User getUserById(int id){
        return userService.getById(id);

    }



}
