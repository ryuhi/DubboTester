package com.ryuhi.util.dubbo.test.core.service;

import java.io.IOException;
import java.net.SocketException;
import java.util.List;

import org.apache.zookeeper.KeeperException;

public interface RegisterCenterService {

    String DEFAULT_ROOT = "/dubbo";

    List<String> listServices(String url, String path) throws KeeperException, InterruptedException, IOException;

    List<String> listProvider(String url, String path) throws KeeperException, InterruptedException, IOException;

    List<String> listMethods(String url, String path) throws KeeperException, InterruptedException, IOException;

	String invoke(String url, int port, String className, String methodName, String... para)
			throws SocketException, IOException;
}
