package com.example.consumer.processor;

import com.example.consumer.client.anno.RpcReference;

import com.example.consumer.proxy.RpcClientProxy;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import java.lang.reflect.Field;


//实现 @RpcReference 逻辑

//BeanPostProcessor 后置处理器，作用是在Bean对象在实例化和依赖注入完毕后，在显示调用初始化方法的前后添加我们自己的逻辑。
// 注意是Bean实例化完毕后及依赖注入完成后触发的。
//这里实现这个接口 对每个bean的属性都进行扫描，查看是否有RpcReference注解
@Component
public class MyBeanPostProcessor implements BeanPostProcessor {
    @Resource
    RpcClientProxy rpcClientProxy;
    //实现类似自动注入的注解 即RpcReference
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        //通过bean得到属性
        Field[] declaredField = bean.getClass().getDeclaredFields();

        for (Field field : declaredField) {
//            判断属性有没有 RpcReference 注解
            RpcReference annotation = field.getAnnotation(RpcReference.class);
            if(annotation!=null){
                //如果有  得到属性的类型 得到代理类
                Object proxy = rpcClientProxy.getProxy(field.getType());

                try{
                    field.setAccessible(true);
                    //将代理对象 注入 到 controller 层的 接口引用
                    field.set(bean,proxy);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }
}
