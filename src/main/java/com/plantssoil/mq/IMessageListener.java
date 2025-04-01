package com.plantssoil.mq;

/**
 * Listen the message comes from subscribed Message Channel<br/>
 * Will process the messages<br/>
 * 
 * @param <T> the message type
 * @author danialdy
 * @Date 23 Nov 2024 9:30:20 pm
 */
public interface IMessageListener<T> {
	/**
	 * MQ will call this method after message received<br/>
	 * Could process the messages in this method<br/>
	 * 
	 * @param message    message comes from publisher
	 * @param consumerId the consumer id
	 */
	public void onMessage(T message, String consumerId);
}
