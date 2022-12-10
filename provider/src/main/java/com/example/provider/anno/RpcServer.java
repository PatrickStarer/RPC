package com.example.provider.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定义注解，用于暴露被调用的服务实现类
 */
@Target(ElementType.TYPE)//用于类上
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcServer {

}
