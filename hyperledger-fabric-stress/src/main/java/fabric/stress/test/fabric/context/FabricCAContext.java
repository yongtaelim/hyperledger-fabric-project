/*
 * Copyright 2019 White-pin Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fabric.stress.test.fabric.context;

import java.util.EnumSet;
import java.util.Properties;

import org.hyperledger.fabric.sdk.Peer.PeerRole;

/**
 * Fabric peer node context
 */
public class FabricCAContext {

	private String name; // name of ca
	private String location; // location of ca e.g) http://localhost:7054
	private String hostAndPort; // ca host e.g) ca0.testnet.com:7054
	private String userName;
	private String userPassword;
	private String orgMsp;

	public static FabricPeerContextBuilder builder() {
		return new FabricPeerContextBuilder();
	}

	public FabricCAContext() {
	}

	public FabricCAContext(String name, String location, String userName, String userPassword, String orgMsp) {
		this.name = name;
		this.location = location;
		this.userName = userName;
		this.userPassword = userPassword;
		this.orgMsp = orgMsp;
	}

	public String getName() {
		return this.name;
	}

	public String getLocation() {
		return this.location;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getHostAndPort() {
		return hostAndPort;
	}

	public void setHostAndPort(String hostAndPort) {
		this.hostAndPort = hostAndPort;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}

	public String getOrgMsp() {
		return orgMsp;
	}

	public void setOrgMsp(String orgMsp) {
		this.orgMsp = orgMsp;
	}

	public static class FabricPeerContextBuilder {
		private String name; // name of ca
		private String location; // location of ca e.g) http://localhost:7054
		private String userName;
		private String userPassword;
		private String orgMsp;
		
		FabricPeerContextBuilder() {
		}

		public FabricCAContext.FabricPeerContextBuilder name(String name) {
			this.name = name;
			return this;
		}

		public FabricPeerContextBuilder location(String location) {
			this.location = location;
			return this;
		}

		public FabricPeerContextBuilder userName(String userName) {
			this.userName = userName;
			return this;
		}

		public FabricPeerContextBuilder userPassword(String userPassword) {
			this.userPassword = userPassword;
			return this;
		}
		
		public FabricPeerContextBuilder orgMsp(String orgMsp) {
			this.orgMsp = orgMsp;
			return this;
		}

		public FabricCAContext build() {
			return new FabricCAContext(name, location, userName, userPassword, orgMsp);
		}

	}
}
