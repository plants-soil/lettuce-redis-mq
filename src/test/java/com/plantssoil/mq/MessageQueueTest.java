package com.plantssoil.mq;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MessageQueueTest {
	static IMessageServiceFactory factory;
	static ExecutorService threadpool;
	private AtomicInteger sequence = new AtomicInteger(0);

	public static void main(String[] args) throws Exception {
		MessageQueueTest.setUpBeforeClass();
		MessageQueueTest t = new MessageQueueTest();
		t.testObjectMessage();
		MessageQueueTest.tearDownAfterClass();
	}

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		String redisHost = System.getenv("REDIS_HOST");
		String redisPassword = System.getenv("REDIS_PASSWORD");
		factory = IMessageServiceFactory.builder().host(redisHost).password(redisPassword).build();
		threadpool = Executors.newFixedThreadPool(100);
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		threadpool.shutdown();
	}

	@Test
	@Order(1)
	void testStringMessage() throws InterruptedException {
		String channel = "queue01";
		// receive message
		factory.createMessageConsumer().channelName(channel).addMessageListener(new IMessageListener<String>() {
			@Override
			public void onMessage(String message, String consumerId) {
				System.out.println(message);
			}
		}).consume(String.class);

		// send message
		IMessagePublisher publisher = factory.createMessagePublisher().channelName(channel);
		for (int i = 0; i < 5; i++) {
			threadpool.submit(() -> {
				publisher.publish("Test message " + sequence.getAndIncrement());
			});
		}
		Thread.sleep(2 * 1000);
	}

	@Test
	@Order(2)
	void testObjectMessage() throws InterruptedException {
		String channel = "queue02";

		// receive message
		factory.createMessageConsumer().channelName(channel).addMessageListener(new IMessageListener<TestBean>() {
			@Override
			public void onMessage(TestBean message, String consumerId) {
				System.out.println(message);
			}
		}).consume(TestBean.class);

		// send message
		IMessagePublisher publisher = factory.createMessagePublisher().channelName(channel);
		for (int i = 0; i < 5; i++) {
			threadpool.submit(() -> {
				int s = sequence.getAndIncrement();
				TestProperty p = new TestProperty();
				p.setP1("p1-" + s);
				p.setP2("p2-" + s);
				TestBean b = new TestBean();
				b.setName("name-" + s);
				b.setCode(s);
				b.setD(new Date());
				b.setP(p);
				publisher.publish(b);
			});
		}
		Thread.sleep(2 * 1000);
	}

	@Test
	@Order(3)
	void testTopicMessage() throws Exception {
		String channel = "topic03";

		// receive message by 2 consumers from channel with type as ChannelType.TOPIC
		IMessageConsumer c1 = factory.createMessageConsumer().channelName(channel).channelType(ChannelType.TOPIC);
		IMessageConsumer c2 = factory.createMessageConsumer().channelName(channel).channelType(ChannelType.TOPIC);
		c1.addMessageListener(new IMessageListener<String>() {
			@Override
			public void onMessage(String message, String consumerId) {
				System.out.println("Consumer 1 received message: " + message);
			}
		}).consume(String.class);
		c2.addMessageListener(new IMessageListener<String>() {
			@Override
			public void onMessage(String message, String consumerId) {
				System.out.println("Consumer 2 received message: " + message);
			}
		}).consume(String.class);

		// send message to channel with type as ChannelType.TOPIC
		IMessagePublisher publisher = factory.createMessagePublisher().channelName(channel)
				.channelType(ChannelType.TOPIC);
		for (int i = 0; i < 5; i++) {
			threadpool.submit(() -> {
				publisher.publish("Test message " + sequence.getAndIncrement());
			});
		}
		Thread.sleep(2 * 1000);
	}
}
