package fabric.stress.test.fabric.contruct;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.identity.X509Enrollment;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.springframework.util.ObjectUtils;

import fabric.stress.test.fabric.client.FabricChannelClient;
import fabric.stress.test.fabric.client.FabricClientFactory;
import fabric.stress.test.fabric.context.FabricCAContext;
import fabric.stress.test.fabric.context.FabricOrdererContext;
import fabric.stress.test.fabric.context.FabricPeerContext;
import fabric.stress.test.fabric.context.FabricUserContext;
import fabric.stress.test.fabric.context.FabricOrdererContext.FabricOrdererContextBuilder;
import fabric.stress.test.fabric.context.FabricPeerContext.FabricPeerContextBuilder;
import fabric.stress.test.fabric.parse.FabricCertParser;
import fabric.stress.test.util.FileUtil;

public class FabricContruct {

	private String channelName;
	public Channel channel;
	public HFClient client;

	private List<FabricOrdererContext> fabricOrdererContexts;
	private List<FabricPeerContext> fabricPeerContexts;
	private FabricCAContext fabricCAContext;
	private FabricUserContext fabricUserContext;

	public FabricContruct(List<FabricOrdererContext> fabricOrdererContexts, List<FabricPeerContext> fabricPeerContexts,
			FabricCAContext fabricCAContext, FabricUserContext fabricUserContext, String channelName) {

		this.fabricOrdererContexts = fabricOrdererContexts;
		this.fabricPeerContexts = fabricPeerContexts;
		this.fabricCAContext = fabricCAContext;
		this.fabricUserContext = fabricUserContext;
		this.channelName = channelName;
	}

	public FabricContruct(List<FabricOrdererContext> fabricOrdererContexts, List<FabricPeerContext> fabricPeerContexts,
			FabricUserContext fabricUserContext, String channelName) {
		this.fabricOrdererContexts = fabricOrdererContexts;
		this.fabricPeerContexts = fabricPeerContexts;
		this.fabricCAContext = null;
		this.fabricUserContext = fabricUserContext;
		this.channelName = channelName;
	}

	public static FabricContructBuilder builder() {
		return new FabricContructBuilder();
	}

	public void initialize() throws Exception {
		FabricChannelClient FabricChannelClient = new FabricChannelClient();
		getHFClient();
		channel = FabricChannelClient.buildChannel(client, channelName, fabricOrdererContexts, fabricPeerContexts);
		channel.initialize();
	}

	private void getHFClient() throws Exception {
		FabricClientFactory clientFactory = new FabricClientFactory();
		Enrollment enroll = null;

		if (ObjectUtils.isEmpty(fabricCAContext)) {
			File privateKeyFile = FileUtil.findFileSk(Paths.get("src/test",
					String.format(
							"crypto-config/peerOrganizations/%s.example.com/users/Admin@%s.example.com/msp/keystore",
							fabricUserContext.getAffiliation(), fabricUserContext.getAffiliation()))
					.toFile());
			File certificateFile = Paths.get("src/test", String.format(
					"crypto-config/peerOrganizations/%s.example.com/users/Admin@%s.example.com/msp/signcerts/Admin@%s.example.com-cert.pem",
					fabricUserContext.getAffiliation(), fabricUserContext.getAffiliation(),
					fabricUserContext.getAffiliation())).toFile();

			String certificate = new String(IOUtils.toByteArray(new FileInputStream(certificateFile)), "UTF-8");
			PrivateKey privateKey = getPrivateKeyFromBytes(IOUtils.toByteArray(new FileInputStream(privateKeyFile)));
			enroll = new X509Enrollment(privateKey, certificate);

		} else {
			HFCAClient caClient = clientFactory.createCaClient(fabricCAContext.getName(),
					fabricCAContext.getLocation());
			enroll = caClient.enroll(fabricCAContext.getUserName(), fabricCAContext.getUserPassword());
		}
		fabricUserContext.setEnrollment(enroll);

		client = clientFactory.createHFClient(fabricUserContext);
	}

	private PrivateKey getPrivateKeyFromBytes(byte[] data)
			throws IOException, NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException {
		final Reader pemReader = new StringReader(new String(data));

		final PrivateKeyInfo pemPair;
		try (PEMParser pemParser = new PEMParser(pemReader)) {
			pemPair = (PrivateKeyInfo) pemParser.readObject();
		}

		Security.addProvider(new BouncyCastleProvider());
		PrivateKey privateKey = new JcaPEMKeyConverter().setProvider(BouncyCastleProvider.PROVIDER_NAME)
				.getPrivateKey(pemPair);

		return privateKey;
	}

	public static class FabricContructBuilder {
		private List<FabricOrdererContext> fabricOrdererContexts;
		private List<FabricPeerContext> fabricPeerContexts;
		private FabricCAContext fabricCAContext;
		private FabricUserContext fabricUserContext;
		private String channelName;

		public FabricContruct build() {
			return new FabricContruct(fabricOrdererContexts, fabricPeerContexts, fabricCAContext, fabricUserContext,
					channelName);
		}

		public FabricContruct.FabricContructBuilder fabricOrdererContexts(
				List<FabricOrdererContext> fabricOrdererContexts) {
			this.fabricOrdererContexts = fabricOrdererContexts;
			return this;
		}

		public FabricContruct.FabricContructBuilder fabricPeerContexts(List<FabricPeerContext> fabricPeerContexts) {
			this.fabricPeerContexts = fabricPeerContexts;
			return this;
		}

		public FabricContruct.FabricContructBuilder fabricUserContext(FabricUserContext fabricUserContext) {
			this.fabricUserContext = fabricUserContext;
			return this;
		}

		public FabricContruct.FabricContructBuilder fabricCAContext(FabricCAContext fabricCAContext) {
			this.fabricCAContext = fabricCAContext;
			return this;
		}

		public FabricContruct.FabricContructBuilder channelName(String channelName) {
			this.channelName = channelName;
			return this;
		}

	}

	public Channel getChannel() {
		return channel;
	}

	public HFClient getClient() {
		return client;
	}

}
