package com.example.provider.service;

import com.example.provider.anno.RpcServer;
import com.example.srpc.api.IUserService;
import com.example.srpc.pojo.User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


//被调用的服务
@Service
@RpcServer
public class UserServiceImpl implements IUserService {
    Map<Object,User> map = new HashMap<>();
    @Override
    public User getById(int id) {
        if(map.size()==0){
            User u1 = new User();
            u1.setId(1);
            u1.setName("zhangsan");

            User u2= new User();
            u2.setId(2);
            u2.setName("lisi");
            map.put(u1.getId(),u1);
            map.put(u2.getId(),u2);
        }
         return map.get(id);
    }
}
