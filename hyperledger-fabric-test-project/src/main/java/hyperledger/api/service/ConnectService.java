package hyperledger.api.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

import javax.annotation.PostConstruct;

import org.hyperledger.fabric.sdk.BlockEvent.TransactionEvent;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.QueryByChaincodeRequest;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.json.simple.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import hyperledger.api.hlutils.client.CAClient;
import hyperledger.api.hlutils.client.ChannelClient;
import hyperledger.api.hlutils.client.FabricClient;
import hyperledger.api.hlutils.config.Config;
import hyperledger.api.hlutils.user.UserContext;
import hyperledger.api.utils.CommonUtil;
import hyperledger.api.utils.PropertiesUtil;


@Service
public class ConnectService {
	
private static final Logger logger = LoggerFactory.getLogger(ConnectService.class);
	
	public ChannelClient channelClient = null;
	public FabricClient fabClient = null;
	public Peer peer = null;
	
	@PostConstruct
	public void setDefault() throws Exception {
		fabClient = getFabricClient();
		channelClient = setChannelClient(PropertiesUtil.getString("CHANNEL_NAME"));
	}
	
	private FabricClient getFabricClient() throws Exception {
		CAClient caClient = new CAClient(PropertiesUtil.getString("CA_ORG1_URL"), null);
		
		UserContext userContext = new UserContext();
		userContext.setName(PropertiesUtil.getString("ADMIN"));
		userContext.setAffiliation(PropertiesUtil.getString("ORG1"));
		userContext.setMspId(PropertiesUtil.getString("ORG1_MSP"));
		caClient.setAdminUserContext(userContext);
		userContext = caClient.enrollAdminUser(PropertiesUtil.getString("ADMIN"), PropertiesUtil.getString("ADMIN_PASSWORD"));
		
		fabClient = new FabricClient(userContext);
		return fabClient;
	}

