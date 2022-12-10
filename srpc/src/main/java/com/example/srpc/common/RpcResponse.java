package com.example.srpc.common;

import lombok.Data;

//响应信息字段
@Data
public class RpcResponse {
     //请求序列号
    private  String requestId;
    //响应错误或者结果
    private  String error;
    private  Object result;
}
