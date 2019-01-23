package com.ryuhi.util.dubbo.test;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class Run {

    public static void main(String[] args) throws IOException {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:/config/spring/application-context.xml");
        applicationContext.start();
        System.out.println("服务启动成功!");
        System.in.read(); // 为保证服务一直开着，利用输入流的阻塞来模拟
    }
}
