package com.example.provider.handler;

import com.alibaba.fastjson.JSON;
import com.example.provider.anno.RpcServer;
import com.example.srpc.common.RpcRequest;
import com.example.srpc.common.RpcResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.BeansException;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
@ChannelHandler.Sharable //该handler可以被共享
public class NettyServerHandler extends SimpleChannelInboundHandler<String> implements ApplicationContextAware {


    //ApplicationContextAware 实现这个接口 可以得到spring中所有的bean

    static Map<String,Object> SERVICE_INSTANCE_MAP = new HashMap<>();


    //实现ApplicationContextAware，获得注解缓存
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        //通过实现实现 ApplicationContextAware 获得所有bean的集合 并选出 被RpcServer注解的类
        Map<String, Object> serviceMap = applicationContext.getBeansWithAnnotation(RpcServer.class);
       //遍历map
        Set<Map.Entry<String, Object>> entries = serviceMap.entrySet();

        for (Map.Entry<String, Object> entry : entries) {
            Object serviceBean = entry.getValue();
            //如果该bean没有实现接口，throw
            if(serviceBean.getClass().getInterfaces().length==0){
                throw new RuntimeException("该服务没有实现任何接口");
            }
            //如果有多个接口 ，默认处理第一个接口
            String serverName = serviceBean.getClass().getInterfaces()[0].getName();
            //存放接口的名称和接口的实现类
            SERVICE_INSTANCE_MAP.put(serverName,serviceBean);
        }
    }


    //netty server接受到数后的handler
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg)   {
        //接受到json(请求) 后 反序列化为 请求对象
        RpcRequest rpcRequest = JSON.parseObject(msg, RpcRequest.class);
        //创建响应对象
        RpcResponse response = new RpcResponse();

        //拿到请求序列号
        response.setRequestId(rpcRequest.getRequestId());

        //正常返回结果 否则返回异常
        try {
            response.setResult(handler(rpcRequest));
        }catch (Exception e){
            e.printStackTrace();
            response.setError(e.getMessage());
        }
        //响应
        ctx.writeAndFlush(JSON.toJSONString(response));
    }



    private Object handler(RpcRequest rpcRequest) throws InvocationTargetException {

        Object serviceBean = SERVICE_INSTANCE_MAP.get(rpcRequest.getClassName());

        if(serviceBean==null){
             throw new RuntimeException("没有找到服务");
         }
        //通过反射 调用bean的方法
        FastClass proxyClass = FastClass.create(serviceBean.getClass());

        FastMethod method = proxyClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
        return  method.invoke(serviceBean,rpcRequest.getParameters());
    }


}
