package com.ryuhi.util.dubbo.test.core.service;

import com.ryuhi.util.dubbo.test.util.PropertiesReader;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Properties;

public class BaseServiceImpl {

    protected static Logger log = LogManager.getLogger(new Exception().getStackTrace()[1].getClassName());

    protected Properties ipHash;

    public BaseServiceImpl() {
        try {
            ipHash = PropertiesReader.getInstance().getLists("/ip.properties");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
