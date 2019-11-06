package fabric.stress.test.config;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import fabric.stress.test.config.PropertiesConfig.Orderer;
import fabric.stress.test.config.PropertiesConfig.Peer;
import fabric.stress.test.fabric.context.FabricOrdererContext;
import fabric.stress.test.fabric.context.FabricOrdererContext.FabricOrdererContextBuilder;
import fabric.stress.test.fabric.context.FabricPeerContext;
import fabric.stress.test.fabric.context.FabricPeerContext.FabricPeerContextBuilder;
import fabric.stress.test.fabric.context.FabricUserContext;
import fabric.stress.test.fabric.context.FabricUserContext.Builder;
import fabric.stress.test.fabric.contruct.FabricContruct;

@Component
public class FabricConfig {

	@Autowired
	private PropertiesConfig properties;
	
	private FabricContruct fabricContruct;
	
	@PostConstruct
	public void initialize() throws Exception {
		this.fabricContruct = new FabricContruct(getFabricOrdererContexts(), getFabricPeerContexts(), getFabricUserContext(), getChannelName());
		fabricContruct.initialize();
	}
	
	public FabricContruct getFabricContruct() {
		return this.fabricContruct;
	}
	
	private List<FabricOrdererContext> getFabricOrdererContexts() {
		List<FabricOrdererContext> ordererContexts = new ArrayList<FabricOrdererContext>();
		
		for (Orderer orderer : properties.getOrderers()) {
			FabricOrdererContextBuilder fabricOrdererContextBuilder = FabricOrdererContext.builder();
			fabricOrdererContextBuilder.name(orderer.getName());
			fabricOrdererContextBuilder.location(orderer.getLocation());
			fabricOrdererContextBuilder.properties(getProperties("orderer", orderer.getName()));
			ordererContexts.add(fabricOrdererContextBuilder.build());
		}
		
		return ordererContexts;
	}
	
	private List<FabricPeerContext> getFabricPeerContexts() {
		List<FabricPeerContext> peerContexts = new ArrayList<FabricPeerContext>();
		for(Peer peer : properties.getPeers()) {
			FabricPeerContextBuilder fabricPeer1ContextBuilder = FabricPeerContext.builder();
			fabricPeer1ContextBuilder.name(peer.getName());
			fabricPeer1ContextBuilder.location(peer.getLocation());
			fabricPeer1ContextBuilder.properties(getProperties("peer", peer.getName()));
			peerContexts.add(fabricPeer1ContextBuilder.build());
		}
		
		return peerContexts;
	}
	
	private FabricUserContext getFabricUserContext() {
		Builder userContextBuilder = FabricUserContext.builder();
		userContextBuilder.mspId(properties.getMsp());
		userContextBuilder.name(properties.getUserName());
		userContextBuilder.affiliation(properties.getAffiliation());
		userContextBuilder.isAdmin(true);
		return userContextBuilder.build();
	}
	
	private String getChannelName() {
		return properties.getChannel();
	}
	
	private Properties getProperties(final String type, final String name) {
        Properties ret = new Properties();

        final String domainName = getDomainName(name);

        String testPath = "src/test";
        
        File cert = Paths.get(testPath, "crypto-config/ordererOrganizations".replace("orderer", type), domainName, type + "s",
                name, "tls/server.crt").toFile();
        if (!cert.exists()) {
            throw new RuntimeException(String.format("Missing cert file for: %s. Could not find at location: %s", name,
                    cert.getAbsolutePath()));
        }

//        if (!isRunningAgainstFabric10()) {
        File clientCert;
        File clientKey;
        if ("orderer".equals(type)) {
            clientCert = Paths.get(testPath, "crypto-config/ordererOrganizations/example.com/users/Admin@example.com/tls/client.crt").toFile();

            clientKey = Paths.get(testPath, "crypto-config/ordererOrganizations/example.com/users/Admin@example.com/tls/client.key").toFile();
        } else {
            clientCert = Paths.get(testPath, "crypto-config/peerOrganizations/", domainName, "users/User1@" + domainName, "tls/client.crt").toFile();
            clientKey = Paths.get(testPath, "crypto-config/peerOrganizations/", domainName, "users/User1@" + domainName, "tls/client.key").toFile();
        }

        if (!clientCert.exists()) {
            throw new RuntimeException(String.format("Missing  client cert file for: %s. Could not find at location: %s", name,
                    clientCert.getAbsolutePath()));
        }

        if (!clientKey.exists()) {
            throw new RuntimeException(String.format("Missing  client key file for: %s. Could not find at location: %s", name,
                    clientKey.getAbsolutePath()));
        }
        ret.setProperty("clientCertFile", clientCert.getAbsolutePath());
        ret.setProperty("clientKeyFile", clientKey.getAbsolutePath());
//        }

        ret.setProperty("pemFile", cert.getAbsolutePath());

        ret.setProperty("hostnameOverride", name);
        ret.setProperty("sslProvider", "openSSL");
        ret.setProperty("negotiationType", "TLS");

        return ret;
    }
	
	private String getDomainName(final String name) {
        int dot = name.indexOf(".");
        if (-1 == dot) {
            return null;
        } else {
            return name.substring(dot + 1);
        }

    }
}
