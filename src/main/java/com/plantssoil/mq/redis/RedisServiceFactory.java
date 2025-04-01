package com.plantssoil.mq.redis;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.plantssoil.mq.IMessageConsumer;
import com.plantssoil.mq.IMessagePublisher;
import com.plantssoil.mq.IMessageServiceFactory;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;

/**
 * The IMessageServiceFactory implementation base on Redis.<br/>
 * Referenced Redis Lettuce documentation, redis-lettuce does't need connection
 * pool for non-blocking commands.<br/>
 * There are only 2 connections in this factory, one for publishing and another
 * for subscribing.<br/>
 * 
 * @param <T> The data type to transfer between message server & client
 * @auther duyong
 * @time 12:03:30
 */
public class RedisServiceFactory implements IMessageServiceFactory {
	private RedisClient client;
	private long connectionTimeout;
	private StatefulRedisPubSubConnection<String, String> pubsubPublisherConnection;
	private StatefulRedisPubSubConnection<String, String> pubsubConsumerConnection;
	private StatefulRedisConnection<String, String> publisherConnection;
	private StatefulRedisConnection<String, String> consumerConnection;
	private List<MessageConsumer> consumers;
	private ScheduledExecutorService heartbeat = Executors.newScheduledThreadPool(1);

	/**
	 * Constructor<br/>
	 * Initialize Redis Client and connections for publisher and consumer<br/>
	 */
	public RedisServiceFactory(RedisClientFactory rcf) {
		// get RedisClient
		this.client = rcf.getRedisClient();
		this.connectionTimeout = rcf.getRedisUri().getTimeout().getSeconds() * 1000;

		// create publisher connection
		this.pubsubPublisherConnection = this.client.connectPubSub();
		this.pubsubPublisherConnection.setTimeout(Duration.ofMillis(this.connectionTimeout));

		// create consumer connection
		this.pubsubConsumerConnection = this.client.connectPubSub();
		this.pubsubConsumerConnection.setTimeout(Duration.ofMillis(this.connectionTimeout));

		// create publisher connection
		this.publisherConnection = this.client.connect();
		this.publisherConnection.setTimeout(Duration.ofMillis(this.connectionTimeout));

		// create consumer connection
		this.consumerConnection = this.client.connect();
		this.consumerConnection.setTimeout(Duration.ofMillis(this.connectionTimeout));

		// create consumers collection
		this.consumers = new ArrayList<>();

		// heart beat
		heartbeat();
	}

	@Override
	public void close() throws Exception {
		this.heartbeat.close();
		if (this.consumers != null) {
			for (MessageConsumer consumer : this.consumers) {
				consumer.close();
			}
		}
		if (this.pubsubConsumerConnection != null) {
			this.pubsubConsumerConnection.close();
		}
		if (this.publisherConnection != null) {
			this.publisherConnection.close();
		}
		if (this.consumerConnection != null) {
			this.consumerConnection.close();
		}
		this.client.shutdown();
	}

	@Override
	public IMessagePublisher createMessagePublisher() {
		return new MessagePublisher(this.publisherConnection.async(), this.pubsubPublisherConnection.async());
	}

	@Override
	public IMessageConsumer createMessageConsumer() {
		MessageConsumer consumer = new MessageConsumer(this.pubsubConsumerConnection.reactive(),
				this.consumerConnection);
		this.consumers.add(consumer);
		return consumer;
	}

	private void heartbeat() {
		this.heartbeat.scheduleAtFixedRate(() -> {
			for (MessageConsumer c : this.consumers) {
				String channel = c.getChannelName();
				this.pubsubPublisherConnection.async().publish(MessagePublisher.WEBIZ_QUEUE_NOTIFICATION_CHANNEL,
						channel);
			}
		}, 30, 30, TimeUnit.MINUTES);
	}

}
