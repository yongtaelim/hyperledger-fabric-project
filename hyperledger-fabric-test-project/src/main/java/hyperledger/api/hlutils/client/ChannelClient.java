package hyperledger.api.hlutils.client;

import org.hyperledger.fabric.sdk.Channel;

public class ChannelClient {
	String name;
	Channel channel;
	FabricClient fabClient;

	public String getName() {
		return name;
	}

	public Channel getChannel() {
		return channel;
	}

	public FabricClient getFabClient() {
		return fabClient;
	}

	/**
	 * Constructor
	 * 
	 * @param name
	 * @param channel
	 * @param fabClient
	 */
	public ChannelClient(String name, Channel channel, FabricClient fabClient) {
		this.name = name;
		this.channel = channel;
		this.fabClient = fabClient;
	}
}
