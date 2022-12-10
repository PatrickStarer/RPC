package com.example.provider.server;

import com.example.provider.handler.NettyServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * netty 服务端
 */
@Component
public class NettyServer implements DisposableBean {

    @Resource
    NettyServerHandler handler;

    EventLoopGroup boss =  null;
    EventLoopGroup worker = null;

    public void start(String ip,Integer port)  {
         boss = new NioEventLoopGroup(1);
         worker = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap()
                .group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                          ch.pipeline().addLast(new StringEncoder());
                          ch.pipeline().addLast(new StringDecoder());

                          ch.pipeline().addLast(handler);
                    }
                });
        try {
            ChannelFuture channelFuture = serverBootstrap.bind(ip, port).sync();
            System.out.println("服务启动成共");
            //监听通道的关闭状态
            channelFuture.channel().closeFuture().sync();
        }catch (InterruptedException e){
            if(boss!=null)
                boss.shutdownGracefully();
            if(worker!=null)
                worker.shutdownGracefully();
        }
    }

    @Override
    public void destroy() throws Exception {

        if(boss!=null)
            boss.shutdownGracefully();
        if(worker!=null)
            worker.shutdownGracefully();
    }
}
