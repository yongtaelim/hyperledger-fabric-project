package hyperledger.api.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.cxf.common.util.Base64Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import hyperledger.api.service.ApiService;
import hyperledger.api.service.ConnectService;
import hyperledger.api.utils.HASH;
import hyperledger.api.utils.RSA;

/**
 * Handles requests for the application home page.
 */

@RestController
public class ApiController {
	
	@Autowired
	private ApiService apiService;
	
	@Autowired
	private ConnectService connectService;
	
	@RequestMapping("home")
	public ModelAndView home(@RequestParam Map<String, Object> paramMap) throws Exception {
		ModelAndView mav = new ModelAndView();
		mav.addAllObjects(connectService.query(paramMap));
		return mav;
	}	
	
	@RequestMapping("setDefault")
	public void setDefault() throws Exception {
		connectService.setDefault();
	}
	
	//결제요청 Test
	@RequestMapping(value="setlReq", method=RequestMethod.POST)
	public Map<String, Object> setlReq(@RequestBody Map<String, Object> paramMap) throws Exception {
		Map<String, Object> resultMap = apiService.setlReq(paramMap);
		return resultMap;
	}
	

	//가맹점등록 Test
	@RequestMapping(value="insertAfltstorTest.do")
	public Map<String, Object> insertAfltstorTest(Map<String, Object> param) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		
		//임시생성 RSA키페어
		String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkK6NN+HHb6pVF2YdBu5FLFw2D+KFJaeFlYOVm59x195YKNC0y9e409jDUnNKMa5zZ/rjoDdt2y+pyksXKY7Zfp1iu3b7fm+jKdMG/Dy7H+JlO7WLLTqwkG7mCxouF4HLbBoKh6IXZF5Ludx0crxK1REW5xMMll3FeRdm12AxMmhONl9uEQ+j4TbxtEuOpdcJGz2yKyezxkXn7cNdqEem+GD8n6AjxuqjonlGTOCLOoIAbyMCZx2Y5SUb49kUH/AgbjNpaWmQyKtKvOj/6dDWJieJMwHHAf2GdjCM1hiEILxJfHutC9k/BLO6ASWQRFU4WwM/WJTV4gwTrbw4U+pWBwIDAQAB";
		String privateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCQro034cdvqlUXZh0G7kUsXDYP4oUlp4WVg5Wbn3HX3lgo0LTL17jT2MNSc0oxrnNn+uOgN23bL6nKSxcpjtl+nWK7dvt+b6Mp0wb8PLsf4mU7tYstOrCQbuYLGi4XgctsGgqHohdkXku53HRyvErVERbnEwyWXcV5F2bXYDEyaE42X24RD6PhNvG0S46l1wkbPbIrJ7PGReftw12oR6b4YPyfoCPG6qOieUZM4Is6ggBvIwJnHZjlJRvj2RQf8CBuM2lpaZDIq0q86P/p0NYmJ4kzAccB/YZ2MIzWGIQgvEl8e60L2T8Es7oBJZBEVThbAz9YlNXiDBOtvDhT6lYHAgMBAAECggEAdSei2nlM0TMQLB5XquENylnwvPQj9NWt6d/fKWD3AmHc6U/21okb9Q81OnJ4xboJYU1bPMUlg7MYveIII3K00z4GUN6Lz1n+uUNUCLP0omDksHDU/g7kIlBqntrYL9i+LA76L9IlNvTURPAqqy2lOtrXvM1ECA5udRtp/0S5EaMBDvS6/ZRclRQ/meWauhbUQRQUXrsIWqFJ3gGoKO3+Z8hCQ7tGrpO0qb4CwEvZOfeK8qdojB1wgN3v/dMVHWVYNzHKWTE+pBbwL/ghRDTVmBMq3UsFb7qYKuSxJg5jTbmUzvNMfP/HdW3UAvd9nhZFe+1u/CT4DnsZTiKlXrazgQKBgQDhF2VjfMEMBAmTozqrCE2nd6Un7XmawOrtv60sBbJ9VYlAAJCiNczYFQB9MpkAfomnhPoAkA7zziI14GpoAXxC4L35ndkdTnBtu7P7tQZKgkafC8H1hyJnwOnZiFS3dqLaMxxzXMMjAQY7Re9peVq6XLIxds2v19CMvaHpnFl99wKBgQCkjIfgdt1Lxd0XwtCcdAYAwRkLWy/AgI1qI4i5d2g5uGwOJZ+Y1DJyDQFmGmH4WJeN3yF8UGZwTeCa+PCT/ySDNMPAcCRMPgTQWhBKSaPZKsXxqEDT6s+onAN5ExGTOD+qyvuQb/LViiBCvHfaAanz5pGHA701zrVc8aCcKiUkcQKBgQCPxITCtZUL2qxPOyumwCfxNe8sAHbyTBQBhLLT8lOdz/tZAMNgh+JthYSJcr7c3PWJJNsVbu3N9TROmyb7hS9b+/X7MEHTgR04SXJc67O39VusDDN3Yjesc0Ap6/TINaMwoQKkAHS2r9nOFA4lnRhA/iHmjA42KS8QJbL75hwW4wKBgESm+T3WeefWSrNSLZVI9wQzjE5nohmvyNCCuNWgkxSZ9ENGwTqTNeTNtkqyMTPbSof/k0n5hXPPmikuObmVltdDGXTdlNdWfM2s9eT4qmq95M45Q6rm6fLP/Mb0BL4mlrGcdK7Ddk5PjbwqQ9rqHM1M4mSxU6zybEq16SuejdaRAoGATxl8WeDnPaAoG5dkcPzbEV0IPrKrP0hMH6jxrGkYTPzysGseI3eOnNMe9RZ66K9bfTuobjAwNRsfP0p02o7hPLzU2dZqwGKwdDgdnGalnbnVVdXRqEofRJ3g4U5vNQgmZv3Yy0WZU/hw2LVogNKeP3E/3mwK5K+twSp5TSfMMx0=";
		
		
		//테스트 데이터 고정
		param.put("afltstorRcgntKey", "01234567896431231564564");			//가맹점 식별키
		param.put("afltstorNum", "0123456789999999");			//가맹점 식별키
		param.put("afltstorBcKey", privateKey);						//가맹점BC키	
		param.put("afltstorPbKey", publicKey);					//가맹점공개키
		param.put("bzregno", "1234567890");				//사업자번호
		
