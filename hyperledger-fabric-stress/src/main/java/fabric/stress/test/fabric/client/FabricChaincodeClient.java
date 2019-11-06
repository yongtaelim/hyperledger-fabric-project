package fabric.stress.test.fabric.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.QueryByChaincodeRequest;
import org.hyperledger.fabric.sdk.SDKUtils;
import org.hyperledger.fabric.sdk.TransactionProposalRequest;
import org.hyperledger.fabric.sdk.TransactionRequest.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FabricChaincodeClient {
	
	private int TRANSACTION_WAIT_TIME = 20000;
	private Logger logger = LoggerFactory.getLogger(FabricChaincodeClient.class);
	
	public String query(Channel channel, HFClient client, String fcn, ChaincodeID chaincodeID, String...args) throws Exception {
		String payload = "";
		
		QueryByChaincodeRequest queryByChaincodeRequest = client.newQueryProposalRequest();
		queryByChaincodeRequest.setArgs(args);
		queryByChaincodeRequest.setFcn(fcn);
		queryByChaincodeRequest.setChaincodeID(chaincodeID);
		
		Collection<ProposalResponse> queryByChaincode = channel.queryByChaincode(queryByChaincodeRequest);
		for (ProposalResponse proposalResponse : queryByChaincode) {
			if (!proposalResponse.isVerified() || proposalResponse.getStatus() != ProposalResponse.Status.SUCCESS) {
				logger.error("failed to query....");
				logger.error("function :: {}, args :: {}, verified :: {}, status :: {}", fcn, args, proposalResponse.isVerified(), proposalResponse.getStatus());
            } else {
                payload = proposalResponse.getProposalResponse().getResponse().getPayload().toStringUtf8();
                logger.info("Query payload of b from peer {} returned {}", proposalResponse.getPeer().getName(), payload);
            }
		}
		return payload;
	}
	
	public boolean invoke(Channel channel, HFClient client, String fcn, ChaincodeID chaincodeID, Type chaincodeLanguage, String...args) throws Exception {
		List<ProposalResponse> successful = new ArrayList<ProposalResponse>();
		List<ProposalResponse> failed = new ArrayList<ProposalResponse>();
		
		TransactionProposalRequest transactionProposalRequest = client.newTransactionProposalRequest();
		transactionProposalRequest.setChaincodeID(chaincodeID);
        transactionProposalRequest.setChaincodeLanguage(chaincodeLanguage);
        transactionProposalRequest.setFcn(fcn);
        transactionProposalRequest.setProposalWaitTime(TRANSACTION_WAIT_TIME);
        transactionProposalRequest.setArgs(args);

        Collection<ProposalResponse> transactionPropResp = channel.sendTransactionProposal(transactionProposalRequest, channel.getPeers());
        for (ProposalResponse response : transactionPropResp) {
            if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
                logger.info("Successful transaction proposal response Txid: {} from peer {}", response.getTransactionID(), response.getPeer().getName());
                successful.add(response);
            } else {
                failed.add(response);
            }
        }

        logger.warn("Received {} transaction proposal responses. Successful+verified: {} . Failed: {}",
                transactionPropResp.size(), successful.size(), failed.size());
        if (failed.size() > 0) {
            ProposalResponse firstTransactionProposalResponse = failed.iterator().next();
            logger.error("Not enough endorsers for invoke:" + failed.size() + " endorser error: " +
                    firstTransactionProposalResponse.getMessage() +
                    ". Was verified: " + firstTransactionProposalResponse.isVerified());
            return false;
        }

        // Send Transaction Transaction to orderer
        logger.info("Sending chaincode transaction to orderer.");
        channel.sendTransaction(successful).get(TRANSACTION_WAIT_TIME, TimeUnit.SECONDS);
        return true;
	}
}
