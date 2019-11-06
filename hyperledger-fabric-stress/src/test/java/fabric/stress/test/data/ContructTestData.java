package fabric.stress.test.data;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import fabric.stress.test.fabric.context.FabricOrdererContext;
import fabric.stress.test.fabric.context.FabricPeerContext;
import fabric.stress.test.fabric.context.FabricUserContext;
import fabric.stress.test.fabric.context.FabricOrdererContext.FabricOrdererContextBuilder;
import fabric.stress.test.fabric.context.FabricPeerContext.FabricPeerContextBuilder;
import fabric.stress.test.fabric.context.FabricUserContext.Builder;

public class ContructTestData {
	
	String ordererName = "orderer.example.com";
	String ordererLocation = "grpcs://192.168.56.1:7050";
	
	String peer1Name = "peer0.org1.example.com";
	String peer1Location = "grpcs://192.168.56.1:7051";
	String peer2Name = "peer1.org1.example.com";
	String peer2Location = "grpcs://192.168.56.1:8051";
	
	String orgMsp = "Org1MSP";
	String affiliation = "org1";
	String userName = "admin";
	
	private String channelName = "mychannel";
	
	public List<FabricOrdererContext> getFabricOrdererContexts() {
		FabricOrdererContextBuilder fabricOrdererContextBuilder = FabricOrdererContext.builder();
		fabricOrdererContextBuilder.name(ordererName);
		fabricOrdererContextBuilder.location(ordererLocation);
		fabricOrdererContextBuilder.properties(getProperties("orderer", ordererName));
		FabricOrdererContext ordererContext = fabricOrdererContextBuilder.build();
		return Arrays.asList(ordererContext);
	}
	
	public List<FabricPeerContext> getFabricPeerContexts() {
		FabricPeerContextBuilder fabricPeer1ContextBuilder = FabricPeerContext.builder();
		fabricPeer1ContextBuilder.name(peer1Name);
		fabricPeer1ContextBuilder.location(peer1Location);
		fabricPeer1ContextBuilder.properties(getProperties("peer", peer1Name));
		FabricPeerContext peer1Context = fabricPeer1ContextBuilder.build();
		
//		FabricPeerContextBuilder fabricPeer2ContextBuilder = FabricPeerContext.builder();
//		fabricPeer2ContextBuilder.name(peer1Name);
//		fabricPeer2ContextBuilder.location(peer1Location);
//		fabricPeer2ContextBuilder.properties(getProperties("peer", peer2Name));
//		FabricPeerContext peer2Context = fabricPeer2ContextBuilder.build();
		
//		return Arrays.asList(peer1Context, peer2Context);
		return Arrays.asList(peer1Context);
	}
	
	public FabricUserContext getFabricUserContext() {
		Builder userContextBuilder = FabricUserContext.builder();
		userContextBuilder.mspId(orgMsp);
		userContextBuilder.name(userName);
		userContextBuilder.affiliation(affiliation);
		userContextBuilder.isAdmin(true);
		return userContextBuilder.build();
	}
	
	public String getChannelName() {
		return channelName;
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
