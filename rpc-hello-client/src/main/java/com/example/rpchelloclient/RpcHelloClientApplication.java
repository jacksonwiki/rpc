package com.example.rpchelloclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.example")
public class RpcHelloClientApplication {


    public static void main(String[] args) {
        SpringApplication.run(RpcHelloClientApplication.class, args);
    }
}
