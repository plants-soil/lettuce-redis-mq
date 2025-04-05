package com.plantssoil.mq.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.plantssoil.common.io.ObjectJsonSerializer;
import com.plantssoil.mq.AbstractMessageConsumer;
import com.plantssoil.mq.ChannelType;
import com.plantssoil.mq.IMessageListener;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.pubsub.api.reactive.RedisPubSubReactiveCommands;

/**
 * The IMessageSubscriber implementation base on Redis PubSub & List
 * 
 * @param <T> the message type
 * @author duyong
 * @time 12:30:30
 */
class MessageConsumer extends AbstractMessageConsumer {
	private RedisPubSubReactiveCommands<String, String> command;
	private StatefulRedisConnection<String, String> consumerConnection;

	MessageConsumer(RedisPubSubReactiveCommands<String, String> command,
			StatefulRedisConnection<String, String> consumerConnection) {
		this.command = command;
		this.consumerConnection = consumerConnection;
	}

	@Override
	public <T> void consume(Class<T> clazz) {
		if (ChannelType.TOPIC == getChannelType()) {
			this.command.observeChannels().doOnNext((message) -> {
				if (message.getMessage() == null || !message.getChannel().equals(getChannelName())) {
					return;
				}
				try {
					consumeMessage(message.getMessage(), clazz);
				} catch (JsonProcessingException e) {
					throw new RuntimeException(e);
				}
			}).doOnError(ex -> {
				ex.printStackTrace();
			}).subscribe();
			this.command.subscribe(getChannelName()).subscribe();
		} else {
			this.command.observeChannels().doOnNext((message) -> {
				if (!message.getChannel().equals(MessagePublisher.WEBIZ_QUEUE_NOTIFICATION_CHANNEL)) {
					return;
				}
				String listName = message.getMessage();
				if (listName == null || !listName.equals(getChannelName())) {
					return;
				}
				try {
					consumeListMessage(clazz, listName);
				} catch (JsonProcessingException e) {
					throw new RuntimeException(e);
				}
			}).doOnError(ex -> {
				ex.printStackTrace();
			}).subscribe();
			this.command.subscribe(MessagePublisher.WEBIZ_QUEUE_NOTIFICATION_CHANNEL).subscribe();
		}
	}

	private <T> void consumeListMessage(Class<T> clazz, String listName)
			throws JsonMappingException, JsonProcessingException {
		String v = this.consumerConnection.sync().rpop(listName);
		while (v != null) {
			consumeMessage(v, clazz);
			try {
				Thread.sleep(0);
			} catch (InterruptedException e) {
			}
			v = this.consumerConnection.sync().rpop(listName);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private <T> void consumeMessage(String t, Class<T> clazz) throws JsonMappingException, JsonProcessingException {
		T message = ObjectJsonSerializer.getInstance().unserialize(t, clazz);
		for (IMessageListener l : getListeners()) {
			l.onMessage(message, getConsumerId());
		}
	}

	@Override
	public void close() {
	}

}
