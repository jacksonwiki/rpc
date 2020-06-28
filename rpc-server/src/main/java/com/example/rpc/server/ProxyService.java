//package com.example.rpc.server;
//
//
//import java.lang.reflect.InvocationHandler;
//import java.lang.reflect.Method;
//import java.lang.reflect.Proxy;
//
///**
// * <p>
// *
// * </p>
// *
// * @author yang.ye
// * @since 2019-03-19 17:52
// */
//public class ProxyService {
//
//
//    public Object proxy(Class interfaceClass) {
//
//        TestService testService = new TestService();
//
//        return Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, new InvocationHandler() {
//            @Override
//            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//
//                System.out.println("before=>");
//                Object invoke = method.invoke(testService, args);
//                System.out.println("after=>");
//
//
//                return invoke;
//            }
//        });
//    }
//
//    public static void main(String[] args) {
//        ProxyService proxyService = new ProxyService();
//        TestSl proxy = (TestSl) proxyService.proxy(TestSl.class);
//        proxy.sayHello();
//    }
//
//}
