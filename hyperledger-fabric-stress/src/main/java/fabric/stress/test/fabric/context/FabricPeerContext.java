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
public class FabricPeerContext {

    private String name;                    // name of peer
    private String location;                // location of peer e.g) grpc://localhost:7051
    private Properties properties;          // peer properties
    private String hostAndPort;             // peer host e.g) peer0.peerorg1.testnet.com:7051
    private EnumSet<PeerRole> peerRoles;    // peer roles

    public static FabricPeerContextBuilder builder() {
        return new FabricPeerContextBuilder();
    }

    /**
     * Adds peer's default grpc settings if not exist.
     *
     * - grpc.NettyChannelBuilderOption.maxInboundMessageSize==9000000
     */
    public static Properties appendDefaultProperties(Properties properties) {
        if (properties == null) {
            properties = new Properties();
        }

        Object[] keyValues = {
                "grpc.NettyChannelBuilderOption.maxInboundMessageSize", 9000000
        };

        for (int i = 0; i < keyValues.length; i += 2) {
            if (properties.get(keyValues[i]) == null) {
                properties.put(keyValues[i], keyValues[i + 1]);
            }
        }

        return properties;
    }

    /**
     * Return default peer roles.
     */
    public static EnumSet<PeerRole> createDefaultPeerRoles() {
        return EnumSet.of(PeerRole.ENDORSING_PEER, PeerRole.LEDGER_QUERY,
                          PeerRole.CHAINCODE_QUERY, PeerRole.EVENT_SOURCE);
    }

    public FabricPeerContext() {
    }

    public FabricPeerContext(String name, String location, Properties properties, String hostAndPort,
                             EnumSet<PeerRole> peerRoles) {
        this.name = name;
        this.location = location;
        this.properties = properties;
        this.hostAndPort = hostAndPort;
        this.peerRoles = peerRoles;
    }

    public String getName() {
        return this.name;
    }

    public String getLocation() {
        return this.location;
    }

    public Properties getProperties() {
        return this.properties;
    }

    public String getHostAndPort() {
        return this.hostAndPort;
    }

    public EnumSet<PeerRole> getPeerRoles() {
        return this.peerRoles;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void setHostAndPort(String hostAndPort) {
        this.hostAndPort = hostAndPort;
    }

    public void setPeerRoles(EnumSet<PeerRole> peerRoles) {
        this.peerRoles = peerRoles;
    }

    public static class FabricPeerContextBuilder {
        private String name;
        private String location;
        private Properties properties;
        private String hostAndPort;
        private EnumSet<PeerRole> peerRoles;

        FabricPeerContextBuilder() {
        }

        public FabricPeerContext.FabricPeerContextBuilder name(String name) {
            this.name = name;
            return this;
        }

        public FabricPeerContextBuilder location(String location) {
            this.location = location;
            return this;
        }

        public FabricPeerContextBuilder properties(Properties properties) {
            this.properties = properties;
            return this;
        }

        public FabricPeerContextBuilder hostAndPort(String hostAndPort) {
            this.hostAndPort = hostAndPort;
            return this;
        }

        public FabricPeerContextBuilder peerRoles(EnumSet<PeerRole> peerRoles) {
            this.peerRoles = peerRoles;
            return this;
        }

        public FabricPeerContext build() {
            return new FabricPeerContext(this.name, this.location, this.properties, this.hostAndPort,
                                         this.peerRoles);
        }

        public String toString() {
            return "FabricPeerContext.FabricPeerContextBuilder(name=" + this.name + ", location="
                   + this.location + ", properties=" + this.properties + ", hostAndPort=" + this.hostAndPort
                   + ", peerRoles=" + this.peerRoles + ")";
        }
    }
}
