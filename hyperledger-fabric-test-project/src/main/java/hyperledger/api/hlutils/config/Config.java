package hyperledger.api.hlutils.config;

import java.io.File;

public class Config {
	public static final String ORG1_MSP = "Org1MSP";
	public static final String ORG1 = "org1";

	public static final String ADMIN = "admin";
	public static final String ADMIN_PASSWORD = "adminpw";
	
	public static final String CHANNEL_CONFIG_PATH = "config/channel.tx";
	
	public static final String ORG1_USR_BASE_PATH = "crypto-config" + File.separator + "peerOrganizations" + File.separator
			+ "org1.example.com" + File.separator + "users" + File.separator + "Admin@org1.example.com"
			+ File.separator + "msp";
	
	public static final String ORG1_USR_ADMIN_PK = ORG1_USR_BASE_PATH + File.separator + "keystore";
	public static final String ORG1_USR_ADMIN_CERT = ORG1_USR_BASE_PATH + File.separator + "admincerts";
	
	public static final String ORDERER_NAME = "orderer.example.com";
	
	public static final String CHANNEL_NAME_CH1 = "mychannel";
	public static final String CHANNEL_NAME_CH2 = "ch2";
	
	public static final String ORG1_PEER_1 = "peer1.hyundaicard.poc";
	public static final String ORG1_PEER_2 = "peer2.hyundaicard.poc";
	public static final String ORG1_PEER_3 = "peer3.hyundaicard.poc";
	public static final String ORG1_PEER_4 = "peer4.hyundaicard.poc";
	public static final String ORG1_PEER_5 = "peer5.hyundaicard.poc";
	
	public static final String CHAINCODE_AFTLSTOR = "HSC-MCT-MNG-01-001";
	public static final String FUNC_AFTLSTOR_SELECT = "getAfltstorIfo";
	public static final String FUNC_AFTLSTOR_SELECT_QUERY = "getAfltstorIfoList";
	public static final String FUNC_AFTLSTOR_CREATE = "crtAfltstorIfo";
	
	public static final String CHAINCODE_FDSCLCT = "HSC-PMT-BIZ-01-001";
	public static final String FUNC_FDSCLCT_CHECK = "chkFdsClctIfo";
	public static final String FUNC_FDSCLCT_CREATE = "crtFdsClctIfo";
	
	public static final String CHAINCODE_SETLREQ = "HSC-PMT-BIZ-02-001";
	public static final String FUNC_SETLREQ_SELECT = "getSetlReqIfo";
	public static final String FUNC_SETLREQ_CREATE = "crtSetlReqIfo";
	
	public static final String CHAINCODE_SETLMEANCERT = "HSC-PMT-BIZ-03-001";
	public static final String FUNC_SETLMEANCERT_SELECT = "getSetlMeanCertIfo";
	public static final String FUNC_SETLMEANCERT_CREATE = "crtSetlMeanCertIfo";

	public static final String CHAINCODE_APV = "HSC-PMT-BIZ-04-001";
	public static final String FUNC_APV_SELECT = "getApvIfo";
	public static final String FUNC_APV_CHECK = "chkApvIfo";
	public static final String FUNC_APV_CREATE = "crtApvIfo";
	public static final String FUNC_CANAPV_CREATE = "crtCanApvIfo";
	public static final String FUNC_APV_UPDATE = "udtApvIfo";
	
	public static final String MSG_SIZE_FUNC = "grpc.NettyChannelBuilderOption.maxInboundMessageSize";
	public static final int MSG_SIZE = 100 * 1024 * 1024;

}
