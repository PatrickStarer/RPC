package com.example.provider;

import com.example.provider.server.NettyServer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

@SpringBootApplication
public class ProviderApplication implements CommandLineRunner {
    @Resource
    NettyServer nettyServer;
    public static void main(String[] args) {
        SpringApplication.run(ProviderApplication.class, args);
    }


    //在所有Bean生成之后，进行其他步骤的初始化，这里在bean生成后，开启 netty server 线程
    @Override
    public void run(String... args) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                nettyServer.start("127.0.0.1",8888);
            }
        }).start();
    }
}