		result = apiService.crtAfltstorIfo(param);
		System.out.println("-- insertAfltstorTest result : " + result);
		return result;
		
	}
	
	//가맹점조회 단일 TEST
	@RequestMapping(value="selectAfltstorTest.do")
	public Map<String, Object> selectAfltstorTest(Map<String, Object> param) throws Exception {
		Map<String, Object> result = new HashMap<>();
		
		param.put("afltstorRcgntKey", "0x000002");
		result = apiService.getAlftstorIfo(param);
		System.out.println("result ::: "+result);
		
		return result;
	}
	
	//가맹점조회 복수 TEST
	@RequestMapping(value="selectAfltstorQuery.do")
	public Map<String, Object> selectAfltstorQuery(Map<String, Object> param) throws Exception {
		Map<String, Object> result = new HashMap<>(); 

		result = apiService.getAlftstorListIfo(param);
		System.out.println("result ::: "+result);
		
		return result;
	}
	
	//결제요청 Test
	@RequestMapping(value="setlReqTest.do")
	public Map<String, Object> setlReqTest(Map<String, Object> param) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();

		//임시생성 RSA키페어
		String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkK6NN+HHb6pVF2YdBu5FLFw2D+KFJaeFlYOVm59x195YKNC0y9e409jDUnNKMa5zZ/rjoDdt2y+pyksXKY7Zfp1iu3b7fm+jKdMG/Dy7H+JlO7WLLTqwkG7mCxouF4HLbBoKh6IXZF5Ludx0crxK1REW5xMMll3FeRdm12AxMmhONl9uEQ+j4TbxtEuOpdcJGz2yKyezxkXn7cNdqEem+GD8n6AjxuqjonlGTOCLOoIAbyMCZx2Y5SUb49kUH/AgbjNpaWmQyKtKvOj/6dDWJieJMwHHAf2GdjCM1hiEILxJfHutC9k/BLO6ASWQRFU4WwM/WJTV4gwTrbw4U+pWBwIDAQAB";
		String privateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCQro034cdvqlUXZh0G7kUsXDYP4oUlp4WVg5Wbn3HX3lgo0LTL17jT2MNSc0oxrnNn+uOgN23bL6nKSxcpjtl+nWK7dvt+b6Mp0wb8PLsf4mU7tYstOrCQbuYLGi4XgctsGgqHohdkXku53HRyvErVERbnEwyWXcV5F2bXYDEyaE42X24RD6PhNvG0S46l1wkbPbIrJ7PGReftw12oR6b4YPyfoCPG6qOieUZM4Is6ggBvIwJnHZjlJRvj2RQf8CBuM2lpaZDIq0q86P/p0NYmJ4kzAccB/YZ2MIzWGIQgvEl8e60L2T8Es7oBJZBEVThbAz9YlNXiDBOtvDhT6lYHAgMBAAECggEAdSei2nlM0TMQLB5XquENylnwvPQj9NWt6d/fKWD3AmHc6U/21okb9Q81OnJ4xboJYU1bPMUlg7MYveIII3K00z4GUN6Lz1n+uUNUCLP0omDksHDU/g7kIlBqntrYL9i+LA76L9IlNvTURPAqqy2lOtrXvM1ECA5udRtp/0S5EaMBDvS6/ZRclRQ/meWauhbUQRQUXrsIWqFJ3gGoKO3+Z8hCQ7tGrpO0qb4CwEvZOfeK8qdojB1wgN3v/dMVHWVYNzHKWTE+pBbwL/ghRDTVmBMq3UsFb7qYKuSxJg5jTbmUzvNMfP/HdW3UAvd9nhZFe+1u/CT4DnsZTiKlXrazgQKBgQDhF2VjfMEMBAmTozqrCE2nd6Un7XmawOrtv60sBbJ9VYlAAJCiNczYFQB9MpkAfomnhPoAkA7zziI14GpoAXxC4L35ndkdTnBtu7P7tQZKgkafC8H1hyJnwOnZiFS3dqLaMxxzXMMjAQY7Re9peVq6XLIxds2v19CMvaHpnFl99wKBgQCkjIfgdt1Lxd0XwtCcdAYAwRkLWy/AgI1qI4i5d2g5uGwOJZ+Y1DJyDQFmGmH4WJeN3yF8UGZwTeCa+PCT/ySDNMPAcCRMPgTQWhBKSaPZKsXxqEDT6s+onAN5ExGTOD+qyvuQb/LViiBCvHfaAanz5pGHA701zrVc8aCcKiUkcQKBgQCPxITCtZUL2qxPOyumwCfxNe8sAHbyTBQBhLLT8lOdz/tZAMNgh+JthYSJcr7c3PWJJNsVbu3N9TROmyb7hS9b+/X7MEHTgR04SXJc67O39VusDDN3Yjesc0Ap6/TINaMwoQKkAHS2r9nOFA4lnRhA/iHmjA42KS8QJbL75hwW4wKBgESm+T3WeefWSrNSLZVI9wQzjE5nohmvyNCCuNWgkxSZ9ENGwTqTNeTNtkqyMTPbSof/k0n5hXPPmikuObmVltdDGXTdlNdWfM2s9eT4qmq95M45Q6rm6fLP/Mb0BL4mlrGcdK7Ddk5PjbwqQ9rqHM1M4mSxU6zybEq16SuejdaRAoGATxl8WeDnPaAoG5dkcPzbEV0IPrKrP0hMH6jxrGkYTPzysGseI3eOnNMe9RZ66K9bfTuobjAwNRsfP0p02o7hPLzU2dZqwGKwdDgdnGalnbnVVdXRqEofRJ3g4U5vNQgmZv3Yy0WZU/hw2LVogNKeP3E/3mwK5K+twSp5TSfMMx0=";
		
		
		//테스트 데이터 고정
		param.put("afltstorRcgntKey", "0x000002");			//가맹점 식별키
		param.put("blkchnDivCod", "H");					//블록체인구분코드
		param.put("trscNum", "H");						//거래번호	
		param.put("prdIfo", "H");						//상품정보
		param.put("cardCoDivCod", "00");				//카드사구분코드(00:현대카드)
		param.put("itlmMmsCnt", "00");					//할부개월수(00:일시불)
		param.put("curcyCod", "401");					//통화코드(410:원)
		param.put("setlReqAmot", "30000");				//결제요청금액
		param.put("gdsDlipc", "seoul");					//물품배송지
		param.put("afltstorMembContct", "01011111111");	//가맹점회원연락처
