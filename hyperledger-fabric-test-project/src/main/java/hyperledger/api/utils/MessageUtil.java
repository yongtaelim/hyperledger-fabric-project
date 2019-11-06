package hyperledger.api.utils;

import org.springframework.context.support.MessageSourceAccessor;

public class MessageUtil {

	private static MessageSourceAccessor messageSourceAccessor = null;
	
	public MessageSourceAccessor getMessageSourceAccessor() {
		return messageSourceAccessor;
	}

	public void setMessageSourceAccessor(MessageSourceAccessor messageSourceAccessor) {
		MessageUtil.messageSourceAccessor = messageSourceAccessor;
	}

	public static String getMessage(String code) {
		return  messageSourceAccessor.getMessage(code);
	}
	
}
