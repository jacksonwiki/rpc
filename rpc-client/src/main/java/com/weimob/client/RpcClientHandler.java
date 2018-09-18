package com.weimob.client;

import com.weimob.common.bean.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentMap;

/**
 * <p>
 *
 * </p>
 *
 * @author yang.ye
 * @since 2018/9/18 11:11
 */
public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private static final Logger logger = LoggerFactory.getLogger(RpcClientHandler.class);

    /**
     * 存放 请求编号 与 响应对象 之间的映射关系
     */
    private ConcurrentMap<String, RpcResponse> responseMap;

    public RpcClientHandler(ConcurrentMap<String, RpcResponse> responseMap) {
        this.responseMap = responseMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse rpcResponse) throws Exception {
        // 建立 请求编号 与 响应对象 之间的映射关系
        responseMap.put(rpcResponse.getRequestId(), rpcResponse);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("client caught exception", cause);
        ctx.close();
    }
}
