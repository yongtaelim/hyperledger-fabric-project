package ibiz.example.hyperledger.sdk;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hyperledger.fabric.sdk.Channel.NOfEvents.createNofEvents;
import static org.hyperledger.fabric.sdk.Channel.PeerOptions.createPeerOptions;
import static org.hyperledger.fabric.sdk.Channel.TransactionOptions.createTransactionOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.io.IOUtils;
import org.app.client.CAClient;
import org.app.client.ChannelClient;
import org.app.client.FabricClient;
import org.app.user.UserContext;
import org.hyperledger.fabric.protos.peer.Query.ChaincodeInfo;
import org.hyperledger.fabric.sdk.BlockEvent;
import org.hyperledger.fabric.sdk.ChaincodeEndorsementPolicy;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.InstallProposalRequest;
import org.hyperledger.fabric.sdk.InstantiateProposalRequest;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.Peer.PeerRole;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.TransactionRequest;
import org.hyperledger.fabric.sdk.UpgradeProposalRequest;
import org.hyperledger.fabric.sdk.ChaincodeResponse.Status;
import org.hyperledger.fabric.sdk.exception.ChaincodeEndorsementPolicyParseException;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.hyperledger.fabric.sdk.identity.X509Enrollment;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.InvalidProtocolBufferException;

import ibiz.example.hyperledger.sdk.common.Util;
import ibiz.example.hyperledger.sdk.config.FabricConfig;

/**
 * 체인코드를 도커 컨테이너로 올리는 작업을 할 경우
 * 여러 피어 중 한개의 피어만 instantiate(upgrade)를 하고 orderer에 send한다.
 * 나머지 피어는 install만 진행 ( query나 invoke를 할 시 container가 올라간다 )
 * @author Park
 *
 */
public class FabricChaincode {
	
	private HFClient client;
	
	private Channel channel;
	
	private Logger logger = LoggerFactory.getLogger(FabricChaincode.class);
	
	public void setUp() throws Exception {
		logger.info("##############################################################");
		logger.info("#####################      fabric client set up     #####################");
		logger.info("##############################################################");
		FabricConfig fabricConfig = new FabricConfig(); 
//		String caUrl = fabricConfig.getCaOrg1Url();
//		String caName = fabricConfig.getCaAdmin();
//		String caPassword = fabricConfig.getCaAdminPassword();
		String orgMsp = fabricConfig.getOrg1Msp();
		String fabricName = fabricConfig.getFabricName();
		
//		CAClient caClient = new CAClient(caUrl, null);
		
		UserContext adminUserContext = new UserContext();
		adminUserContext.setName(fabricName);
		adminUserContext.setMspId(orgMsp);
//		caClient.setAdminUserContext(adminUserContext);
		adminUserContext.setEnrollment(getEnrollment());
		
		CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
		// setup the client
		client = HFClient.createNewInstance();
		client.setCryptoSuite(cryptoSuite);
		client.setUserContext(adminUserContext);
	}
	
	private Enrollment getEnrollment() throws UnsupportedEncodingException, FileNotFoundException, IOException, NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException {
		File privateKeyFile = Util.findFileSk((Paths.get("src/test/file", "msp/keystore")).toFile());
		File certificateFile = Paths.get("src/test/file", "msp/signcerts/Admin@org1.example.com-cert.pem").toFile();
		
		PrivateKey privateKey = Util.getPrivateKeyFromBytes(IOUtils.toByteArray(new FileInputStream(privateKeyFile)));
		 String certificate = new String(IOUtils.toByteArray(new FileInputStream(certificateFile)), "UTF-8");
		X509Enrollment enrollment = new X509Enrollment(privateKey, certificate);
		return enrollment;
	}
	
	public Collection<String> getChaincodeNames() throws InvalidArgumentException, TransactionException, IOException {
		FabricConfig fabricConfig = new FabricConfig();
		String channelName = fabricConfig.getChannelName();
		String peerName = fabricConfig.getOrg1Peer0();
		String peerUrl = fabricConfig.getOrg1Peer0Url();

//		String path = "src/test/file/INIT/"+channelName+".tx";
//		ChannelConfiguration channelConfiguration = new ChannelConfiguration(new File(path));
		
		Channel newChannel = client.newChannel(channelName);
		
		Peer peer = client.newPeer(peerName, peerUrl);
		
		Properties sdprops = new Properties();
		sdprops.put("org.hyperledger.fabric.sdk.discovery.default.protocol", "grpc:");
		
		newChannel.addPeer(peer, createPeerOptions().setPeerRoles(EnumSet.of(PeerRole.SERVICE_DISCOVERY, PeerRole.LEDGER_QUERY, PeerRole.EVENT_SOURCE, PeerRole.CHAINCODE_QUERY)));
		newChannel.setServiceDiscoveryProperties(sdprops);
		
		newChannel.initialize();
		
		return newChannel.getDiscoveredChaincodeNames();
	}
	
