package fabric.stress.test.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@ConfigurationProperties(prefix = "hyperledger.fabric")
public class PropertiesConfig {
	
	private List<Orderer> orderers;
	private List<Peer> peers;
	private String msp;
	private String affiliation;
	private String userName;
	private String channel;

	public List<Orderer> getOrderers() {
		return orderers;
	}

	public void setOrderers(List<Orderer> orderers) {
		this.orderers = orderers;
	}

	public List<Peer> getPeers() {
		return peers;
	}

	public void setPeers(List<Peer> peers) {
		this.peers = peers;
	}

	public String getMsp() {
		return msp;
	}

	public void setMsp(String msp) {
		this.msp = msp;
	}

	public String getAffiliation() {
		return affiliation;
	}

	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public static class Orderer {
		private String name;
		private String location;
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getLocation() {
			return location;
		}
		public void setLocation(String location) {
			this.location = location;
		}
	}
	
	public static class Peer {
		private String name;
		private String location;
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getLocation() {
			return location;
		}
		public void setLocation(String location) {
			this.location = location;
		}

	}
}
