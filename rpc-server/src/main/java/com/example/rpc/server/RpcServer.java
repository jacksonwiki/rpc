package com.example.rpc.server;

import com.example.common.bean.RpcRequest;
import com.example.common.bean.RpcResponse;
import com.example.common.encode.RpcDecode;
import com.example.common.encode.RpcEncode;
import com.example.registry.ServiceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * 服务注册与发现
 * </p>
 *
 * @author yang.ye
 * @since 2018/9/17 16:10
 */
@Component
public class RpcServer implements ApplicationContextAware, InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);

    private Map<String, Object> handlerMap = new ConcurrentHashMap<>();
    @Value("${rpc.port}")
    private int port;

    @Autowired
    private ServiceRegistry serviceRegistry;

    /**
     * server服务
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        NioEventLoopGroup bossEvent = new NioEventLoopGroup();
        NioEventLoopGroup workEvent = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossEvent, workEvent).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {

                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    pipeline.addLast(new RpcDecode(RpcRequest.class));
                    pipeline.addLast(new RpcEncode(RpcResponse.class));
                    pipeline.addLast(new RpcServerHandler(handlerMap));

                }
            }).option(ChannelOption.SO_BACKLOG, 512).childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture future = b.bind(port).sync();
            logger.info("server started, listening on {}", port);

            // 注册 RPC 服务地址
            String serviceAddress = InetAddress.getLocalHost().getHostAddress() + ":" + port;
            for (String interfaceName : handlerMap.keySet()) {
                serviceRegistry.register(interfaceName, serviceAddress);
                logger.info("register service: {} => {}", interfaceName, serviceAddress);
            }

            // 释放资源
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("server exception", e);
        } finally {
            workEvent.shutdownGracefully();
            bossEvent.shutdownGracefully();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        /* 扫描带有 @RpcService 注解的服务类 */
        Map<String, Object> serverBeanMap = ctx.getBeansWithAnnotation(RpcService.class);
        if (MapUtils.isNotEmpty(serverBeanMap)) {
            for (Object serviceBean : serverBeanMap.values()) {
                RpcService annotation = serviceBean.getClass().getAnnotation(RpcService.class);
                String serviceName = annotation.value().getName();
                handlerMap.put(serviceName, serviceBean);
            }
        }

    }
}
