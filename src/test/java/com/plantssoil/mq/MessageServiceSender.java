package com.plantssoil.mq;

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
class MessageServiceSender {
	static IMessageServiceFactory factory;
	static ExecutorService threadpool;
	private AtomicInteger sequence = new AtomicInteger(0);

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		String redisHost = System.getenv("REDIS_HOST");
		String redisPassword = System.getenv("REDIS_PASSWORD");
		factory = IMessageServiceFactory.builder().host(redisHost).password(redisPassword).build();
		threadpool = Executors.newFixedThreadPool(100);
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
		threadpool.close();
	}

	@Test
	@Order(1)
	void testSendMessage() throws InterruptedException {
		String channel = "test01";
		IMessagePublisher publisher = factory.createMessagePublisher().channelName(channel);
		// send message
		for (int i = 0; i < 1000; i++) {
			threadpool.submit(() -> {
				publisher.publish("Test message " + sequence.getAndIncrement());
			});
		}
		Thread.sleep(2 * 1000);
	}
}
