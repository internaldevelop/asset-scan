package com.toolkit.assetscan.global.websocket;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.alibaba.fastjson.JSONObject;
import com.toolkit.assetscan.global.utils.MyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


//@RestController
//@CrossOrigin(origins = "*",maxAge = 3600)
//@RequestMapping(value = "/websocket/{sid}")
@ServerEndpoint("/websocket/{sid}")
@Component
@CrossOrigin(origins = "*", maxAge = 3600)
public class WebSocketServer {
    static protected Logger logger = LoggerFactory.getLogger(WebSocketServer.class);

    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;
    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<WebSocketServer>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    //接收sid
    private String sid = "";

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        this.session = session;
        webSocketSet.add(this);     //加入set中
        addOnlineCount();           //在线数加1
        logger.info("有新窗口开始监听:" + sid + ",当前在线人数为" + getOnlineCount());
        this.sid = sid;

        sendMessage(SockMsgTypeEnum.GENERAL_INFO, "连接成功");
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);  //从set中删除
        subOnlineCount();           //在线数减1
        logger.info("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        logger.info("收到来自窗口" + sid + "的信息:" + message);
        //群发消息
//        for (WebSocketServer item : webSocketSet) {
//            item.sendMessage(SockMsgTypeEnum.GENERAL_INFO, message);
//        }
    }

    /**
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        logger.error("发生错误");
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
            onClose();
        }
    }

    public void sendMessage(SockMsgTypeEnum msgType, Object object) {
        SockMsgBean sockMsgBean = new SockMsgBean();
        sockMsgBean.setType(msgType.getType());
        sockMsgBean.setMessage(msgType.getMessage());
        sockMsgBean.setTimeStamp(MyUtils.getCurrentSystemTimestamp());
        sockMsgBean.setPayload(object);

        String message = JSONObject.toJSONString(sockMsgBean);
        sendMessage(message);
    }

    public static void sendInfo(SockMsgTypeEnum msgType, Object object, String sid) {
        for (WebSocketServer item : webSocketSet) {
            if (item.sid.equals(sid)) {
                item.sendMessage(msgType, object);
            }
        }
    }

    /**
     * 群发任务运行状态的自定义消息
     */
    public static void broadcastTaskInfo(SockMsgTypeEnum msgType, Object object) {
        for (WebSocketServer item : webSocketSet) {
                if (item.sid.startsWith("task_run_info") || item.sid.startsWith("accept_all"))
                    item.sendMessage(msgType, object);
        }
    }

    /**
     * 群发资产实时系统信息
     * @param msgType
     * @param object
     */
    public static void broadcastAssetInfo(SockMsgTypeEnum msgType, Object object) {
        for (WebSocketServer item : webSocketSet) {
                if (item.sid.startsWith("asset_info") || item.sid.startsWith("accept_all"))
                    item.sendMessage(msgType, object);
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }
}
