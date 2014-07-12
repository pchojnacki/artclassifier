package artclassifier;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.HashMap;

public class RabbitHole {
	public static final String INLET_QUEUE_NAME = "ArticleType.yuri.inlet";
	public static final String INLET_ROUTING_KEY = "ArticleType.yuri.inlet";

	public static final String INLET_EXCHANGE_NAME = "test_ex";
	public static final String FAILURES_EXCHANGE_NAME = "test_ex";
	public static final String FAILURES_ROUTING_KEY = "ArticleType.yuri.failures";


	public static final String USERNAME = "***";
	public static final String PASSWORD = "***";
	public static final String VIRTUAL_HOST = "events";
	public static final String HOSTNAME = "***";

	public static void main(String[] args) throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setUsername(USERNAME);
		factory.setPassword(PASSWORD);
		factory.setVirtualHost(VIRTUAL_HOST);
		factory.setHost(HOSTNAME);
		Connection conn = factory.newConnection();
		Channel channel = conn.createChannel();

		HashMap<String, Object> queueArgs = new HashMap<String, Object>();
		queueArgs.put("x-dead-letter-exchange", FAILURES_EXCHANGE_NAME);
		queueArgs.put("x-dead-letter-routing-key", FAILURES_ROUTING_KEY);

		AMQP.Queue.DeclareOk declareOk = channel.queueDeclare(INLET_QUEUE_NAME, true, false, false, queueArgs);

		AMQP.Queue.BindOk bindOk = channel.queueBind(declareOk.getQueue(), INLET_EXCHANGE_NAME, INLET_ROUTING_KEY);

		System.err.println(bindOk.toString());

		channel.close();
		conn.close();
	}
}