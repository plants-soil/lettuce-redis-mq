package com.plantssoil.mq;

import java.util.List;

/**
 * Consume message from specific channel<br/>
 * Consumer could get messages from Message Queue Server which publisher created
 * (so the MQ Channel Name should be consistent with publisher's Channel
 * Name)<br/>
 * 
 * @author danialdy
 * @Date 1 Nov 2024 4:55:17 pm
 */
public interface IMessageConsumer {
	/**
	 * The channel name to receive message
	 * 
	 * @param channelName channel name
	 * @return current consumer instance
	 */
	public IMessageConsumer channelName(String channelName);

	/**
	 * The channel type to receive message, defaults to {@link ChannelType#QUEUE}
	 * 
	 * @param channelType channel type
	 * @return current consumer instance
	 */
	public IMessageConsumer channelType(ChannelType channelType);

	/**
	 * Add the listener which to receive and process messages from publisher<br/>
	 * Could call this method multi-times to add multiple listeners<br/>
	 * 
	 * @param listener the message receiver and handler
	 * @return current consumer instance
	 */
	public <T> IMessageConsumer addMessageListener(IMessageListener<T> listener);

	/**
	 * set the consumer id which will be brought to {@link IStringMessageListener}
	 * <br/>
	 * if not set consumer id via this method, null consumerTag will be passed to
	 * {@link DeliverCallback#}<br/>
	 * 
	 * @param consumerId consumer id
	 * @return current consumer instance
	 */
	public IMessageConsumer consumerId(String consumerId);

	/**
	 * Consume the message from the specific channel<br/>
	 * Consumer could get message from Message Queue Server which publisher created
	 * (so the MQ channel name should be consistent with publisher)<br/>
	 * 
	 * @param clazz the Message Object class,
	 *              {@link IMessageListener#onMessage(Object, String)} will receive
	 *              the instance of this class as the first parameter
	 * 
	 */
	public <T> void consume(Class<T> clazz);

	/**
	 * Get channel name
	 * 
	 * @return channel name
	 */
	public String getChannelName();

	/**
	 * Get channel type
	 * 
	 * @return channel type
	 */
	public ChannelType getChannelType();

	/**
	 * Get consumer id, if provided
	 * 
	 * @return consumer id
	 */
	public String getConsumerId();

	/**
	 * Get message listener list
	 * 
	 * @return message listener list
	 */
	public <T> List<IMessageListener<T>> getListeners();

	/**
	 * Close the consumer and stop receive message from publisher
	 */
	public void close();
}
