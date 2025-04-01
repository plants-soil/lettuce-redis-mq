package com.plantssoil.mq;

/**
 * Message Publisher<br/>
 * Message sender could publish message to message channel<br/>
 * This Publisher will choose which channel should be used to send message by
 * the channelName {@link IMessagePublisher#channelName(String)}<br/>
 * Subscribers will subscribe messages from the same channel named by
 * channelName<br/>
 * Need put IMessagePublisher object into try-catch-resource clause to ensure
 * the object is closed properly after all operations done:<br/>
 * 
 * <pre>
 * <code>
 * IMessageServiceFactory<Message> f = IMessageServiceFactory.getFactoryInstance();
 * try (IMessagePublisher<Message> publisher = f.createMessagePublisher().channelName("PUBLISHER-ID-01-V1.0")) {
 *   ...
 * }
 * catch (Exception e) {
 *   ...
 * }
 * </code>
 * </pre>
 * 
 * @param
 * @auther duyong
 * @time 13:25:35
 */
public interface IMessagePublisher extends AutoCloseable {
	/**
	 * The channel name to send message
	 * 
	 * @param channelName channel name
	 * @return current publisher instance
	 */
	public IMessagePublisher channelName(String channelName);

	/**
	 * The channel type to send message, defaults to {@link ChannelType#QUEUE}
	 * 
	 * @param channelType channel type
	 * @return current publisher instance
	 */
	public IMessagePublisher channelType(ChannelType channelType);

	/**
	 * Send object message to message channel<br/>
	 * This Publisher will choose which channel should be used to send message by
	 * the channelName {@link IMessagePublisher#channelName(String)}<br/>
	 * Subscribers should subscribe messages from the same channel named by
	 * channelName<br/>
	 * 
	 * @param message object to be sent
	 */
	public <T> void publish(T message);
}
