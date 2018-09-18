package com.weimob.client;

import com.weimob.common.bean.RpcRequest;
import com.weimob.common.bean.RpcResponse;
import com.weimob.common.encode.RpcDecode;
import com.weimob.common.encode.RpcEncode;
import com.weimob.registry.ServiceDiscovery;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * <p>
 *
 * </p>
 *
 * @author yang.ye
 * @since 2018/9/18 10:31
 */
@Component
public class RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);

    @Autowired
    private ServiceDiscovery serviceDiscovery;

    private ConcurrentMap<String, RpcResponse> responseMap = new ConcurrentHashMap();

    public <T> T create(final Class<?> interfaceClass) {

        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, new
                InvocationHandler() {

                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        // 创建 RPC 请求对象
                        RpcRequest rpcRequest = new RpcRequest();
                        rpcRequest.setRequestId(UUID.randomUUID().toString());
                        rpcRequest.setInterfaceName(method.getDeclaringClass().getName());
                        rpcRequest.setMethodName(method.getName());
                        rpcRequest.setParameters(args);
                        rpcRequest.setParameterTypes(method.getParameterTypes());

                        // 获取服务地址
                        String serviceName = interfaceClass.getName();
                        String serviceAddress = serviceDiscovery.discover(serviceName);
                        logger.debug("discover service: {} => {}", serviceName, serviceAddress);
                        if (StringUtils.isEmpty(serviceAddress)) {
                            throw new RuntimeException("serviceAddress is empty!");
                        }
                        String[] array = StringUtils.split(serviceAddress, ":");
                        String host = array[0];
                        int port = Integer.valueOf(array[1]);


                        // 调用 RPC 服务
                        RpcResponse rpcResponse = send(rpcRequest, host, port);
                        if (rpcResponse == null) {
                            logger.error("send request failure", new IllegalStateException("response is null"));
                            return null;
                        }
                        if (rpcResponse.hasException()) {
                            logger.error("response has exception", rpcResponse.getException());
                            return null;
                        }
                        return rpcResponse.getResult();
                    }

                    private RpcResponse send(RpcRequest rpcRequest, String host, int port) {
                        NioEventLoopGroup workerEvent = new NioEventLoopGroup(1);

                        try {
                            Bootstrap b = new Bootstrap();
                            b.group(workerEvent).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel channel) throws Exception {
                                    ChannelPipeline pipeline = channel.pipeline();
                                    pipeline.addLast(new RpcEncode(RpcRequest.class));
                                    pipeline.addLast(new RpcDecode(RpcResponse.class));
                                    pipeline.addLast(new RpcClientHandler(responseMap));
                                }
                            });

                            ChannelFuture future = b.connect(host, port).sync();
                            // 写入 RPC 请求对象
                            future.channel().writeAndFlush(rpcRequest).sync();
                            // 优雅停机
                            future.channel().closeFuture().sync();
                            return responseMap.get(rpcRequest.getRequestId());
                        } catch (InterruptedException e) {
                            logger.error("client exception", e);
                            return null;
                        } finally {
                            workerEvent.shutdownGracefully();
                            responseMap.remove(rpcRequest.getRequestId());
                        }

                    }
                });
    }

}
