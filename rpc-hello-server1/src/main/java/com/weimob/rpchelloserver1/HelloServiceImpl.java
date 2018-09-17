package com.weimob.rpchelloserver1;

import com.weimob.hello.api.HelloService;
import com.weimob.rpc.server.RpcService;

/**
 * <p>
 *
 * </p>
 *
 * @author yang.ye
 * @since 2018/9/17 15:58
 */
@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService {

    @Override
    public String say(String name) {
        return String.format("hello,%s",name);
    }

}
