package hyperledger.api.utils;

public class ResultCode {
	public static final String S = "S";
	public static final String E = "E";
	
	public static final String E0000 = "E0000";			//암호화에 실패하였습니다
	public static final String E0001 = "E0001";			//검증에 실패하였습니다.
	public static final String E0002 = "E0002";			//결제비밀번호 조회에 실패하였습니다.
	public static final String E0003 = "E0003";			//복호화에 실패하였습니다.
	public static final String E0004 = "E0004";			//결제 비밀번호 인증 불가
	public static final String E0006 = "E0006";			//선행프로세스 검증 실패하였습니다.
	public static final String E0007 = "E0007";			//DB 조회에 실패하였습니다.
	public static final String E0008 = "E0008";			//결제수단 정보 조회에 실패하였습니다.
	public static final String E0009 = "E0009";			//결제 비밀번호 정보 조회에 실패하였습니다.
	public static final String E0010 = "E0010";			//결제수단OTC발급요청 조회에 실패하였습니다.
	public static final String E0011 = "E0011";			//결제승인요청 조회에 실패하였습니다.
	public static final String E0013 = "E0013";			//결제승인정보 검증에 실패하였습니다.
	public static final String E0014 = "E0014";			//취소승인정보 저장에 실패하였습니다.
	
	public static final String E1000 = "E1000";			//가맹점 정보 조회에 실패하였습니다.
	public static final String E1001 = "E1001";			//승인 정보 조회에 실패하였습니다.
	public static final String E1002 = "E1002";			//FDS수집정보 조회에 실패하였습니다.
	public static final String E1003 = "E1003";			//결제요청정보 저장에 실패하였습니다.
	public static final String E1004 = "E1004";			//결제수단인증정보 저장에 실패하였습니다.
	public static final String E1005 = "E1005";			//승인정보 저장에 실패하였습니다.
	public static final String E1006 = "E1006";			//FDS수집정보 저장에 실패하였습니다.
	
}
