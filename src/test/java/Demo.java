import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import com.ryuhi.util.dubbo.test.util.UrlParserUtil;

public class Demo {

    public static void main(String[] args) throws KeeperException, InterruptedException, IOException {
        ZooKeeper connection = new ZooKeeper("118.31.7.87:2181", 3000, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {

            }
        });
        /**
         * consumers
         * configurators
         * routers
         * providers
         */
        List<String> list = connection.getChildren("/dubbo",true);
        String str = StringUtils.join(list, "\n");
        System.out.println("str = " + str);
        System.exit(1);
        /**
         * dubbo://172.16.76.6:20884/cn.myclass365.bedrock.service.organization.DepartmentService?anyhost=true&application=myclass-bedrock&dubbo=2.5.3&interface=cn.myclass365.bedrock.service.organization.DepartmentService&methods=deleteDepartmentList,insertOrUpdateDepartment,listDepartment,listDepartmentInByName&pid=32495&revision=1.0-SNAPSHOT&side=provider&timestamp=1544173135217
         */
        for (String st : list) {
            UrlParserUtil util = new UrlParserUtil(st,true);
            System.out.println("util.getUrl() = " + util.getUrl());
            System.out.println("util.getKeyValues().toString() = " + util.getKeyValues().toString());
            System.out.println("util.getNode().toString() = " + util.getNode().toString());
        }
    }
}
