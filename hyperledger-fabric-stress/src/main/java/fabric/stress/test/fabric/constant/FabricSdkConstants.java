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

package fabric.stress.test.fabric.constant;

import java.util.Arrays;
import java.util.List;

import org.hyperledger.fabric_ca.sdk.Attribute;

/**
 * Constants for fabric.
 */
public class FabricSdkConstants {

    /**
     * Admin attributes
     */
    public static final List<Attribute> ADMIN_ATTRIBUTES = Arrays.asList(
            new Attribute("hf.Registrar.Roles", "*")
            , new Attribute("hf.Registrar.DelegateRoles", "*")
            , new Attribute("hf.Registrar.Attributes", "*")
            , new Attribute("hf.GenCRL", "1")
            , new Attribute("hf.Revoker", "1")
            , new Attribute("hf.AffiliationMgr", "1")
            , new Attribute("hf.IntermediateCA", "1")
    );

    /**
     * Orderer identity type
     */
    public static final String ORDERER_IDENTITY_TYPE = "orderer";

    /**
     * Peer identity type
     */
    public static final String PEER_IDENTITY_TYPE = "peer";

    /**
     * Client identity type
     */
    public static final String CLIENT_IDENTITY_TYPE = "client";
}
