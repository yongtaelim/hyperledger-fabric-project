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

import java.util.Set;

import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;

/**
 * Fabric user context
 */
public class FabricUserContext implements User {

    /**
     * default user name.
     */
    public static final String DEFAULT_USER_NAME = "FabricUser";

    private String name;
    private Set<String> roles;
    private String account;
    private String affiliation;
    private String mspId;
    private String password;

    private String enrollmentSecret;
    private Enrollment enrollment;
    private boolean isAdmin;

    /**
     * @return A {@linke FabricUserContext} with default name and given {@link Enrollment}
     */
    public static FabricUserContext newInstance(Enrollment enrollment) {
        return newInstance(DEFAULT_USER_NAME, enrollment);
    }

    /**
     * @return A {@linke FabricUserContext} with given name and {@link Enrollment}
     */
    public static FabricUserContext newInstance(String name, Enrollment enrollment) {
        return builder()
                .name(name)
                .enrollment(enrollment)
                .build();
    }

    public FabricUserContext() {
    }

    public FabricUserContext(String name, Set<String> roles, String account, String affiliation,
                             String mspId, String password, String enrollmentSecret,
                             Enrollment enrollment, boolean isAdmin) {
        this.name = name;
        this.roles = roles;
        this.account = account;
        this.affiliation = affiliation;
        this.mspId = mspId;
        this.password = password;
        this.enrollmentSecret = enrollmentSecret;
        this.enrollment = enrollment;
        this.isAdmin = isAdmin;
    }

    // getters, setters, builder

    public static Builder builder() {
        return new Builder();
    }

    public String getName() {
        return this.name;
    }

    public Set<String> getRoles() {
        return this.roles;
    }

    public String getAccount() {
        return this.account;
    }

    public String getAffiliation() {
        return this.affiliation;
    }

    public String getMspId() {
        return this.mspId;
    }

    public String getPassword() {
        return this.password;
    }

    public String getEnrollmentSecret() {
        return this.enrollmentSecret;
    }

    public Enrollment getEnrollment() {
        return this.enrollment;
    }

    public boolean isAdmin() {
        return this.isAdmin;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    public void setMspId(String mspId) {
        this.mspId = mspId;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEnrollmentSecret(String enrollmentSecret) {
        this.enrollmentSecret = enrollmentSecret;
    }

    public void setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String toString() {
        return "FabricUserContext(name=" + getName()
               + ", roles=" + getRoles()
               + ", account=" + getAccount()
               + ", affiliation=" + getAffiliation() + ", mspId=" + getMspId()
               + ", isAdmin=" + isAdmin() + ")";
    }

    public static class Builder {

        private String name;
        private Set<String> roles;
        private String account;
        private String affiliation;
        private FabricOrgType orgType;
        private String mspId;
        private String password;
        private String enrollmentSecret;
        private Enrollment enrollment;
        private boolean isAdmin;

        Builder() {
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder roles(Set<String> roles) {
            this.roles = roles;
            return this;
        }

        public Builder account(String account) {
            this.account = account;
            return this;
        }

        public Builder affiliation(String affiliation) {
            this.affiliation = affiliation;
            return this;
        }

        public Builder orgType(FabricOrgType orgType) {
            this.orgType = orgType;
            return this;
        }

        public Builder mspId(String mspId) {
            this.mspId = mspId;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder enrollmentSecret(String enrollmentSecret) {
            this.enrollmentSecret = enrollmentSecret;
            return this;
        }

        public Builder enrollment(Enrollment enrollment) {
            this.enrollment = enrollment;
            return this;
        }

        public Builder isAdmin(boolean isAdmin) {
            this.isAdmin = isAdmin;
            return this;
        }

        public FabricUserContext build() {
            return new FabricUserContext(name, roles, account, affiliation, mspId,
                                         password, enrollmentSecret, enrollment, isAdmin);
        }
    }
}
