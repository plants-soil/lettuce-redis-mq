package com.plantssoil.mq.redis;

import com.plantssoil.mq.AbstractMessagePublisher;
import com.plantssoil.mq.ChannelType;
import com.plantssoil.mq.json.ObjectJsonSerializer;

import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;

/**
 * The IMessagePublisher implementation base on Redis PubSub & List
 * 
 * @author danialdy
 * @Date 6 Nov 2024 4:34:30 pm
 */
class MessagePublisher extends AbstractMessagePublisher {
	final static String WEBIZ_QUEUE_NOTIFICATION_CHANNEL = "com.webiz.mq.notification.channel";
	private RedisAsyncCommands<String, String> command;
	private RedisPubSubAsyncCommands<String, String> pubsubCommand;

	/**
	 * Constructor mandatory
	 * 
	 * @param command Redis command - to execute publish command
	 */
	protected MessagePublisher(RedisAsyncCommands<String, String> command,
			RedisPubSubAsyncCommands<String, String> pubsubCommand) {
		this.command = command;
		this.pubsubCommand = pubsubCommand;
	}

	@Override
	public <T> void publish(T message) {
		if (this.getChannelName() == null) {
			throw new RuntimeException("The [queueName] should not be null!");
		}
		String channel = getChannelName();
		if (ChannelType.TOPIC == getChannelType()) {
			this.pubsubCommand.publish(channel, ObjectJsonSerializer.getInstance().serialize(message));
		} else {
			// push message to the channel left end
			this.command.lpush(channel, ObjectJsonSerializer.getInstance().serialize(message));
			this.pubsubCommand.publish(WEBIZ_QUEUE_NOTIFICATION_CHANNEL, channel);
		}
	}

	@Override
	public void close() throws Exception {
		// nothing need to be clean for redis
	}
}
