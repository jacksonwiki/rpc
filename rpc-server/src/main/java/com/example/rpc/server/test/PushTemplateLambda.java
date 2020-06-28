package com.example.rpc.server.test;

import java.util.function.Consumer;

public class PushTemplateLambda {
 
    public void push(int customerId, String shopName, Consumer<Object[]> execute) {
        System.out.println("准备推送...");
        Object[] param = new Object[]{customerId, shopName};
        execute.accept(param);
        System.out.println("推送完成\n");
    }
}