package com.plantssoil.mq.redis;

import java.time.Duration;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;

/**
 * Redis client factory, in order to get redis client
 * 
 * @auther duyong
 * @time 12:23:01
 */
public class RedisClientFactory {
	private static RedisClientFactory instance;
	private RedisClient client;
	private RedisURI redisUri;

	private RedisClientFactory(RedisURI uri) {
		this.redisUri = uri;
		this.client = RedisClient.create(uri);
	}

	/**
	 * The redis client factory builder, to build redis client factory and setup all
	 * connection parameters for redis client
	 * 
	 * @auther duyong
	 * @time 12:23:24
	 */
	public static class Builder {
		private RedisURI.Builder redisUriBuilder;

		private Builder() {
			redisUriBuilder = RedisURI.builder();
		}

		/**
		 * Set the redis host
		 * 
		 * @param host Redis host
		 * @return Builder instance
		 * @auther duyong
		 * @time 12:24:09
		 */
		public Builder host(String host) {
			redisUriBuilder.withHost(host);
			return this;
		}

		/**
		 * Set the redis port
		 * 
		 * @param port Redis port
		 * @return Builder instance
		 * @auther duyong
		 * @time 12:24:46
		 */
		public Builder port(int port) {
			redisUriBuilder.withPort(port);
			return this;
		}

		/**
		 * Set the redis password
		 * 
		 * @param password Redis password
		 * @return Builder instance
		 * @auther duyong
		 * @time 12:24:58
		 */
		public Builder password(String password) {
			redisUriBuilder.withPassword(password.toCharArray());
			return this;
		}

		/**
		 * Set the redis database
		 * 
		 * @param database Redis database
		 * @return Builder instance
		 * @auther duyong
		 * @time 12:25:07
		 */
		public Builder database(int database) {
			redisUriBuilder.withDatabase(database);
			return this;
		}

		/**
		 * Set the redis use ssl or not
		 * 
		 * @param ssl Redis connection with SSL or not
		 * @return Builder instance
		 * @auther duyong
		 * @time 12:25:14
		 */
		public Builder ssl(boolean ssl) {
			redisUriBuilder.withSsl(ssl);
			return this;
		}

		/**
		 * Set the connection timeout of redis
		 * 
		 * @param timeout The redis connection timeout
		 * @return Builder instance
		 * @auther duyong
		 * @time 12:25:26
		 */
		public Builder Timeout(Duration timeout) {
			redisUriBuilder.withTimeout(timeout);
			return this;
		}

		/**
		 * Build redis client factory from builder
		 * 
		 * @return Redis client factory
		 * @auther duyong
		 * @time 12:25:37
		 */
		public RedisClientFactory build() {
			if (instance == null) {
				synchronized (this) {
					if (instance == null) {
						instance = new RedisClientFactory(redisUriBuilder.build());
					}
				}
			}
			return instance;
		}
	}

	/**
	 * Get redis client factory builder
	 * 
	 * @return Redis client factory builder
	 * @auther duyong
	 * @time 12:33:14
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Get redis client instance
	 * 
	 * @return Redis client
	 * @auther duyong
	 * @time 12:29:25
	 */
	public RedisClient getRedisClient() {
		return this.client;
	}

	/**
	 * Get redis uri
	 * 
	 * @return Redis URI
	 * @auther duyong
	 * @time 12:45:46
	 */
	public RedisURI getRedisUri() {
		return this.redisUri;
	}

}
