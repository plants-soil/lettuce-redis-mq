package com.plantssoil.mq;

import java.time.Duration;

import com.plantssoil.mq.redis.RedisServiceFactoryBuilder;

/**
 * Message service factory, could use this factory to create message publisher /
 * consumer<br/>
 * This factory implements AutoCloseable interface, could release all resources
 * attached in {@link AutoCloseable#close()}
 * 
 * @auther duyong
 * @time 11:17:08
 */
public interface IMessageServiceFactory extends AutoCloseable {
	/**
	 * create message publisher (which is used to publish message)
	 * 
	 * @return Message Publisher instance
	 */
	public IMessagePublisher createMessagePublisher();

	/**
	 * create message consumer (which is used to consume message)
	 * 
	 * @return Message Consumer instance
	 */
	public IMessageConsumer createMessageConsumer();

	/**
	 * The default builder to create Message Service Factory
	 * 
	 * @return The implementation of default builder
	 * @auther duyong
	 * @time 11:52:47
	 */
	public static IBuilder builder() {
		return new RedisServiceFactoryBuilder();
	}

	/**
	 * The factory builder to create message service factory
	 * 
	 * @param <T> The data type to transfer between message server & client
	 * @auther duyong
	 * @time 11:49:26
	 */
	public interface IBuilder {
		/**
		 * Set the message server host
		 * 
		 * @param host Message server host name
		 * @return IBuilder instance
		 * @auther duyong
		 * @time 11:50:26
		 */
		public IBuilder host(String host);

		/**
		 * Set the message server port
		 * 
		 * @param port Message server port
		 * @return IBuilder instance
		 * @auther duyong
		 * @time 11:51:24
		 */
		public IBuilder port(int port);

		/**
		 * Set the message server password
		 * 
		 * @param password Message server password
		 * @return IBuilder instance
		 * @auther duyong
		 * @time 11:52:01
		 */
		public IBuilder password(String password);

		/**
		 * Set the message server connection timeout (Optional)
		 * 
		 * @param timeout Message server connection timeout, defaults to 30 seconds
		 * @return IBuilder instance
		 * @auther duyong
		 * @time 11:52:07
		 */
		public IBuilder Timeout(Duration timeout);

		/**
		 * Builder the IMessageServiceFactory instance
		 * 
		 * @return IMessageServiceFactory instance
		 * @auther duyong
		 * @time 11:52:12
		 */
		public IMessageServiceFactory build();
	}

}
