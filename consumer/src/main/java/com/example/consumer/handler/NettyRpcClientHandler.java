package com.example.consumer.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;
@Component
public class NettyRpcClientHandler extends SimpleChannelInboundHandler<String> implements Callable {
    ChannelHandlerContext context;
    private String reqMsg;
    private String respMsg;

    public String getReqMsg() {
        return reqMsg;
    }

    public void setReqMsg(String reqMsg) {
        this.reqMsg = reqMsg;
    }

    //读取 来自服务端的消息
    @Override
    protected synchronized void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {
        respMsg = msg;
        notify();
    }

    //通道就绪后要做的事情
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //用context来发送信息
        context = ctx;
    }

    @Override
    public synchronized Object call() throws Exception {
          context.writeAndFlush(reqMsg);
           wait();
          return respMsg;
    }
}
