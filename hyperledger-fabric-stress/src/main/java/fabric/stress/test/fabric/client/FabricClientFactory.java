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

/*
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

package fabric.stress.test.fabric.client;

import java.util.Properties;

import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;

import fabric.stress.test.fabric.context.FabricUserContext;
import fabric.stress.test.fabric.exception.FabricClientCreateException;

/**
 * FabricClient Factory
 */
public class FabricClientFactory {

    /**
     * Create a {@link HFCAClient} instance.
     *
     * @param caName     : name of ca (e.g : ca0.testnet.com)
     * @param protocol   : http or https
     * @param address    : ca server host
     * @param port       : ca server port
     * @param properties : properties
     *
     * @return A {HFCAClient} instance with given args.
     */
    public HFCAClient createCaClient(String caName, String protocol, String address, Integer port,
                                     Properties properties) throws FabricClientCreateException {

        return createCaClient(caName, protocol + "://" + address + ":" + port, properties);
    }

    /**
     * Create a {@link HFCAClient} instance.
     *
     * @param caName     : name of ca (e.g : ca0.testnet.com)
     * @param caLocation : location of ca (e.g : http://192.168.10.11:7054)
     *
     * @return A {HFCAClient} instance with given args.
     */
    public HFCAClient createCaClient(String caName, String caLocation)
            throws FabricClientCreateException {

        return createCaClient(caName, caLocation, null);
    }

    /**
     * Create a {@link HFCAClient} instance.
     *
     * @param caName     : name of ca (e.g : ca0.testnet.com)
     * @param caLocation : location of ca (e.g : http://192.168.10.11:7054)
     * @param properties : ca properties (including tls, etc)
     */
    public HFCAClient createCaClient(String caName, String caLocation, Properties properties)
            throws FabricClientCreateException {

        CryptoSuite cryptoSuite = null;

        try {
            cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
        } catch (Exception e) {
            throw new FabricClientCreateException(e);
        }

        return createCaClient(caName, caLocation, properties, cryptoSuite);
    }

    public HFCAClient createCaClient(String caName, String caLocation, Properties properties,
                                     CryptoSuite cryptoSuite) throws FabricClientCreateException {

        try {
            HFCAClient caClient = HFCAClient.createNewInstance(caName, caLocation, properties);
            caClient.setCryptoSuite(cryptoSuite);
            return caClient;
        } catch (Exception e) {
            throw new FabricClientCreateException(e);
        }
    }

    /**
     * Create a {@link HFClient} instance.
     *
     * @return A {@link HFClient} instance with default settings.
     */
    public HFClient createHFClient() throws FabricClientCreateException {
        return createHFClient(null);
    }

    /**
     * Create a {@link HFClient} instance with given {@link FabricUserContext}.
     *
     * @return A {@link HFClient} instance with given {@link FabricUserContext}.
     */
    public HFClient createHFClient(FabricUserContext fabricUserContext)
            throws FabricClientCreateException {

        try {
            CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();

            HFClient client = HFClient.createNewInstance();
            client.setCryptoSuite(cryptoSuite);

            if (fabricUserContext != null) {
                client.setUserContext(fabricUserContext);
            }

            return client;
        } catch (Exception e) {
            throw new FabricClientCreateException(e);
        }
    }
}
