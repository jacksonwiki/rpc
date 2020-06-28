package com.example.rpc.server;

/**
 * <p>
 *
 * </p>
 *
 * @author yang.ye
 * @since 2019-03-19 17:52
 */
public class TestService implements TestSl{

    @Override
    public String sayHello() {
        System.out.println("say hello");
        return "say hello";
    }

}
