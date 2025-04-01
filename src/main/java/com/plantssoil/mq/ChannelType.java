package com.plantssoil.mq;

/**
 * The type of message channel, used to determine the behavior of message
 * subscription
 * 
 * @author danialdy
 * @Date 26 Dec 2024 3:36:45 pm
 */
public enum ChannelType {
	/**
	 * Queue, could be consumed by only one consumer (who subscribed) sequentially
	 */
	QUEUE,

	/**
	 * Topic, could be consumed by all consumers (who subscribed), as broadcast
	 */
	TOPIC
}
