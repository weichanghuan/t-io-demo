package com;

import java.util.Scanner;

import org.tio.client.AioClient;
import org.tio.client.ClientChannelContext;
import org.tio.client.ClientGroupContext;
import org.tio.client.ReconnConf;
import org.tio.client.intf.ClientAioHandler;
import org.tio.client.intf.ClientAioListener;
import org.tio.core.Aio;
import org.tio.core.Node;

import com.client.HelloClientAioHandler;
import com.constant.Const;
import com.listener.AioCommonListenerImpl;
import com.packet.HelloPacket;

/**
 *
 * @author tanyaowu
 *
 */
public class HelloClientStarter {
	// 服务器节点
	public static Node serverNode = new Node(Const.SERVER, Const.PORT);

	// handler, 包括编码、解码、消息处理
	public static ClientAioHandler aioClientHandler = new HelloClientAioHandler();

	// 事件监听器，可以为null，但建议自己实现该接口，可以参考showcase了解些接口
	public static ClientAioListener aioListener = new AioCommonListenerImpl();

	// 断链后自动连接的，不想自动连接请设为null
	private static ReconnConf reconnConf = new ReconnConf(5000L);

	// 一组连接共用的上下文对象
	public static ClientGroupContext clientGroupContext = new ClientGroupContext(aioClientHandler, aioListener,
			reconnConf);

	public static AioClient aioClient = null;
	public static ClientChannelContext clientChannelContext = null;

	/**
	 * 启动程序入口
	 */
	public static void main(String[] args) throws Exception {
		clientGroupContext.setHeartbeatTimeout(Const.TIMEOUT);
		aioClient = new AioClient(clientGroupContext);
		clientChannelContext = aioClient.connect(serverNode);
		System.out.println("请输入");
		for (;;) {
			Scanner scanner = new Scanner(System.in);
			String next = scanner.next();
			if (null != next && !("".equals(next))) {
				HelloPacket packet = new HelloPacket();
				packet.setBody(next.getBytes(HelloPacket.CHARSET));
				Aio.send(clientChannelContext, packet);// 异步发送
				Thread.sleep(1000);
				System.out.println("发送成功，请输入");
			}
		}
		// 连上后，发条消息玩玩
		// send();
	}

	private static void send() throws Exception {
		HelloPacket packet = new HelloPacket();
		packet.setBody("hello world".getBytes(HelloPacket.CHARSET));
		Aio.send(clientChannelContext, packet);// 异步发送
	}
}
