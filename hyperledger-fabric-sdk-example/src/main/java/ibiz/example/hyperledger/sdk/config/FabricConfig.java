package ibiz.example.hyperledger.sdk.config;

import org.hyperledger.fabric.sdk.TransactionRequest;

import lombok.Getter;

@Getter
public class FabricConfig {
	public final String caOrg1Url = "http://192.168.30.18:7054";
	public final String org1Msp = "Org1MSP";
	public final String caAdminPeer = "peerOrg1Admin";
	public final String fabricName = "test";
	public final String caAdmin = "admin";
	public final String caAdminPassword = "adminpw";
	
	public final String ordererUrl = "grpc://192.168.30.18:7050";
	public final String ordererName = "orderer.example.com";
	
	public final String channelName = "foo";
	
	public final String org1Peer0 = "peer0.org1.example.com";
	public final String org1Peer0Url = "grpc://192.168.30.18:7051";
	public final String org1Peer1 = "peer1.org1.example.com";
	public final String org1Peer1Url = "grpc://192.168.30.18:7056";
	
//	public final String caOrg1Url = "http://192.168.30.10:7054";
//	public final String org1 = "prOrg";
//	public final String org1Msp = "prOrg";
//	public final String caAdmin = "aasdf@RootCA";
//	public final String caAdminPassword = "asdf1234";
//	
//	public final String ordererUrl = "grpc://192.168.30.11:7050";
//	public final String ordererName = "orD.ordOrg.network.com";
//	
//	public final String channelName = "channel1";
//	
//	public final String org1Peer0 = "prD0.prOrg.network.com";
//	public final String org1Peer0Url = "grpc://192.168.30.12:7051";
//	public final String org1Peer1 = "prD1.prOrg.network.com";
//	public final String org1Peer1Url = "grpc://192.168.30.13:7051";
	
	// chaincode
	public final String chaincodeName = "private_data_cc_go";
	public final String chaincodePath = "github.com/private_data_cc";
	public final String chaincodeVersion = "1.2";
	public final TransactionRequest.Type chaincodeLang = TransactionRequest.Type.GO_LANG;
}
