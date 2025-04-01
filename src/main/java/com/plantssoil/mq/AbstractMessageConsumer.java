package com.plantssoil.mq;

import java.util.ArrayList;
import java.util.List;

/**
 * The abstract message consumer which hold channel name & channel type &
 * consumer id & listeners
 * 
 * @author danialdy
 * @Date 6 Nov 2024 7:42:02 pm
 */
public abstract class AbstractMessageConsumer implements IMessageConsumer {
	private String channelName;
	private ChannelType channelType = ChannelType.QUEUE;
	private String consumerId;
	@SuppressWarnings("rawtypes")
	private List<IMessageListener> listeners = new ArrayList<>();

	@Override
	public IMessageConsumer channelName(String channelName) {
		this.channelName = channelName;
		return this;
	}

	@Override
	public IMessageConsumer channelType(ChannelType channelType) {
		this.channelType = channelType;
		return this;
	}

	@Override
	public IMessageConsumer addMessageListener(@SuppressWarnings("rawtypes") IMessageListener listener) {
		this.listeners.add(listener);
		return this;
	}

	@Override
	public IMessageConsumer consumerId(String consumerId) {
		this.consumerId = consumerId;
		return this;
	}

	@Override
	public String getChannelName() {
		return channelName;
	}

	@Override
	public ChannelType getChannelType() {
		return channelType;
	}

	@Override
	public String getConsumerId() {
		return consumerId;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<IMessageListener> getListeners() {
		return listeners;
	}

}
