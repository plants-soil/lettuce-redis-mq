# Lettuce Redis MQ (MessageQueue)
Lettuce Redis MQ implemented MessageQueue like message sender and receive which bases on Redis pub/sub & list

The messages could be consumed once one by one by each consumer as QUEUE, when the channel type is ChannelType.QUEUE (default type if not set the channel type);

The messages could be received and consumed by every consumer as TOPIC, when the channel type is ChannelType.TOPIC (should be set explicitly)

Key Classes：

- com.plantssoil.mq.IMessageServiceFactory, Used to create message publisher and message consumer
- com.plantssoil.mq.IMessagePublisher, Created by IMessageServiceFactory, used to send message
- com.plantssoil.mq.IMessageConsumer, Created by IMessageServiceFactory, used to receive and consume message

## Dependencies and tested
- Lettuce Redis MQ depends on io.lettuce.lettuce-core, which is an advanced Java client for Redis that supports synchronous, asynchronous, and reactive connections.
- Lettuce Redis MQ depends on com.fasterxml.jackson, which is widely used to process JSON and Object Mapping.
- Compiled and tested with Java 11+, io.lettuce.lettuce-core 6.5.0.RELEASE, com.fasterxml.jackson 2.18.2

## How to integrate Lettuce Redis MQ with your Java project
### Add dependencies to POM if you are using Maven

	<dependencies>
		<dependency>
			<groupId>com.plantssoil</groupId>
			<artifactId>lettuce-redis-mq</artifactId>
			<version>1.0.1</version>
		</dependency>
	</dependencies>

	<distributionManagement>
		<repository>
			<id>github</id>
			<name>GitHub plantssoil Apache Maven Packages</name>
			<url>https://maven.pkg.github.com/plants-soil/lettuce-redis-mq</url>
		</repository>
	</distributionManagement>

### Save your pom.xml, and enjoy!

## Example 1: Send and receive String message (ChannelType.QUEUE)
Lettuce Redis MQ could support String as message body, simple String or JSON String etc.

#### Create IMessageServiceFactory instance via it's builder:

	// host: Redis server domain name (redis.example.com) or IP address (192.168.0.6)
	// port: Redis server port, 6379 by default
	// password: Redis server password
	String host = System.getenv("REDIS_HOST");
	String port = System.getenv("REDIS_PORT");
	String password = System.getenv("REDIS_PASSWORD");
	IMessageServiceFactory factory = IMessageServiceFactory.builder().host(host).port(port).password(password).build();

#### Create IMessageConsumer to listen and receive message:

	String channel = "queue01";
	factory.createMessageConsumer().channelName(channel).addMessageListener(new IMessageListener<String>() {
				@Override
				public void onMessage(String message, String consumerId) {
					// consume the message here
					System.out.println(message);
				}
	
			}).consume(String.class);
	
#### Create IMessagePublisher to send message:

	String channel = "queue01";
	IMessagePublisher publisher = factory.createMessagePublisher().channelName(channel);
	publisher.publish("Test message");

## Example 2: Send and receive Java Bean message (ChannelType.QUEUE)
Lettuce Redis MQ could support Java Bean (Any java bean which compliant to Jackson) as message body.

#### Java Bean: TestBase

	package com.plantssoil.mq;
	
	import java.util.Date;
	
	public class TestBean {
		private String name;
		private int code;
		private Date d;
		private TestProperty p;
	
		public String getName() {
			return name;
		}
	
		public void setName(String name) {
			this.name = name;
		}
	
		public int getCode() {
			return code;
		}
	
		public void setCode(int code) {
			this.code = code;
		}
	
		public Date getD() {
			return d;
		}
	
		public void setD(Date d) {
			this.d = d;
		}
	
		public TestProperty getP() {
			return p;
		}
	
		public void setP(TestProperty p) {
			this.p = p;
		}
	
		@Override
		public String toString() {
			return "TestBean [name=" + name + ", code=" + code + ", d=" + d + ", p=" + p + "]";
		}
	}

#### Java Bean: TestProperty
	package com.plantssoil.mq;
	
	public class TestProperty {
		private String p1;
		private String p2;
	
		public String getP1() {
			return p1;
		}
	
		public void setP1(String p1) {
			this.p1 = p1;
		}
	
		public String getP2() {
			return p2;
		}
	
		public void setP2(String p2) {
			this.p2 = p2;
		}
	
		@Override
		public String toString() {
			return "TestProperty [p1=" + p1 + ", p2=" + p2 + "]";
		}
	}

#### Create IMessageServiceFactory instance via it's builder:

	// host: Redis server domain name (redis.example.com) or IP address (192.168.0.6)
	// port: Redis server port, 6379 by default
	// password: Redis server password
	String host = System.getenv("REDIS_HOST");
	String port = System.getenv("REDIS_PORT");
	String password = System.getenv("REDIS_PASSWORD");
	IMessageServiceFactory factory = IMessageServiceFactory.builder().host(host).port(port).password(password).build();

#### Create IMessageConsumer to listen and receive TestBean message:

	String channel = "queue02";
	factory.createMessageConsumer().channelName(channel).addMessageListener(new IMessageListener<TestBean>() {
		@Override
		public void onMessage(TestBean message, String consumerId) {
			System.out.println(message);
		}
	}).consume(TestBean.class);
	
#### Create IMessagePublisher to send TestBean message：

	TestProperty p = new TestProperty();
	p.setP1("p1");
	p.setP2("p2");
	TestBean b = new TestBean();
	b.setName("name");
	b.setCode(66);
	b.setD(new Date());
	b.setP(p);
	String channel = "queue02";
	IMessagePublisher publisher = factory.createMessagePublisher().channelName(channel);
	publisher.publish(b);

## Example 3: Send and receive message with ChannelType.TOPIC
Lettuce Redis MQ consumers which subscribed the specific channel could all receive the messages.

#### Create IMessageServiceFactory instance via it's builder:

	// host: Redis server domain name (redis.example.com) or IP address (192.168.0.6)
	// port: Redis server port, 6379 by default
	// password: Redis server password
	String host = System.getenv("REDIS_HOST");
	String port = System.getenv("REDIS_PORT");
	String password = System.getenv("REDIS_PASSWORD");
	IMessageServiceFactory factory = IMessageServiceFactory.builder().host(host).port(port).password(password).build();
	
#### Create 2 IMessageConsumer (means multiple subscribers) to subscribe message (with ChannelType.TOPIC):

		String channel = "topic03";
		// receive message by 2 consumers from channel with type as ChannelType.TOPIC
		IMessageConsumer c1 = factory.createMessageConsumer().channelName(channel).channelType(ChannelType.TOPIC);
		IMessageConsumer c2 = factory.createMessageConsumer().channelName(channel).channelType(ChannelType.TOPIC);
		c1.addMessageListener(new IMessageListener<String>() {
			@Override
			public void onMessage(String message, String consumerId) {
				System.out.println("Consumer 1 received message: " + message);
			}
		}).consume(String.class);
		c2.addMessageListener(new IMessageListener<String>() {
			@Override
			public void onMessage(String message, String consumerId) {
				System.out.println("Consumer 2 received message: " + message);
			}
		}).consume(String.class);

#### Create IMessagePublisher to send message (with ChannelType.TOPIC):

	String channel = "topic03";
	IMessagePublisher publisher = factory.createMessagePublisher().channelName(channel)
				.channelType(ChannelType.TOPIC);
	publisher.publish("Test message");
