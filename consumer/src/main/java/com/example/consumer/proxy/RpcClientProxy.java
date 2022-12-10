package com.example.consumer.proxy;

import com.alibaba.fastjson.JSON;
import com.example.consumer.client.NettyRpcClient;
import com.example.srpc.common.RpcRequest;
import com.example.srpc.common.RpcResponse;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


//代理对象
@Component
public class RpcClientProxy {
    @Resource
    NettyRpcClient nettyRpcClient;

    Map<Class,Object> SERVICE_PROXY = new HashMap<>();
                            // 这里接受的参数是 打了 RpcReference注解的 属性的类型
    public Object getProxy(Class serviceClass){
        Object proxy = SERVICE_PROXY.get(serviceClass);

        if(proxy==null){
            Object o = Proxy.newProxyInstance(this.getClass().getClassLoader(),
                    new Class[]{serviceClass}, new InvocationHandler() {
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            //封装请求
                            RpcRequest rpcRequest = new RpcRequest();
                            rpcRequest.setRequestId(UUID.randomUUID().toString());
                            rpcRequest.setClassName(method.getDeclaringClass().getName());
                            rpcRequest.setMethodName(method.getName());
                            rpcRequest.setParameterTypes(method.getParameterTypes());
                            rpcRequest.setParameters(args);
                           try {
                               Object msg = nettyRpcClient.send(JSON.toJSONString(rpcRequest));
                               RpcResponse rpcResponse = JSON.parseObject(msg.toString(), RpcResponse.class);
                              if(rpcResponse.getError()!=null){
                                  throw new RuntimeException(rpcResponse.getError());
                              }
                              if(rpcResponse.getResult()!=null){
                                  return JSON.parseObject(rpcResponse.getResult().toString(),method.getReturnType());
                              }
                           }catch (Exception e){
                               e.printStackTrace();
                               throw e;
                           }
                            return null;
                        }
                    }
            );
            SERVICE_PROXY.put(serviceClass,o);
            return o;
        }else{
            return proxy;
        }


    }
}
