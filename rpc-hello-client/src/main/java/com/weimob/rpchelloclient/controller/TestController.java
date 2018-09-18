package com.weimob.rpchelloclient.controller;

import com.weimob.client.RpcClient;
import com.weimob.hello.api.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *
 * </p>
 *
 * @author yang.ye
 * @since 2018/9/18 15:39
 */
@RestController
public class TestController {

    @Autowired
    private RpcClient rpcClient;

    @RequestMapping("test")
    public Object run() {
        HelloService helloService = rpcClient.create(HelloService.class);
        String huangDaoZhu = helloService.say("huangDaoZhu");
        System.out.println("================>>" + huangDaoZhu);
        return huangDaoZhu;
    }


}
