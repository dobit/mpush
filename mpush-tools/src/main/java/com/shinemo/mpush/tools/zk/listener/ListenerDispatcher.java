package com.shinemo.mpush.tools.zk.listener;

import com.google.common.collect.Maps;
import com.shinemo.mpush.tools.zk.ServerApp;
import com.shinemo.mpush.tools.zk.manage.ServerManage;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;

public class ListenerDispatcher implements CallBack {

    private static final Logger log = LoggerFactory.getLogger(ListenerDispatcher.class);

    private Map<String, CallBack> holder = Maps.newTreeMap();

    public ListenerDispatcher(ServerApp app) {
        //所有connection server
        //holder.put(ZKPath.CONNECTION_SERVER.getPathByIp(app.getIp()), new ConnPathListener());
        //所有redis
        //holder.put(ZKPath.REDIS_SERVER.getPathByIp(app.getIp()), new RedisPathListener());
        //踢人的目录已经交给队列处理了，这里不需要重复处理
        //holder.put(ZKPath.GATEWAY_SERVER.getPathByIp(app.getIp()), new GatewayPathListener());
    }

    @Override
    public void handler(CuratorFramework client, TreeCacheEvent event, String path) {

        Iterator<Map.Entry<String, CallBack>> it = holder.entrySet().iterator();
        boolean hasHandler = false;
        while (it.hasNext()) {
            Map.Entry<String, CallBack> entry = it.next();
            if (path.startsWith(entry.getKey())) {
                hasHandler = true;
                entry.getValue().handler(client, event, path);
            }
        }

        if (!hasHandler) {
            log.warn("ListenerDispatcher other path:" + path + "," + event.getType().name());
        }

    }

    @Override
    public void initData(ServerManage manage) {

        Iterator<Map.Entry<String, CallBack>> it = holder.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, CallBack> entry = it.next();
            entry.getValue().initData(manage);
        }

    }
}