	public Channel getChannel() throws InvalidArgumentException, TransactionException {
		FabricConfig fabricConfig = new FabricConfig();
		String channelName = fabricConfig.getChannelName();
		String peer0Name = fabricConfig.getOrg1Peer0();
		String peer0Url = fabricConfig.getOrg1Peer0Url();
		String peer1Name = fabricConfig.getOrg1Peer1();
		String peer1Url = fabricConfig.getOrg1Peer1Url();
		String ordererName = fabricConfig.getOrdererName();
		String ordererUrl = fabricConfig.getOrdererUrl();

		int result = 1;
		
		channel = client.newChannel(channelName);
		
		Peer peer0 = client.newPeer(peer0Name, peer0Url);
		Peer peer1 = client.newPeer(peer1Name, peer1Url);
		channel.addPeer(peer0); 
		channel.addPeer(peer1); 
		
		Orderer orderer = client.newOrderer(ordererName, ordererUrl);
		channel.addOrderer(orderer);
				
		channel.initialize();
		
		return channel;
	}
	
	public int installChaincode() throws InvalidArgumentException, TransactionException, ProposalException {
		return installChaincode(client, channel);
	}
	
	public int installChaincode(HFClient client, Channel channel) throws InvalidArgumentException, TransactionException, ProposalException {
		FabricConfig fabricConfig = new FabricConfig();
		String chaincodeName = fabricConfig.getChaincodeName();
		String chaincodePath = fabricConfig.getChaincodePath();
		String chaincodeVersion = fabricConfig.getChaincodeVersion();
		TransactionRequest.Type chaincodeLang = fabricConfig.getChaincodeLang();
		
		ChaincodeID chaincodeID = ChaincodeID.newBuilder()
																	.setName(chaincodeName)
																	.setVersion(chaincodeVersion)
																	.setPath(chaincodePath)
																	.build();
		
		int result = 1;
		InstallProposalRequest installProposalRequest = client.newInstallProposalRequest();
		installProposalRequest.setChaincodeID(chaincodeID);
		installProposalRequest.setChaincodeSourceLocation(Paths.get("src/test/file", "chaincodes/go/").toFile());
		installProposalRequest.setChaincodeVersion(chaincodeVersion);
		installProposalRequest.setProposalWaitTime(120000);
		installProposalRequest.setChaincodeLanguage(chaincodeLang);
		
		Collection<ProposalResponse> sendInstallProposal = client.sendInstallProposal(installProposalRequest, channel.getPeers());
		
		for(ProposalResponse response : sendInstallProposal) {
			if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
				logger.info("Successful install proposal response Txid: {} from peer {}", response.getTransactionID(), response.getPeer().getName());
            } else {
                logger.info("Failed install proposal response Txid: {} from peer {}", response.getTransactionID(), response.getPeer().getName());
                result = 0;
            }
		}
		
