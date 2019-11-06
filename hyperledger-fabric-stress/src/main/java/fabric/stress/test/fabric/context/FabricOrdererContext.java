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

import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Fabric orderer node context
 */
public class FabricOrdererContext {

    private String name;            // name of orderer
    private String location;        // location of orderer e.g) grpc://localhost:7050
    private Properties properties;  // orderer properties

    public static FabricOrdererContextBuilder builder() {
        return new FabricOrdererContextBuilder();
    }

    /**
     * Adds orderer's default grpc settings if not exist.
     *
     * - grpc.NettyChannelBuilderOption.keepAliveTime={ 5L, TimeUnit.MINUTES }
     * - grpc.NettyChannelBuilderOption.keepAliveTimeout={ 8L, TimeUnit.SECONDS }
     * - grpc.NettyChannelBuilderOption.keepAliveWithoutCalls={ true }
     */
    public static Properties appendDefaultProperties(Properties properties) {
        if (properties == null) {
            properties = new Properties();
        }

        Object[] keyValues = {
                "grpc.NettyChannelBuilderOption.keepAliveTime", new Object[] { 5L, TimeUnit.MINUTES }
                , "grpc.NettyChannelBuilderOption.keepAliveTimeout", new Object[] { 8L, TimeUnit.SECONDS }
                , "grpc.NettyChannelBuilderOption.keepAliveWithoutCalls", new Object[] { true }
        };

        for (int i = 0; i < keyValues.length; i += 2) {
            if (properties.get(keyValues[i]) == null) {
                properties.put(keyValues[i], keyValues[i + 1]);
            }
        }

        return properties;
    }

    public FabricOrdererContext() {
    }

    public FabricOrdererContext(String name, String location, Properties properties) {
        this.name = name;
        this.location = location;
        this.properties = properties;
    }

    // getters, setters, builder
    public String getName() {
        return this.name;
    }

    public String getLocation() {
        return this.location;
    }

    public Properties getProperties() {
        return this.properties;
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

    public static class FabricOrdererContextBuilder {
        private String name;
        private String location;
        private Properties properties;

        FabricOrdererContextBuilder() {
        }

        public FabricOrdererContext.FabricOrdererContextBuilder name(String name) {
            this.name = name;
            return this;
        }

        public FabricOrdererContext.FabricOrdererContextBuilder location(String location) {
            this.location = location;
            return this;
        }

        public FabricOrdererContext.FabricOrdererContextBuilder properties(Properties properties) {
            this.properties = properties;
            return this;
        }

        public FabricOrdererContext build() {
            return new FabricOrdererContext(this.name, this.location, this.properties);
        }

        public String toString() {
            return "FabricOrdererContext.FabricOrdererContextBuilder(name=" + this.name + ", location="
                   + this.location + ", properties=" + this.properties + ")";
        }
    }

}
