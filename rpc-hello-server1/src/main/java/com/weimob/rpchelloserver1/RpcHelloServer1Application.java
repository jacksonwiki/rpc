package com.weimob.rpchelloserver1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.weimob")
public class RpcHelloServer1Application {

    public static void main(String[] args) {
        SpringApplication.run(RpcHelloServer1Application.class, args);
    }
}
