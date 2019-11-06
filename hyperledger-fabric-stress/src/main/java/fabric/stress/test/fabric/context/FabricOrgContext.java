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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Fabric organization context.
 */
public class FabricOrgContext {

    private FabricOrgType orgType;
    private String name;
    private String domain;

    private Map<String, FabricUserContext> users = new HashMap<>();
    private Map<String, FabricPeerContext> peers = new HashMap<>();
    private Map<String, FabricOrdererContext> orderers = new HashMap<>();

    public static FabricOrgContextBuilder builder() {
        return new FabricOrgContextBuilder();
    }

    public FabricOrgContext() {
    }

    public FabricOrgContext(FabricOrgType orgType, String name, String domain) {
        this.orgType = orgType;
        this.name = name;
        this.domain = domain;
    }

    /**
     * Adds a fabric user context given name and {@link FabricUserContext}.
     */
    public void addUser(String name, FabricUserContext userContext) {
        synchronized (users) {
            users.put(name, userContext);
        }
    }

    /**
     * Adds a fabric peer context given name and {@link FabricPeerContext}.
     */
    public void addPeer(String name, FabricPeerContext peerContext) {
        synchronized (peers) {
            peers.put(name, peerContext);
        }
    }

    /**
     * Adds a fabric orderer context given name and {@link FabricOrdererContext}.
     */
    public void addOrderer(String name, FabricOrdererContext ordererContext) {
        synchronized (orderers) {
            orderers.put(name, ordererContext);
        }
    }

    /**
     * Return a admin {@link FabricUserContext}.
     *
     * @throws IllegalStateException if there is no a admin in this organization
     * @return A {@link FabricUserContext} with admin attributes.
     */
    public FabricUserContext getAdmin() {
        synchronized (users) {
            List<FabricUserContext> admin = users.values()
                                                 .stream()
                                                 .filter(user -> user.isAdmin())
                                                 .collect(Collectors.toList());

            if (admin.size() != 1) {
                throw new IllegalStateException("Admin must be one but exist "
                                                + admin.size() + " admins.");
            }

            return admin.get(0);
        }
    }

    // getters, setters, builder
    public FabricOrgType getOrgType() {
        return this.orgType;
    }

    public String getName() {
        return this.name;
    }

    public String getDomain() {
        return this.domain;
    }

    public Map<String, FabricUserContext> getUsers() {
        return this.users;
    }

    public Map<String, FabricPeerContext> getPeers() {
        return this.peers;
    }

    public Map<String, FabricOrdererContext> getOrderers() {
        return this.orderers;
    }

    public void setOrgType(FabricOrgType orgType) {
        this.orgType = orgType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setUsers(Map<String, FabricUserContext> users) {
        this.users = users;
    }

    public void setPeers(Map<String, FabricPeerContext> peers) {
        this.peers = peers;
    }

    public void setOrderers(Map<String, FabricOrdererContext> orderers) {
        this.orderers = orderers;
    }

    public static class FabricOrgContextBuilder {
        private FabricOrgType orgType;
        private String name;
        private String domain;

        FabricOrgContextBuilder() {
        }

        public FabricOrgContextBuilder orgType(FabricOrgType orgType) {
            this.orgType = orgType;
            return this;
        }

        public FabricOrgContextBuilder name(String name) {
            this.name = name;
            return this;
        }

        public FabricOrgContextBuilder domain(String domain) {
            this.domain = domain;
            return this;
        }

        public FabricOrgContext build() {
            return new FabricOrgContext(this.orgType, this.name, this.domain);
        }

        public String toString() {
            return "FabricOrgContext.FabricOrgContextBuilder(orgType=" + this.orgType + ", name=" + this.name
                   + ", domain=" + this.domain + ")";
        }
    }
}