//		param.put("setlIfoSignKey", "0x1Q2ew");			//결제정보서명키(C01)
		param.put("setlTxProcTypeCod", "A");			//결제Trx처리유형코드(A:12345)
		param.put("setlReqTypeCod", "1");				//결제요청유형코드(APP:0, WEB:1)
		param.put("uuid", "10203-1231");				//UUID
		param.put("ip", "1.1.1.1");						//IP
		param.put("os", "WINDOW");						//OS
		

		//결제정보 서명키 생성
		//1. h-결제정보 = hash(거래번호+결제요청금액+상픔정보)
		String trscNum = param.get("trscNum").toString();
		String apvAmot = param.get("setlReqAmot").toString();
		String prdIfo = param.get("prdIfo").toString();
		String hashSetlIfo = HASH.sha3String(trscNum+apvAmot+prdIfo);
		
		//2. 결제정보서명키 = 가맹점BC키 Enc(h-결제정보)
		String setlIfoSignKey = Base64Utility.encode(RSA.encryptByPrivateKey(hashSetlIfo.getBytes(), privateKey));
		System.out.println("-- setlIfoSignKey : " + setlIfoSignKey);
		
		//복호화 테스트
		byte[] decodeData = Base64Utility.decode(setlIfoSignKey);
		String decData = new String(RSA.decryptByPublicKey(decodeData, publicKey));
		System.out.println("-- decData : " + decData);
		
		
		param.put("setlIfoSignKey", setlIfoSignKey);			//결제정보서명키(C01)
		
		result = apiService.setlReq(param);
		System.out.println("-- setlReqTest result : " + result);
		return result;
		
	}
	
	//결제승인요청 Test
	@RequestMapping(value="setlApvReqTest.do")
	public Map<String, Object> setlApvReqTest(Map<String, Object> param) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		
		//테스트 데이터 고정
		param.put("afltstorRcgntKey", "abcde");			//가맹점 식별키
		param.put("blkchnDivCod", "H");					//블록체인구분코드
		param.put("trscNum", "00000000000001");						//거래번호	
		param.put("prdIfo", "상품1");						//상품정보
		param.put("itlmMms", "00");					//할부개월수(00:일시불)
		param.put("curcyCod", "401");					//통화코드(410:원)
		param.put("setlReqAmot", "30000");				//결제요청금액
		param.put("afltstorMembContct", "01011111111");	//가맹점회원연락처1(AES) (C03)
		param.put("afltstorNum", "123456789012345");	//가맹점번호
		param.put("enprNum", "1234567890");				//사업자번호
		param.put("setlTxId", "0xb2930b35844a230f00e51431acae96fe543a0347");			//결제TxId
		param.put("reqDtm", "201902161123020000");		//요청일시
		param.put("crdId", "0xAAAAAAAA");				//카드ID
		param.put("custNum", "CUSTNUM0001");			//고객번호
		param.put("crdtCrdNum1", "0x1ew31E14r1");		//신용카드번호1(MASK,AES) (C03)
		param.put("crdtCrdNum2", "0x1ew31E14r2");		//신용카드번호2(MASK,HASH) (H01)
		param.put("fdsAddCertCod", "08");				//FDS추가인증코드 (08:무인증)
		param.put("blkchnFdsVrfcRsultCod", "00");		//블록체인FDS검증결과코드 (00:정상)
		param.put("setlMeanCertRspsCod", "00");			//결제수단인증 응답코드 (00:정상)
		param.put("setlPwdCertRspsCod", "00");			//결제비밀번호인증 응답코드 (00:정상)
		param.put("otcCreDivCod", "0");					//OTC생성구분코드 (0:자동인증, 1:추가인증)
		param.put("setlTxProcTypeCod", "A");			//결제Trx처리유형코드(A:12345)
		param.put("setlReqTypeCod", "1");				//결제요청유형코드(APP:0, WEB:1)
		param.put("uuid", "10203-1231");				//UUID
		param.put("ip", "1.1.1.1");						//IP
		param.put("os", "WINDOW");						//OS
		
		result = apiService.setlApvReq(param);
		
		return result;
		
	}

	//결제취소요청 Test
	@RequestMapping(value="setlCanApvReqTest.do")
	public Map<String, Object> setlCanApvReqTest(Map<String, Object> param) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		
		//테스트 데이터 고정
		param.put("afltstorRcgntKey", "01234567896431231564564");			//가맹점 식별키
		param.put("blkchnDivCod", "H");						//블록체인구분코드
		param.put("apvNum", "00800000051");					//승인번호	
		param.put("trscNum", "0000000000");					//거래번호
		param.put("apvAmot", "180000");					//승인금액
		
		result = apiService.setlCanApvReq(param);
		
		return result;
		
	}
	
	@RequestMapping("getSeltTxIdPage")
	public ModelAndView getSeltTxIdPage(@RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		return mav;
	}
	
	@RequestMapping("afltstorManagement")
	public ModelAndView afltstorManagement(@RequestParam Map<String, Object> paramMap) {
		ModelAndView mav = new ModelAndView();
		return mav;
	}
	
	@RequestMapping("getSetlTxId")
	public Map<String, Object> getSetlTxId(@RequestParam Map<String, Object> paramMap) throws Exception {
		Map<String, Object> resultMap = apiService.getSetlTxId(paramMap);
		return resultMap;
	}
	
	@RequestMapping("getAlftstorListIfo")
	public Map<String, Object> getAlftstorListIfo(@RequestParam Map<String, Object> paramMap) throws Exception {
		Map<String, Object> resultMap = apiService.getAlftstorListIfo(paramMap);
		return resultMap;
	}
	
	@RequestMapping(value="insertAfltstor")
	public Map<String, Object> insertAfltstor(@RequestParam Map<String, Object> paramMap) throws Exception {
		 Map<String, Object> resultMap = apiService.crtAfltstorIfo(paramMap);
		System.out.println("-- insertAfltstor result : " + resultMap);
		return resultMap;
			
	}
	
	@RequestMapping(value="updateAfltstor")
	public Map<String, Object> updateAfltstor(@RequestParam Map<String, Object> paramMap) throws Exception {
		Map<String, Object> resultMap = apiService.uptAfltstorIfo(paramMap);
		System.out.println("-- updateAfltstor result : " + resultMap);
		return resultMap;
		
	}
}
