package com.example.srpc.common;

import lombok.Data;

//请求消息需要的字段
@Data
public class RpcRequest {
    //请求序列号，由uuid生成
    private String requestId;
    //需要调用的服务类名
    private String className;
    //类下的方法名
    private String methodName;
    //参数类型
    private  Class<?>[] parameterTypes;
    //参数
    private Object[] parameters;
}
