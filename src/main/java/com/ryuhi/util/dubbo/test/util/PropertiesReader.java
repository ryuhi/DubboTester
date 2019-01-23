package com.ryuhi.util.dubbo.test.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public final class PropertiesReader extends Properties {
    private static final long serialVersionUID = -401804861245600270L;

    private static PropertiesReader instance = new PropertiesReader();

    public static PropertiesReader getInstance() {
        return instance;
    }

    private PropertiesReader() {}

    /**
     * 获取配置文件某键的值
     * @param file 配置文件的文件名，从classpath开始写，以/开头
     * @param key 键的名称
     * @return 某键的值
     */
    public String getValues(String file, String key) throws IOException {
        load(file);
        return instance.getProperty(key);
    }

    /**
     * 获得某配置文件的所有配置项键值对
     * @param file 配置文件的文件名，从classpath开始写，以/开头
     * @return PropertiesReader
     */
    public Properties getLists(String file) throws IOException {
        load(file);
        return instance;
    }

    /**
     * 加载配置文件的配置项
     * @param file 配置文件的文件名，从classpath开始写，以/开头
     * @throws Exception
     */
    private void load(String file) throws IOException {
        try (InputStream is = getClass().getResourceAsStream(file);
             InputStreamReader isr = new InputStreamReader(is,"UTF-8")) {
            instance.load(isr);
        }
    }
}
