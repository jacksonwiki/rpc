package com.example.registry;

import org.I0Itec.zkclient.ZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * <p>
 *  服务注册与发现
 * </p>
 *
 * @author yang.ye
 * @since 2018/9/17 17:15
 */
@Component
public class ServiceRegistry {

    private static final Logger logger = LoggerFactory.getLogger(ServiceRegistry.class);
    @Value("${rpc.registry-address}")
    private String zkAddress;

    private ZkClient zkClient;

    @PostConstruct
    private void init(){
        zkClient = new ZkClient(zkAddress, Constant.ZK_SESSION_TIMEOUT, Constant.ZK_CONNECTION_TIMEOUT);
    }

    public void register(String interfaceName, String serviceAddress) {
        // 创建 registry 节点（持久）
        String registryPath = Constant.ZK_REGISTRY_PATH;
        if (!zkClient.exists(registryPath)){
            zkClient.createPersistent(registryPath);
            logger.debug("create registry node: {}", registryPath);
        }
        // 创建 service 节点（持久）
        String servicePath = registryPath + "/" + interfaceName;
        if (!zkClient.exists(servicePath)){
            zkClient.createPersistent(servicePath);
            logger.debug("create registry node: {}", servicePath);
        }
        // 创建 address 节点（临时）
        String addressPath = servicePath + "/address-";
        if (!zkClient.exists(addressPath)){
            zkClient.createEphemeralSequential(addressPath,serviceAddress);
            logger.debug("create address node: {}", serviceAddress);
        }
    }
}