	private ChannelClient setChannelClient(String string) throws Exception {
		
		Properties properties = new Properties();
		properties.put(PropertiesUtil.getString("MSG_SIZE_FUNC"), PropertiesUtil.getString("MSG_SIZE"));
		
		logger.info("properties ::: "+properties.toString());
		
		channelClient = fabClient.createChannelClient(PropertiesUtil.getString("CHANNEL_NAME_CH1"));
		Channel channel = channelClient.getChannel();
		
		//peer = fabClient.getInstance().newPeer(PropertiesUtil.getString("ORG1_PEER_1_NAME"), PropertiesUtil.getString("ORG1_PEER_1"), properties);
		peer = fabClient.getInstance().newPeer(PropertiesUtil.getString("ORG1_PEER_1_NAME"), PropertiesUtil.getString("ORG1_PEER_1"));
		channel.addPeer(peer);
		
		Orderer orderer = fabClient.getInstance().newOrderer(PropertiesUtil.getString("ORDERER_NAME"), PropertiesUtil.getString("ORDERER_URL"));
		channel.addOrderer(orderer);
		channel.initialize();
		
		return channelClient;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> query(Map<String, Object> params) throws Exception {
		Map<String, Object> resultMap = new HashMap();
		
		// 시작 시간
        long startTime = System.nanoTime();

		logger.info("query args ::: " +params.get("args"));
		
		String stringResponse	= ""; 
		String chaincode		= (String) params.get("chaincode");
		String funcName			= (String) params.get("funcName");
		String[] arguments		= {(String)params.get("args")};
		String channelName		= (String) params.get("channel");
		
		Channel channel = null;
		if(Config.CHANNEL_NAME_CH1.equals(channelName)){		//ch1
			channel = channelClient.getChannel();
		} 
		
		QueryByChaincodeRequest request = fabClient.getInstance().newQueryProposalRequest();
		ChaincodeID ccid = ChaincodeID.newBuilder().setName(chaincode).build();
		request.setChaincodeID(ccid);
		request.setFcn(funcName);
		if(arguments != null) request.setArgs(arguments);
		
		String status = "";
		String message = "";
		
		Collection<ProposalResponse> responseQuery = channel.queryByChaincode(request);
		for(ProposalResponse pres : responseQuery) {
			status = pres.getStatus().toString();
			message = pres.getMessage();
			
			if(pres.getChaincodeActionResponseStatus() == 500) {
				resultMap.put("rsltCd", "99");
				resultMap.put("rsltMsg", "Fail");
				resultMap.put("runTime", "00");
				return resultMap;
			}
			
			JSONArray jsonArray = new JSONArray();
			stringResponse = new String(pres.getChaincodeActionResponsePayload());
			if(stringResponse != null && !"SUCCESS".equals(stringResponse.toUpperCase()) && !"NO DATA.".equals(stringResponse.toUpperCase())) {
				jsonArray = CommonUtil.jsonStringToObject(stringResponse);
			}
			
			if("SUCCESS".equals(status.toUpperCase())) {
				if(jsonArray.size() > 1){
					resultMap.put("resultData", jsonArray);
					resultMap.put("rsltCd", "00");
					resultMap.put("rsltMsg", status);
				} else if(jsonArray.size() == 1){
					Map<String, Object> resultDataMap = (Map<String, Object>) jsonArray.get(0);
					resultMap.putAll(resultDataMap);
					resultMap.put("rsltCd", "00");
					resultMap.put("rsltMsg", status);
				} else if(jsonArray.size() < 1){
					resultMap.put("resultData", stringResponse);
					resultMap.put("rsltCd", "00");
					resultMap.put("rsltMsg", status);
				}
			} else {
				resultMap.put("rsltCd", "99");
				resultMap.put("rsltMsg", "Fail");
				resultMap.put("runTime", "00");
			}
		}
		
		// 종료 시간
		long endTime = System.nanoTime();
        double runTime = (( endTime - startTime )/1000000.0);
        resultMap.put("runTime", runTime);
//		logger.info("-- query result : " + resultMap);
		return resultMap;
	}
	
	public Map<String, Object> invoke(Map<String, Object> params) throws Exception {
		Map<String, Object> resultMap = new HashMap();
		
		// 시작 시간
        long startTime = System.nanoTime();
        
		String channelName = (String) params.get("channel");
		TransactionProposalRequest request = fabClient.getInstance().newTransactionProposalRequest();
		ChaincodeID ccid = ChaincodeID.newBuilder().setName((String) params.get("chaincode")).build();
		request.setChaincodeID(ccid);
		request.setFcn((String) params.get("funcName"));
		request.setArgs((String) params.get("args"));
		
		request.setProposalWaitTime(20000);
		
		Channel channel = null;
		if(Config.CHANNEL_NAME_CH1.equals(channelName)) {
			channel = channelClient.getChannel();
		}
		
		String status = "";
		String message = "";
		String txHash = "";
		
		Collection<ProposalResponse> response = channel.sendTransactionProposal(request, channel.getPeers());
		for(ProposalResponse pres: response){
			status = pres.getStatus().toString();
			txHash = pres.getTransactionID();
			message = pres.getMessage();
		}
		
		if("SUCCESS".equals(status.toUpperCase())) {
			CompletableFuture<TransactionEvent> cf = channel.sendTransaction(response);
			//resultMap.put("rsltCd", message);
			resultMap.put("rsltCd", "00");
			resultMap.put("rsltMsg", status);
			resultMap.put("txHash", txHash);
		} else {
			resultMap.put("rsltCd", "99");
			resultMap.put("rsltMsg", "Fail");
			resultMap.put("runTime", "00.00");
		}

		// 종료 시간
        long endTime = System.nanoTime();
        double runTime = (( endTime - startTime )/1000000.0);
        resultMap.put("runTime", runTime);
//		logger.info("-- query result : " + resultMap);
		
		return resultMap;
	}
}
