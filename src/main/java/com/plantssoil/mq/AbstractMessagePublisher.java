package com.plantssoil.mq;

/**
 * The abstract message publisher which hold channel name & channel type
 * 
 * @author danialdy
 * @Date 6 Nov 2024 7:37:19 pm
 */
public abstract class AbstractMessagePublisher implements IMessagePublisher {
	private String channelName;
	private ChannelType channelType = ChannelType.QUEUE;

	@Override
	public IMessagePublisher channelName(String channelName) {
		this.channelName = channelName;
		return this;
	}

	@Override
	public IMessagePublisher channelType(ChannelType channelType) {
		this.channelType = channelType;
		return this;
	}

	public String getChannelName() {
		return channelName;
	}

	public ChannelType getChannelType() {
		return channelType;
	}

}
