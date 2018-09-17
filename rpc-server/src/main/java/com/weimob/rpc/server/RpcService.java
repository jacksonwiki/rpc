package com.weimob.rpc.server;

import org.springframework.stereotype.Service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 *  RPC 服务注解（标注在服务实现类上）
 * </p>
 *
 * @author yang.ye
 * @since 2018/9/17 16:04
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Service
public @interface RpcService {
    Class<?> value();
}
