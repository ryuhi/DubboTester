package com.ryuhi.util.dubbo.test.core.service.impl;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.stereotype.Service;

import com.ryuhi.util.dubbo.test.core.Node;
import com.ryuhi.util.dubbo.test.core.service.BaseServiceImpl;
import com.ryuhi.util.dubbo.test.core.service.RegisterCenterService;
import com.ryuhi.util.dubbo.test.util.PropertiesReader;
import com.ryuhi.util.dubbo.test.util.TelnetUtil;
import com.ryuhi.util.dubbo.test.util.UrlParserUtil;

/**
 * @author ken
 */
@Service("zookeeperService")
public class ZookeeperRegisterCenterServiceImpl extends BaseServiceImpl implements RegisterCenterService {

	private CountDownLatch connectedSemaphore = new CountDownLatch( 1 ); 
    private ThreadLocal<ZooKeeper> connection;

    @Override
    public List<String> listServices(String url, String path) throws IOException, KeeperException, InterruptedException {
    	System.out.println("begin to connect zookeeper");
        ZooKeeper connection = getConnection(url);
        System.out.println("get connection of zookeeper");
        List<String> result = connection.getChildren(DEFAULT_ROOT, true);
        System.out.println("get result for listchildren");
        List<String> returnList = Collections.synchronizedList(new ArrayList<>(result.size()));
        if (StringUtils.isNotEmpty(path)) {
            for (String str : result) {
                if (StringUtils.isNotEmpty(str) && str.contains(path)) {
                    returnList.add(str);
                }
            }
        } else {
            returnList.addAll(result);
        }
        return returnList;
    }

    @Override
    public List<String> listProvider(String url, String path) throws IOException {
        ZooKeeper connection = getConnection(url);
        path = processPath(path);
        List<String> list = null;
        boolean isConnected = true;
        do {
            try {
                list = getProviderDatas(connection, path);
            } catch (KeeperException | InterruptedException e) {
                isConnected = false;
                log.error(e.getMessage(),e);
            }
        } while (!isConnected);
        List<String> result = Collections.synchronizedList(new ArrayList<>(list.size()));
        for (String str : list) {
            UrlParserUtil util = new UrlParserUtil(str,true);
            Node node = util.getNode();
            result.add(node.getHost()+":" +node.getPort());
        }
        return result;
    }

    @Override
    public List<String> listMethods(String url, String path) throws IOException {
        ZooKeeper connection = getConnection(url);
        path = processPath(path);
        List<String> list = null;
        boolean isConnected = true;
        do {
            try {
                list = getProviderDatas(connection, path);
            } catch (KeeperException | InterruptedException e) {
                isConnected = false;
                log.error(e.getMessage(),e);
            }
        }  while (!isConnected);
        List<String> result = Collections.synchronizedList(new ArrayList<>(list.size()));
        for (String str : list) {
            List<String> tmpMethods = new UrlParserUtil(str,true).getMethods();
            result.addAll(tmpMethods);
        }
        return result;
    }

    @Override
    public String invoke(String url, int port, String className, String methodName, String...para) throws SocketException, IOException {
    	Properties prop = PropertiesReader.getInstance().getLists("/ip.properties");
        String externalHost = prop.getProperty(url);
        if (StringUtils.isNotEmpty(externalHost)) {
            url = externalHost;
        }
    	TelnetUtil util = new TelnetUtil(url, port);		
		util.connect(2000);
    	StringBuffer sb = new StringBuffer(512);
    	sb.append("invoke ");
    	sb.append(className);
    	sb.append(".");
    	sb.append(methodName);
    	sb.append("(");
        for(Object obj : para) {
        	sb.append(obj).append(", ");
        }
        sb.append(")");
        util.sendCommand(sb.toString(), 2000);        
        return util.getResponse(2000);
    }

    private String processPath(String path) {
        if (null == path) {
            path = DEFAULT_ROOT;
        }
        if (!path.contains(DEFAULT_ROOT)) {
            path = DEFAULT_ROOT + "/" + path;
        }
        return path;
    }

    private List<String> getProviderDatas(ZooKeeper connection, String path) throws KeeperException, InterruptedException {
        path = path + "/providers";
        List<String> list = connection.getChildren(path, true);
        return list;
    }

    private ZooKeeper getConnection(String url) throws IOException {
        Node node = new UrlParserUtil(url, false).getNode();
        url = node.getHost() + ":" + node.getPort();
        ZooKeeper connection = null;
		try {
			if (null != this.connection && null != this.connection.get()) {
			    connection = this.connection.get();
			} else {
			    connection = new ZooKeeper(url,30000, new Watcher() {
					@Override
					public void process(WatchedEvent event) {
						System.err.println(event.toString());
				    	if ( KeeperState.SyncConnected == event.getState() ) {
				    		connectedSemaphore.countDown();
				    	}						
					}			    	
			    });
			    connectedSemaphore.await();
			    this.connection = new ThreadLocal<>();
			    this.connection.set(connection);
			}
		} catch (Exception e) {
		}
        return connection;
    }
}

