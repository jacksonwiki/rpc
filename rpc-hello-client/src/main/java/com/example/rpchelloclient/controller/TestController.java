package com.example.rpchelloclient.controller;

import com.example.client.RpcClient;
import com.example.hello.api.HelloService;
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

       /* 获取代理类 */
        HelloService helloService = rpcClient.create(HelloService.class);

        /*代理类调用*/
        String huangDaoZhu = helloService.say("huangDaoZhu");

        System.out.println("================>>" + huangDaoZhu);

        return huangDaoZhu;
    }


}
