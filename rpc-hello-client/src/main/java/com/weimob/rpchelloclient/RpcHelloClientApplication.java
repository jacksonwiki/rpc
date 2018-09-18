package com.weimob.rpchelloclient;

import com.weimob.client.RpcClient;
import com.weimob.hello.api.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication(scanBasePackages = "com.weimob")
public class RpcHelloClientApplication {


    public static void main(String[] args) {
        SpringApplication.run(RpcHelloClientApplication.class, args);
    }
}
