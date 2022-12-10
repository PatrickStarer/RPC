package com.example.consumer.client;

import com.example.consumer.handler.NettyRpcClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;

import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Component
public class NettyRpcClient  implements InitializingBean , DisposableBean {
    EventLoopGroup group = null;
    Channel channel = null;
   @Resource
   NettyRpcClientHandler nettyRpcClientHandler;

   ExecutorService service = Executors.newCachedThreadPool();
    @Override
    public void afterPropertiesSet() throws Exception {
        try{
            group=  new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                             ch.pipeline().addLast(new StringDecoder());
                             ch.pipeline().addLast(new StringEncoder());
                              ch.pipeline().addLast(nettyRpcClientHandler);
                    }

                });
            channel= bootstrap.connect("127.0.0.1", 8888).sync().channel();
        }catch (Exception e){
            e.printStackTrace();
            if(channel!=null)
                channel.close();
            if(group!=null)
                group.shutdownGracefully();


        }
    }
    @Override
    public void destroy() throws Exception {
        if(channel!=null)
            channel.close();
        if(group!=null)
            group.shutdownGracefully();
    }


    public Object send(String msg) throws ExecutionException, InterruptedException {
        //使用线程池来发送消息
        nettyRpcClientHandler.setReqMsg(msg);
        //提交后 得到call方法的返回值
        Future submit = service.submit(nettyRpcClientHandler);
        return   submit.get();
    }

}
