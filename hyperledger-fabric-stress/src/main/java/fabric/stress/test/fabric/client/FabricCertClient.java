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

package fabric.stress.test.fabric.client;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric_ca.sdk.Attribute;
import org.hyperledger.fabric_ca.sdk.EnrollmentRequest;
import org.hyperledger.fabric_ca.sdk.HFCAAffiliation;
import org.hyperledger.fabric_ca.sdk.HFCAAffiliation.HFCAAffiliationResp;
import org.hyperledger.fabric_ca.sdk.HFCACertificateRequest;
import org.hyperledger.fabric_ca.sdk.HFCACertificateResponse;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.HFCACredential;
import org.hyperledger.fabric_ca.sdk.HFCAIdentity;
import org.hyperledger.fabric_ca.sdk.HFCAX509Certificate;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;
import org.hyperledger.fabric_ca.sdk.exception.AffiliationException;
import org.hyperledger.fabric_ca.sdk.exception.EnrollmentException;
import org.hyperledger.fabric_ca.sdk.exception.IdentityException;
import org.hyperledger.fabric_ca.sdk.exception.InvalidArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fabric.stress.test.fabric.context.FabricUserContext;
import fabric.stress.test.fabric.parse.FabricCertParser;


/**
 * Fabric cert client
 */
public class FabricCertClient {

    private static final Logger logger = LoggerFactory.getLogger(FabricCertClient.class);

    /**
     * Create a affilation
     *
     * @return true if created or already exist, otherwise false
     */
    public boolean createNewAffiliation(HFCAClient caClient, Enrollment enrollment, String name)
            throws InvalidArgumentException, AffiliationException {

        requireNonNull(caClient, "caClient");
        requireNonNull(name, "name");

        // check already exist
        Optional<HFCAAffiliation> affOptional = getAffiliationByName(caClient, enrollment, name);

        if (affOptional.isPresent()) {
            logger.trace("Skip new affiliation because already exist {}", name);
            return true;
        }

        HFCAAffiliation hfcaAffiliation = caClient.newHFCAAffiliation(name);
        HFCAAffiliationResp response = hfcaAffiliation.create(FabricUserContext.newInstance(enrollment));

        return is2xxSuccessful(response);
    }

    /**
     * Return a affiliation given enrollment
     *
     * @return An {@link Optional} containing {@link HFCAAffiliation} given {@link Enrollment}
     * or else an empty {@link Optional}.
     */
    public Optional<HFCAAffiliation> getAffiliations(HFCAClient caClient, Enrollment enrollment)
            throws InvalidArgumentException, AffiliationException {

        requireNonNull(caClient, "caClient");
        HFCAAffiliation affiliation = caClient.getHFCAAffiliations(FabricUserContext.newInstance(enrollment));

        return Optional.ofNullable(affiliation);
    }

    /**
     * Return a affiliation given enrollment and name
     *
     * @return An {@link Optional} containing {@link HFCAAffiliation} given {@link Enrollment} and name
     *         or else an empty {@link Optional}.
     */
    public Optional<HFCAAffiliation> getAffiliationByName(HFCAClient caClient, Enrollment enrollment,
                                                          String name)
            throws InvalidArgumentException, AffiliationException {

        requireNonNull(caClient, "caClient");
        requireNonNull(name, "name");

        HFCAAffiliation affiliation = caClient.getHFCAAffiliations(FabricUserContext.newInstance(enrollment));

        return Optional.ofNullable(searchAffiliationWithRecursively(affiliation, name));
    }

    /**
     * Remove a affiliation by name if exist.
     */
    public void deleteAffiliation(HFCAClient caClient, Enrollment enrollment, String name)
            throws AffiliationException, InvalidArgumentException {

        requireNonNull(caClient, "caClient");
        requireNonNull(name, "name");

        Optional<HFCAAffiliation> affOptional = getAffiliationByName(caClient, enrollment, name);

        if (!affOptional.isPresent()) {
            return;
        }

        HFCAAffiliation affiliation = affOptional.get();
        HFCAAffiliationResp response = affiliation.delete(FabricUserContext.newInstance(enrollment));
        if (!is2xxSuccessful(response)) {
            logger.warn("Failed to delete affiliation. status code :  {}", response.getStatusCode());
        }
    }

    /**
     * Register a new identity with maxEnrollments == -1.
     *
     * @return true if register, otherwise false
     */
    public boolean registerNewIdentity(HFCAClient caClient, Enrollment enrollment, String type, String name,
                                       String password, String affiliation, List<Attribute> attributes)
            throws Exception {

        return registerNewIdentity(caClient, enrollment, type, name, password, affiliation, attributes, -1);
    }

    /**
     * Register a new identity.
     *
     * @return true if register, otherwise false
     */
    public boolean registerNewIdentity(HFCAClient caClient, Enrollment enrollment, String type, String name,
                                       String password, String affiliation, List<Attribute> attributes,
                                       int maxEnrollments) throws Exception {

        requireNonNull(caClient, "caClient");
        requireNonNull(enrollment, "enrollment");
        requireNonNull(password, "password");

        RegistrationRequest rr = new RegistrationRequest(name, affiliation);

        rr.setType(type);
        rr.setSecret(password);
        rr.setMaxEnrollments(maxEnrollments);

        if (attributes != null && !attributes.isEmpty()) {
            for (Attribute attribute : attributes) {
                rr.addAttribute(attribute);
            }
        }

        String secret = caClient.register(rr, FabricUserContext.newInstance(enrollment));
        return password.equals(secret);
    }

