package hyperledger.api.hlutils.client;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;

import hyperledger.api.hlutils.user.UserContext;
import hyperledger.api.hlutils.util.Util;

public class CAClient {
	String caUrl;
	Properties caProperties;
	
	HFCAClient instance;
	UserContext adminContext;
	
	public UserContext getAdminUserContext(){
		return adminContext;
	}
	
	public void setAdminUserContext(UserContext userContext){
		this.adminContext = userContext;
	}
	
	public CAClient(String caUrl, Properties caProperties) throws MalformedURLException, IllegalAccessException, InstantiationException, ClassNotFoundException, CryptoException, InvalidArgumentException, NoSuchMethodException, InvocationTargetException {
		this.caUrl = caUrl;
		this.caProperties = caProperties;
		init();
	}

	public void init() throws MalformedURLException, IllegalAccessException, InstantiationException, ClassNotFoundException, CryptoException, InvalidArgumentException, NoSuchMethodException, InvocationTargetException {
		CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
		instance = HFCAClient.createNewInstance(caUrl, caProperties);
		instance.setCryptoSuite(cryptoSuite);
	}

	public HFCAClient getInstance() {
		return instance;
	}
	
	public UserContext enrollAdminUser(String username, String password) throws Exception {
		UserContext userContext = Util.readUserContext(adminContext.getAffiliation(), username);
		if (userContext != null) {
			Logger.getLogger(CAClient.class.getName()).log(Level.WARNING, "CA -" + caUrl + " admin is already enrolled.");
			return userContext;
		}
		Enrollment adminEnrollment = instance.enroll(username, password);
		adminContext.setEnrollment(adminEnrollment);
		Logger.getLogger(CAClient.class.getName()).log(Level.INFO, "CA -" + caUrl + " Enrolled Admin.");
		Util.writeUserContext(adminContext);	//파일 작성 안해도 ID/PW로 인증
		return adminContext;
	}
}
