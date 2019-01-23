package com.ryuhi.util.dubbo.test.core.service.impl;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;

import org.apache.zookeeper.KeeperException;

import com.alibaba.fastjson.JSON;

public class ZookeeperRegisterCenterServiceImplTest extends BaseTest{

    @Resource
    private ZookeeperRegisterCenterServiceImpl  zookeeperRegisterCenterService;

    private String url;

    @org.junit.Before
    public void setUp() throws Exception {
        url = "172.16.76.8:2181";
    }

    @org.junit.After
    public void tearDown() throws Exception {
    }

    @org.junit.Test
    public void listServices() throws  IOException, KeeperException, InterruptedException {
        String path = "pospeople";
        List<String> result = zookeeperRegisterCenterService.listServices(url, path);
        String jsonStr = JSON.toJSONString(result);
        System.out.println("jsonStr = " + jsonStr);
    }

    @org.junit.Test
    public void listProvider() throws  IOException{
        List<String> result = zookeeperRegisterCenterService.listProvider(url, "cn.myclass365.bedrock.service.pospeople.PeopleRelationService");
        String jsonStr = JSON.toJSONString(result);
        System.out.println("jsonStr = " + jsonStr);
    }

    @org.junit.Test
    public void listMethods() throws IOException{
        List<String> result = zookeeperRegisterCenterService.listMethods(url, "cn.myclass365.bedrock.service.pospeople.PeopleRelationService");
        String jsonStr = JSON.toJSONString(result);
        System.out.println("jsonStr = " + jsonStr);
    }
}