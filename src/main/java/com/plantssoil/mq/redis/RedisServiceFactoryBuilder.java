package com.plantssoil.mq.redis;

import java.time.Duration;

import com.plantssoil.mq.IMessageServiceFactory;
import com.plantssoil.mq.IMessageServiceFactory.IBuilder;

/**
 * The Message Service Factory builder implemented base on Redis as the MQ
 * server
 * 
 * @author duyong
 * @time 11:43:55
 */
public class RedisServiceFactoryBuilder implements IBuilder {
	private static RedisServiceFactory instance;
	private RedisClientFactory.Builder redisClientBuilder = RedisClientFactory.builder();

	@Override
	public IBuilder host(String host) {
		redisClientBuilder.host(host);
		return this;
	}

	@Override
	public IBuilder port(int port) {
		redisClientBuilder.port(port);
		return this;
	}

	@Override
	public IBuilder password(String password) {
		redisClientBuilder.password(password);
		return this;
	}

	@Override
	public IBuilder Timeout(Duration timeout) {
		redisClientBuilder.Timeout(timeout);
		return this;
	}

	@Override
	public IMessageServiceFactory build() {
		if (instance == null) {
			synchronized (this) {
				if (instance == null) {
					instance = new RedisServiceFactory(redisClientBuilder.build());
				}
			}
		}
		return instance;
	}
}