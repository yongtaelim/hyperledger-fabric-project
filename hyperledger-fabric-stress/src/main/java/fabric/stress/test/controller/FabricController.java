package fabric.stress.test.controller;

import java.util.UUID;

import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.TransactionRequest.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import fabric.stress.test.config.FabricConfig;
import fabric.stress.test.fabric.client.FabricChaincodeClient;
import fabric.stress.test.fabric.contruct.FabricContruct;

@RestController
public class FabricController {

	@Autowired
	private FabricConfig fabricConfig;
	
	private FabricChaincodeClient fabricChaincodeClient;
	
	private ChaincodeID chaincodeID;
	private Channel channel;
	private HFClient client;
	
	private String CHAINCODE_NAME = "fabcar";
	private String QUERY_FUNCTION = "queryCar";
	private String INVOKE_FUNCTION = "createCar";

	Logger loggger = LoggerFactory.getLogger(FabricController.class);
	
	@GetMapping(value="/query/{key}")
	public String query(@PathVariable("key") String key ) throws Exception {
		initialize();
		return fabricChaincodeClient.query(channel, client, QUERY_FUNCTION, chaincodeID, key);
	}
	
	@PostMapping(value="/invoke")
	public void invoke() throws Exception {
		initialize();
		String key = UUID.randomUUID().toString();
		loggger.info("key :: " + key);
		fabricChaincodeClient.invoke(channel, client, INVOKE_FUNCTION, chaincodeID, Type.GO_LANG, key, key+"1", key+"2", key+"3", key+"4");
	}
	
	private void initialize() {
		this.fabricChaincodeClient = new FabricChaincodeClient();
		
		FabricContruct fabricContruct = fabricConfig.getFabricContruct();
		
		this.channel = fabricContruct.getChannel();
		this.client = fabricContruct.getClient();
		
		this.chaincodeID = ChaincodeID.newBuilder().setName(CHAINCODE_NAME).build();
	}
}
