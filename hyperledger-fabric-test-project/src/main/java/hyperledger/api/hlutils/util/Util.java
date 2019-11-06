package hyperledger.api.hlutils.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hyperledger.api.hlutils.user.UserContext;


public class Util {
	
	private static Logger logger = LoggerFactory.getLogger(Util.class);
	
	/**
	 * Serialize user
	 * 
	 * @param userContext
	 * @throws Exception
	 */
	public static void writeUserContext(UserContext userContext) throws Exception {
		String directoryPath = "users/" + userContext.getAffiliation();
		String filePath = directoryPath + "/" + userContext.getName() + ".ser";
		
		logger.info("path ::: " +filePath);
		
		File directory = new File(directoryPath);
		if (!directory.exists())
			directory.mkdirs();

		FileOutputStream file = new FileOutputStream(filePath);
		ObjectOutputStream out = new ObjectOutputStream(file);

		out.writeObject(userContext);

		out.close();
		file.close();
	}

	/**
	 * Deserialize user
	 * 
	 * @param affiliation
	 * @param username
	 * @return
	 * @throws Exception
	 */
	public static UserContext readUserContext(String affiliation, String username) throws Exception {
		String filePath = "users/" + affiliation + "/" + username + ".ser";
		
		logger.info("path ::: " +filePath);
		
		File file = new File(filePath);
//		if (file.exists()) {
//			// Reading the object from a file
//			FileInputStream fileStream = new FileInputStream(filePath);
//			ObjectInputStream in = new ObjectInputStream(fileStream);
//
//			// Method for deserialization of object
//			UserContext uContext = (UserContext) in.readObject();
//
//			in.close();
//			fileStream.close();
//			return uContext;
//		}

		return null;
	}
}