		return result;
	}
	
	public int instantiateChaincode() throws InvalidArgumentException, TransactionException, ChaincodeEndorsementPolicyParseException, ProposalException, IOException  {
		return instantiateChaincode(client, channel);
	}
	
	public int instantiateChaincode(HFClient client, Channel channel) throws InvalidArgumentException, TransactionException, ChaincodeEndorsementPolicyParseException, IOException, ProposalException {
		FabricConfig fabricConfig = new FabricConfig();
		String chaincodeName = fabricConfig.getChaincodeName();
		String chaincodePath = fabricConfig.getChaincodePath();
		String chaincodeVersion = fabricConfig.getChaincodeVersion();
		TransactionRequest.Type chaincodeLang = fabricConfig.getChaincodeLang();
		
		int result = 1;
		
		Collection<ProposalResponse> successful = new LinkedList<>();
        Collection<ProposalResponse> failed = new LinkedList<>();
        
		ChaincodeID chaincodeID = ChaincodeID.newBuilder()
																	.setName(chaincodeName)
																	.setVersion(chaincodeVersion)
																	.setPath(chaincodePath)
																	.build();
		
		InstantiateProposalRequest instantiationProposalRequest = client.newInstantiationProposalRequest();
		instantiationProposalRequest.setProposalWaitTime(120000);
		instantiationProposalRequest.setChaincodeID(chaincodeID);
		instantiationProposalRequest.setChaincodeLanguage(chaincodeLang);
		instantiationProposalRequest.setFcn("init");
		instantiationProposalRequest.setArgs(new String[] {"a", "500", "b", "210"});
		
		Map<String, byte[]> tm = new HashMap<>();
        tm.put("HyperLedgerFabric", "InstantiateProposalRequest:JavaSDK".getBytes(UTF_8));
        tm.put("method", "InstantiateProposalRequest".getBytes(UTF_8));
        instantiationProposalRequest.setTransientMap(tm);
        
        ChaincodeEndorsementPolicy chaincodeEndorsementPolicy = new ChaincodeEndorsementPolicy();
        chaincodeEndorsementPolicy.fromYamlFile(new File("src/test/file" + "/chaincodes/policy/chaincodeendorsementpolicy.yaml"));
        instantiationProposalRequest.setChaincodeEndorsementPolicy(chaincodeEndorsementPolicy);
        
        Collection<ProposalResponse> responses = channel.sendInstantiationProposal(instantiationProposalRequest, channel.getPeers());
        
        for (ProposalResponse response : responses) {
            if (response.isVerified() && response.getStatus() == ProposalResponse.Status.SUCCESS) {
                logger.info("Succesful instantiate proposal response Txid: {} from peer {}", response.getTransactionID(), response.getPeer().getName());
                successful.add(response);
            } else {
            	logger.info("Failed instantiate proposal response Txid: {} from peer {}", response.getTransactionID(), response.getPeer().getName());
            	failed.add(response);
            }
        }
        
        Channel.NOfEvents nOfEvents = createNofEvents();
        if (!channel.getPeers(EnumSet.of(PeerRole.EVENT_SOURCE)).isEmpty()) {
            nOfEvents.addPeers(channel.getPeers(EnumSet.of(PeerRole.EVENT_SOURCE)));
        }
        
        if (!channel.getEventHubs().isEmpty()) {
            nOfEvents.addEventHubs(channel.getEventHubs());
        }

        channel.sendTransaction(successful, createTransactionOptions() //Basically the default options but shows it's usage.
                .userContext(client.getUserContext()) //could be a different user context. this is the default.
                .shuffleOrders(false) // don't shuffle any orderers the default is true.
                .orderers(channel.getOrderers()) // specify the orderers we want to try this transaction. Fails once all Orderers are tried.
                .nOfEvents(nOfEvents) // The events to signal the completion of the interest in the transaction
        ).thenApply(transactionEvent -> {
        	logger.info("isValid :: " + transactionEvent.isValid());
        	logger.info("signature :: " + transactionEvent.getSignature());
        	logger.info("transaction id :: " + transactionEvent.getTransactionID());
        	logger.info("peer ::: " + transactionEvent.getPeer());
        	logger.info("type" + transactionEvent.getType());
        	logger.info("action infos :: " + transactionEvent.getTransactionActionInfos().toString());
        	
        	BlockEvent blockEvent = transactionEvent.getBlockEvent();
        	blockEvent.getBlock();
        	try {
				blockEvent.getChannelId();
			} catch (InvalidProtocolBufferException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	return null;
        });
		return result;
	}
	
	public int checkInstalledChaincode() throws InvalidArgumentException, ProposalException {
		FabricConfig fabricConfig = new FabricConfig();
		Collection<Peer> peers = channel.getPeers();
		String chaincodeName = fabricConfig.getChaincodeName();
		String chaincodePath = fabricConfig.getChaincodePath();
		String chaincodeVersion = fabricConfig.getChaincodeVersion();
		
		int result = 0;
		for(Peer peer : peers) {
			List<ChaincodeInfo> chaincodeInfos = client.queryInstalledChaincodes(peer);
			
			for(ChaincodeInfo chaincodeInfo : chaincodeInfos) {
				String ccName = chaincodeInfo.getName();
				String ccPath = chaincodeInfo.getPath();
				String ccVersion = chaincodeInfo.getVersion();
				
				// name 만 체크
				if(chaincodeName.equals(ccName)) {
					result = 1;;
					break;
				}
			}
		}
		return result;
	}
	
	public int checkInstantiatedChaincode() throws InvalidArgumentException, ProposalException {
		FabricConfig fabricConfig = new FabricConfig();
		Collection<Peer> peers = channel.getPeers();
		String chaincodeName = fabricConfig.getChaincodeName();
		String chaincodePath = fabricConfig.getChaincodePath();
		String chaincodeVersion = fabricConfig.getChaincodeVersion();
		int result = 0;
		for(Peer peer : peers) {
			List<ChaincodeInfo> chaincodeInfos = channel.queryInstantiatedChaincodes(peer);
			
			for(ChaincodeInfo chaincodeInfo : chaincodeInfos) {
				String ccName = chaincodeInfo.getName();
				String ccPath = chaincodeInfo.getPath();
				String ccVersion = chaincodeInfo.getVersion();
				
				//name 만 체크
				if(chaincodeName.equals(ccName)) {
					result = 1;
					break;
				}
			}
		}
		
		
		return result;
	}
	
	public int upgradeChaincode() throws ChaincodeEndorsementPolicyParseException, InvalidArgumentException, ProposalException, IOException, InterruptedException, ExecutionException, TimeoutException {
		return upgradeChaincode(client, channel);
	}
	
	public int upgradeChaincode(HFClient client, Channel channel) throws ChaincodeEndorsementPolicyParseException, IOException, InvalidArgumentException, ProposalException, InterruptedException, ExecutionException, TimeoutException {
		FabricConfig fabricConfig = new FabricConfig();
		String chaincodeName = fabricConfig.getChaincodeName();
		String chaincodePath = fabricConfig.getChaincodePath();
		String chaincodeVersion = fabricConfig.getChaincodeVersion();
		
		int result = 1;
		Collection<ProposalResponse> successful = new LinkedList<>();
        Collection<ProposalResponse> failed = new LinkedList<>();
        
		ChaincodeID chaincodeID = ChaincodeID.newBuilder()
																	.setName(chaincodeName)
																	.setVersion(chaincodeVersion)
																	.setPath(chaincodePath)
																	.build();
		
		UpgradeProposalRequest upgradeProposalRequest = client.newUpgradeProposalRequest();
        upgradeProposalRequest.setChaincodeID(chaincodeID);
        upgradeProposalRequest.setProposalWaitTime(120000);
        upgradeProposalRequest.setFcn("init");
        upgradeProposalRequest.setArgs(new String[] {});    // no arguments don't change the ledger see chaincode.
        
        ChaincodeEndorsementPolicy chaincodeEndorsementPolicy = new ChaincodeEndorsementPolicy();
        chaincodeEndorsementPolicy.fromYamlFile(new File("src/test/file" + "/chaincodes/policy/chaincodeendorsementpolicy.yaml"));
        upgradeProposalRequest.setChaincodeEndorsementPolicy(chaincodeEndorsementPolicy);
        
        Map<String, byte[]> tmap = new HashMap<>();
        tmap.put("test", "data".getBytes());
        upgradeProposalRequest.setTransientMap(tmap);
        
        Collection<ProposalResponse> responses = channel.sendUpgradeProposal(upgradeProposalRequest);
        
        for (ProposalResponse response : responses) {
            if (response.getStatus() == Status.SUCCESS) {
                logger.info("Successful upgrade proposal response Txid: {} from peer {}", response.getTransactionID(), response.getPeer().getName());
                successful.add(response);
            } else {
            	logger.info("Failed upgrade proposal response Txid: {} from peer {}", response.getTransactionID(), response.getPeer().getName());
                failed.add(response);
                result = 0;
            }
        }
        
        channel.sendTransaction(successful).get(120000, TimeUnit.SECONDS);
        
		return result;
	}
	
	
	
	public byte[] getChannelSerialize() throws InvalidArgumentException, IOException {
		return channel.serializeChannel();
	}
	
	public Channel channelDeSerialize(HFClient client, byte[] channelBytes) throws ClassNotFoundException, InvalidArgumentException, IOException, TransactionException {
		Channel channel = client.deSerializeChannel(channelBytes);
		channel.initialize();
		
		return channel;
	}
	
	public HFClient getHFClient() throws UnsupportedEncodingException, FileNotFoundException, NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException, IOException, IllegalAccessException, InstantiationException, ClassNotFoundException, CryptoException, InvalidArgumentException, NoSuchMethodException, InvocationTargetException {
		FabricConfig fabricConfig = new FabricConfig(); 
//		String caUrl = fabricConfig.getCaOrg1Url();
//		String caName = fabricConfig.getCaAdmin();
//		String caPassword = fabricConfig.getCaAdminPassword();
		String orgMsp = fabricConfig.getOrg1Msp();
		String fabricName = fabricConfig.getFabricName();
		
//		CAClient caClient = new CAClient(caUrl, null);
		
		UserContext adminUserContext = new UserContext();
		adminUserContext.setName(fabricName);
		adminUserContext.setMspId(orgMsp);
//		caClient.setAdminUserContext(adminUserContext);
		adminUserContext.setEnrollment(getEnrollment());
		
		CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
		// setup the client
		HFClient client = HFClient.createNewInstance();
		client.setCryptoSuite(cryptoSuite);
		client.setUserContext(adminUserContext);
		return client;
	}
}
