    /**
     * Return a identity given name and enrollment.
     *
     * @return An {@link Optional} containing {@link HFCAIdentity} given {@link Enrollment} and name
     *         or else an empty {@link Optional}.
     */
    public Optional<HFCAIdentity> getIdentityByName(HFCAClient caClient, Enrollment enrollment, String name)
            throws InvalidArgumentException, IdentityException {

        requireNonNull(caClient, "caClient");

        Collection<HFCAIdentity>
                identities = caClient.getHFCAIdentities(FabricUserContext.newInstance(enrollment));

        for (HFCAIdentity identity : identities) {
            if (identity.getEnrollmentId().equals(name)) {
                return Optional.of(identity);
            }
        }

        return Optional.empty();
    }

    /**
     * Request to enroll with default {@link EnrollmentRequest}.
     *
     * @return A {@link Enrollment} containing cert and private key
     */
    public Enrollment enroll(HFCAClient caClient, String enrollmentId, String enrollmentSecret)
            throws EnrollmentException, InvalidArgumentException {

        return enroll(caClient, enrollmentId, enrollmentSecret, createDefaultEnrollmentRequest());
    }

    /**
     * Request to enroll given request.
     *
     * @return A {@link Enrollment} containing cert and private key
     */
    public Enrollment enroll(HFCAClient caClient, String enrollmentId, String enrollmentSecret,
                             EnrollmentRequest request) throws EnrollmentException, InvalidArgumentException {

        requireNonNull(caClient, "caClient");
        requireNonNull(request, "request");

        return caClient.enroll(enrollmentId, enrollmentSecret, request);
    }

    /**
     * Return list of {@link HFCAX509Certificate} that enrolled by {@link Enrollment}
     * with {@link HFCACertificateRequest} request filters.
     *
     * @return list of {@link HFCAX509Certificate} or empty list
     */
    public List<HFCAX509Certificate> getCertificates(HFCAClient caClient, Enrollment enrollment,
                                                     HFCACertificateRequest requestFilter) throws Exception {

        requireNonNull(caClient, "caClient");

        if (requestFilter == null) {
            requestFilter = caClient.newHFCACertificateRequest();
        }

        HFCACertificateResponse response = caClient.getHFCACertificates(
                FabricUserContext.newInstance(enrollment), requestFilter);

        Collection<HFCACredential> certs = response.getCerts();
        if (certs == null || certs.isEmpty()) {
            return Collections.emptyList();
        }

        List<HFCAX509Certificate> x509Certificates = new ArrayList<>(certs.size());
        for (HFCACredential cert : certs) {
            x509Certificates.add((HFCAX509Certificate) cert);
        }

        return x509Certificates;
    }

    /**
     * Return list of {@link HFCAX509Certificate} that enrolled by {@link Enrollment}
     * with {@link HFCACertificateRequest} request filters.
     *
     * @return list of {@link HFCAX509Certificate}
     */
    public List<HFCAX509Certificate> getCertificatesByCn(HFCAClient caClient, Enrollment enrollment, String cn)
            throws Exception {

        requireNonNull(caClient, "caClient");

        List<HFCAX509Certificate> certificates = getCertificates(caClient, enrollment, null);
        if (certificates.isEmpty()) {
            return Collections.emptyList();
        }

        List<HFCAX509Certificate> certs = new ArrayList<>();

        for (int i = 0; i < certificates.size(); i++) {
            HFCAX509Certificate certificate = certificates.get(i);
            String cnValue = FabricCertParser.getCnValue(certificate.getX509());

            if (cn.equals(cnValue)) {
                certs.add(certificate);
            }
        }

        return certs;
    }

    /**
     * Check success or not given {@link HFCAAffiliationResp}.
     *
     * @return true : 200 <= status code < 300, otherwise false
     */
    protected boolean is2xxSuccessful(HFCAAffiliationResp response) {
        if (response == null) {
            return false;
        }

        return response.getStatusCode() >= 200 && response.getStatusCode() < 300;
    }

    protected HFCAAffiliation searchAffiliationWithRecursively(HFCAAffiliation affiliation, String name)
            throws AffiliationException {
        if (name == null || name.length() == 0 || affiliation == null) {
            return null;
        }

        if (name.equals(affiliation.getName())) {
            return affiliation;
        }

        for (HFCAAffiliation child : affiliation.getChildren()) {
            HFCAAffiliation found = searchAffiliationWithRecursively(child, name);
            if (found != null) {
                return found;
            }
        }

        return null;
    }

    /**
     * Create a default {@link EnrollmentRequest} instance.
     */
    protected EnrollmentRequest createDefaultEnrollmentRequest() {
        EnrollmentRequest request = new EnrollmentRequest();
        return request;
    }
}
