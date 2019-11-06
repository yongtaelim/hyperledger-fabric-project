package fabric.stress.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.TransactionRequest.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import fabric.stress.test.data.ChaincodeTestData;
import fabric.stress.test.data.ContructTestData;
import fabric.stress.test.fabric.client.FabricChaincodeClient;
import fabric.stress.test.fabric.contruct.FabricContruct;

public class FabricContructTest {

	private FabricContruct fabricContruct;

	private ChaincodeID chaincodeID;
	
	private FabricChaincodeClient chaincodeClient;
	
	@BeforeEach
	public void setUp() throws Exception {
		fabricContruct();
		chaincodeBuild();
	}
	
	private void fabricContruct() throws Exception {
		ContructTestData data = new ContructTestData();
		this.chaincodeClient = new FabricChaincodeClient();
		
		this.fabricContruct = new FabricContruct(data.getFabricOrdererContexts(), data.getFabricPeerContexts(), data.getFabricUserContext(), data.getChannelName());
		fabricContruct.initialize();
	}
	
	private void chaincodeBuild() {
		ChaincodeTestData data = new ChaincodeTestData();
		this.chaincodeID = ChaincodeID.newBuilder().setName(data.getName()).build();
	}
	
	@Test
//	@Disabled
	void query() throws Exception {
		String query = chaincodeClient.query(fabricContruct.getChannel(), fabricContruct.getClient(), "queryCar", chaincodeID, "carB");
		System.out.println(query);
	}
	
//	@Test
	void invoke() throws Exception {
		boolean invoke = chaincodeClient.invoke(fabricContruct.getChannel(), fabricContruct.getClient(), "createCar", chaincodeID, Type.GO_LANG, "carB","a1","a2","a3","a4");
		assertThat(invoke).isTrue();
	}
}
