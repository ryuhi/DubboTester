package com.ryuhi.util.dubbo.test.controller;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;

import org.apache.zookeeper.KeeperException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ryuhi.util.dubbo.test.core.RestResult;
import com.ryuhi.util.dubbo.test.core.service.impl.ZookeeperRegisterCenterServiceImpl;

@RestController
@RequestMapping("/dubbo")
public class ApiController extends BaseController {

    @Resource
    private ZookeeperRegisterCenterServiceImpl zookeeperService;

    /**
     *  获取某zookeeper服务器下dubbo服务列表
     * @param url zookeeper服务器的地址
     * @param path 具体服务的名称，可以是全名，也可以是关键字
     * @return 输出结果
     * @throws InterruptedException 
     * @throws KeeperException 
     */
    @RequestMapping(value = "/listServices", method = RequestMethod.POST)
    public RestResult listServices(@RequestParam("url") String url, @RequestParam(value="path", required=false) String path) throws KeeperException, InterruptedException {
        try {
            List<String> result = zookeeperService.listServices(url, path);
            return RestResult.successResult(result);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return RestResult.failureResult(e.getMessage(),e);
        }
    }

    /**
     *  获取服务的提供者列表
     * @param url zookeeper服务器的地址
     * @param path 具体服务的全包名称
     * @return 输出结果
     */
    @RequestMapping(value = "/listProviders", method = RequestMethod.POST)
    public RestResult listProviders(@RequestParam("url") String url, @RequestParam(value="path", required=false) String path) {
        try {
            List<String> result = zookeeperService.listProvider(url, path);
            return RestResult.successResult(result);
        } catch ( IOException e) {
            log.error(e.getMessage(), e);
            return RestResult.failureResult(e.getMessage(),e);
        }
    }

    /**
     *  获取服务的提供者列表
     * @param url zookeeper服务器的地址
     * @param path 具体服务的全包名称
     * @return 输出结果
     */
    @RequestMapping(value = "/listMethods", method = RequestMethod.POST)
    public RestResult listMethods(@RequestParam("url") String url, @RequestParam(value="path", required=false) String path) {
        try {
            List<String> result = zookeeperService.listMethods(url, path);
            return RestResult.successResult(result);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return RestResult.failureResult(e.getMessage(),e);
        }
    }
    
    /**
     * 利用dubbo的telnet机制执行需要测试的方法，输入数据，获取结果
     * @param provider dubbo服务的提供方法的IP地址和端口号
     * @param className 需要执行的类，全包名
     * @param method 需要执行的方法
     * @param parameter 方法所需的参数列表，可能为空
     * @return 字符串
     */
    @RequestMapping(value = "/executeMethod", method = RequestMethod.POST)
    public RestResult executeMethod(@RequestParam("provider") String provider, @RequestParam("className")String className, @RequestParam("method") String method,  @RequestParam(value="para", required=false)String parameter) {
    	String[] node = provider.split(":");
    	String host = node[0];
    	int port = Integer.parseInt(node[1]);
    	try {
			String result = zookeeperService.invoke(host, port, className, method, parameter.split("\\n"));
			return RestResult.successResult(result);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
            return RestResult.failureResult(e.getMessage(),e);
		}
    }
}
