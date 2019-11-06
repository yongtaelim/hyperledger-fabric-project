package ibiz.example.hyperledger.sdk;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.exception.ChaincodeEndorsementPolicyParseException;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class FabricChaincodeTest {
	
	private static FabricChaincode chaincode;
	
	@BeforeClass
	public static void setUpClass() throws Exception {
		chaincode = new FabricChaincode();
	}
	
	@Before
	public void setUpTest() throws Exception {
		chaincode.setUp();
	}
	
	@Test
	@Ignore
	public void getChannelTest() throws InvalidArgumentException, TransactionException {
		chaincode.getChannel();
	}

	@Test
	@Ignore
	public void installChaincodeTest() throws InvalidArgumentException, TransactionException, ProposalException {
		int installChaincode = chaincode.installChaincode();
		assertEquals(installChaincode, 1);
	}
	
	@Test
	@Ignore
	public void instantiateChaincodeTest() throws InvalidArgumentException, TransactionException, ChaincodeEndorsementPolicyParseException, ProposalException, IOException  {
		int instantiateChaincode = chaincode.instantiateChaincode();
		assertEquals(instantiateChaincode, 1);
	}
	
	@Test
	@Ignore
	public void installAndInstantiateChaincodeTest() throws InvalidArgumentException, TransactionException, ProposalException, ChaincodeEndorsementPolicyParseException, IOException {
		chaincode.getChannel();
		int installChaincode = chaincode.installChaincode();
		assertEquals(installChaincode, 1);
		int instantiateChaincode = chaincode.instantiateChaincode();
		assertEquals(instantiateChaincode, 1);
	}
	
	@Test
	@Ignore
	public void checkInstalledChaincodeTest() throws InvalidArgumentException, ProposalException, TransactionException {
		chaincode.getChannel();
		int checkInstalledChaincode = chaincode.checkInstalledChaincode();
		assertEquals(checkInstalledChaincode, 1);
	}
	
	@Test
	@Ignore
	public void checkInstantiatedChaincodeTest() throws InvalidArgumentException, ProposalException, TransactionException {
		chaincode.getChannel();
		int checkInstantiatedChaincode = chaincode.checkInstantiatedChaincode();
		assertEquals(checkInstantiatedChaincode, 1);
	}
	
	@Test
	@Ignore
	public void upgrageChaincodeTest() throws ChaincodeEndorsementPolicyParseException, InvalidArgumentException, ProposalException, IOException, InterruptedException, ExecutionException, TimeoutException, TransactionException {
		chaincode.getChannel();
		int installChaincode = chaincode.installChaincode();
		assertEquals(installChaincode, 1);
		int upgradeChaincode = chaincode.upgradeChaincode();
		assertEquals(upgradeChaincode, 1);
	}
	
	/**
	 * 채널에 속한 피어 기준 체인코드를 얻어온다.
	 * @throws InvalidArgumentException
	 * @throws TransactionException
	 * @throws IOException 
	 */
	@Test
	@Ignore
	public void getChaincodeNamesTest() throws InvalidArgumentException, TransactionException, IOException {
		Collection<String> chaincodeNames = chaincode.getChaincodeNames();
		System.out.println(chaincodeNames);
	}
	
	@Test
	public void ChannelSerializeTest() throws InvalidArgumentException, IOException, ClassNotFoundException, TransactionException, NoSuchProviderException, NoSuchAlgorithmException, InvalidKeySpecException, IllegalAccessException, InstantiationException, CryptoException, NoSuchMethodException, InvocationTargetException, ProposalException, ChaincodeEndorsementPolicyParseException, InterruptedException, ExecutionException, TimeoutException {
		Channel channel1 = chaincode.getChannel();
		byte[] channelBytes = chaincode.getChannelSerialize();
		HFClient hfClient = chaincode.getHFClient();
		Channel channel2 = chaincode.channelDeSerialize(hfClient, channelBytes);
//		int installChaincode = chaincode.installChaincode(hfClient, channel2);
//		assertEquals(installChaincode, 1);
		int upgradeChaincode = chaincode.instantiateChaincode(hfClient, channel2);
		assertEquals(upgradeChaincode, 1);
	}
}
