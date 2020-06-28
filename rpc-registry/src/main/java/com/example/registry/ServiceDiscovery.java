package com.example.registry;

import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>
 *
 * </p>
 *
 * @author yang.ye
 * @since 2018/9/18 14:00
 */
@Component
public class ServiceDiscovery {

    private static final Logger logger = LoggerFactory.getLogger(ServiceDiscovery.class);

    @Value("${rpc.registry-address}")
    private String zkAddress;

    public String discover(String serviceName) {
        ZkClient zkClient = new ZkClient(zkAddress, Constant.ZK_SESSION_TIMEOUT, Constant.ZK_CONNECTION_TIMEOUT);
        logger.info("connect to zookeeper");

        try {
            // 获取 service 节点
            String servicePath = Constant.ZK_REGISTRY_PATH + "/" + serviceName;
            if (!zkClient.exists(servicePath)) {
                throw new RuntimeException(String.format("can not find any service node on path: %s", servicePath));
            }
            List<String> children = zkClient.getChildren(servicePath);
            int size = children.size();
            String address;
            if (size == 0){
                logger.debug("no interface Provider node,please check service is it normal!");
                throw new RuntimeException("no interface Provider node,please check service is it normal!");
            }else if (size == 1) {
                address = children.get(0);
                logger.debug("get only address node: {}", address);
            } else {
                address = children.get(ThreadLocalRandom.current().nextInt(size));
                logger.debug("get random address node: {}", address);
            }
            String addressPath = servicePath + "/" + address;
            return zkClient.readData(addressPath);
        } finally {
            if (zkClient != null) {
                zkClient.close();
            }
        }
    }
}
