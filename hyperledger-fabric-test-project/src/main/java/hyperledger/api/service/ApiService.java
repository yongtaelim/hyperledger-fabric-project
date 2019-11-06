package hyperledger.api.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.common.util.Base64Utility;
import org.hyperledger.fabric.sdk.Peer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hyperledger.api.hlutils.client.ChannelClient;
import hyperledger.api.hlutils.client.FabricClient;
import hyperledger.api.mapper.ApiMapper;
import hyperledger.api.utils.HASH;
import hyperledger.api.utils.MessageUtil;
import hyperledger.api.utils.PropertiesUtil;
import hyperledger.api.utils.RSA;
import hyperledger.api.utils.ResultCode;

@Service
public class ApiService {
		
	@Autowired
	public ApiMapper apiMapper;
	
	@Autowired
	public ConnectService connectService;
	
	private Log logger = LogFactory.getLog(ApiService.class);
	
	public ChannelClient channelClient 		= null;
	public ChannelClient channelClient_ch1	= null;
	public ChannelClient channelClient_ch2	= null;
	
	public FabricClient fabClient = null;
	
	public Peer peer1 = null;
	public Peer peer3 = null;
	
	/**
	 * 결제요청
	 * @param paramMap
	 * @return
	 */
	public Map<String, Object> setlReq(Map<String, Object> paramMap) throws Exception {
		logger.info("-- START. 결제요청 데이터 : " + paramMap);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		//1. 가맹점 정보조회
		Map<String, Object> afltstorMap = new HashMap<String, Object>();
		afltstorMap = getAlftstorIfo(paramMap);
		if(!"00".equals(afltstorMap.get("rsltCd"))){
			resultMap.putAll(ErrorException(ResultCode.E1000)); 
			return resultMap;
		}
//		System.out.println("-- 1. getAlftstorIfo : " + afltstorMap);

		//2. 주문정보 검증
		//2-1. h결제정보 = hash(주문번호+결제요청금액+상픔정보)
		String hashSetlIfo = HASH.sha3String(paramMap.get("trscNum").toString() + paramMap.get("setlReqAmot").toString() + paramMap.get("prdIfo").toString());
//		System.out.println("-- 2-1. hashSetlIfo : "+hashSetlIfo);
		
		//2-2. dh결제정보 = 가맹점공개키 desc(결제정보서명키)
		byte[] decodeData = Base64Utility.decode(paramMap.get("setlIfoSignKey").toString());
		String decHashSetlIfo = new String(RSA.decryptByPublicKey(decodeData, afltstorMap.get("afltstorPbKey").toString()));
//		System.out.println("-- 2-2. decHashSetlIfo : " + decHashSetlIfo);
		
		//2-3. 비교검증 (h결제정보 == dh-결제정보)
		if(!hashSetlIfo.equals(decHashSetlIfo)) {
			resultMap.putAll(ErrorException(ResultCode.E0001));
			return resultMap;
		}
//		System.out.println("-- 2-3. hashSetlIfo == decHashSetlIfo");
		
		//3. 요청일시 생성 (Sysdate(yyyyMMddhhmmssfff)
		String reqDtm = new SimpleDateFormat("yyyyMMddHHmmssSSSS").format(new Date());
//		System.out.println("-- 3. reqDtm : " + reqDtm);
		
		//4. 결제TxId 생성 = hash(가맹점번호 + 요청일시 + 거래번호)
		String setlTxId = HASH.sha3String(afltstorMap.get("afltstorNum").toString() + reqDtm + paramMap.get("trscNum").toString());
//		System.out.println("-- 4. setlTxId : " + setlTxId);
		
		//5. 가맹점회원연락처2 생성 = hash(가맹점회원연락처)
		String afltstorMembContct2 = HASH.sha3String(paramMap.get("afltstorMembContct").toString());
//		System.out.println("-- 5. afltstorMembContct2 : " + afltstorMembContct2);
		
		//6. FDS검증요청(DB)
		Map<String, Object> getFdsVrfcInfo = apiMapper.getFdsVrfcInfo();
		String fdsAddCertCod = getFdsVrfcInfo.get("PROC_CD").toString();
//		System.out.println("-- 6. fdsAddCertCod : " + fdsAddCertCod);
		
		//7. FDS검증(blockchain)
		Map<String, Object> chkFdsMap = new HashMap<String, Object>();
		chkFdsMap = chkFdsClctIfo(paramMap);
		if(!"00".equals(afltstorMap.get("rsltCd"))){
			resultMap.putAll(ErrorException(ResultCode.E1002)); 
			return resultMap;
		}
				
		resultMap.put("rsltCd", ResultCode.S);
		resultMap.put("afltstorNum", afltstorMap.get("afltstorNum"));//가맹점번호
		resultMap.put("enprNum", afltstorMap.get("enprNum"));		//사업자번호
		resultMap.put("setlTxId", setlTxId);						//결제TxId
		resultMap.put("reqDtm", reqDtm);							//요청일시
		resultMap.put("fdsAddCertCod", fdsAddCertCod);			//FDS추가인증코드(08:추가인증없음)
		resultMap.put("blkchnFdsVrfcRsultCod", chkFdsMap.get("resultData"));	//블록체인FDS검증결과코드(00:정상, 99:실패)
		
		logger.info("-- FIN. 결제요청 결과 : " + resultMap);
		return resultMap;
	}

	/**
	 * 결제승인요청
	 * @param paramMap
	 * @return
	 */
	public Map<String, Object> setlApvReq(Map<String, Object> paramMap) throws Exception{
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("-- START. 결제승인요청 데이터 : " + paramMap);
		
		//1. 선행프로세스 검증 (FDS 추가인증코드=08 / 블록체인FDS검증결과코드=00 / 결제수단인증응답코드=00 / 결제비밀번호인증응답코드=00)
		if(paramMap.get("fdsAddCertCod").equals("08") && paramMap.get("blkchnFdsVrfcRsultCod").equals("00") && paramMap.get("setlMeanCertRspsCod").equals("00") && paramMap.get("setlPwdCertRspsCod").equals("00")) {
//			System.out.println("-- 1. 선행프로세스 검증 TRUE ");
		}else {
//			System.out.println("-- 1. 선행프로세스 검증 FALSE ");
			resultMap.put("rsltCd", "E0006");
			return resultMap;
		}
		
		//2. 결제요청정보 저장 (chaincode)
		Map<String, Object> setlReqIfoMap = new HashMap<String, Object>();
		setlReqIfoMap = crtSetlReqIfo(paramMap);
		if(!"00".equals(setlReqIfoMap.get("rsltCd"))){
			resultMap.put("rsltCd","E1003");
			return resultMap;
		}
//		System.out.println("-- 2. 결제요청정보 저장 : " + setlReqIfoMap);
		String setlReqIfoTxHash = setlReqIfoMap.get("txHash").toString();
		
		//3. 결제수단OTC발급요청(처리계연동가정 DB 조회)
		Map<String, Object> getSetlMeanOtcIssuInfo = apiMapper.getSetlMeanOtcIssuInfo(paramMap);
		if(getSetlMeanOtcIssuInfo == null){
			resultMap.put("rsltCd","E0010");
			return resultMap;
		}
		String otcValue = getSetlMeanOtcIssuInfo.get("OTC").toString();
		paramMap.put("otc", otcValue);
//		System.out.println("-- 3. 결제수단OTC발급요청 : " + otcValue);
		
		//4. 결제수단인증정보 저장 (chaincode)
		Map<String, Object> setlMeanCertIfoMap = new HashMap<String, Object>();
		setlMeanCertIfoMap = crtSetlMeanCertIfo(paramMap);
		if(!"00".equals(setlMeanCertIfoMap.get("rsltCd"))){
			resultMap.put("rsltCd","E1004");
			return resultMap;
		}
//		System.out.println("-- 4. 결제수단인증정보 저장 : " + setlMeanCertIfoMap);
		String setlMeanCertIfoTxHash = setlMeanCertIfoMap.get("txHash").toString();
		
		//5. 결제승인요청 조회 (결제승인OTC DB 응답코드조회)
		Map<String, Object> getSetlApvReqInfo = apiMapper.getSetlApvReqInfo(paramMap);
		if(getSetlApvReqInfo == null){
			resultMap.put("rsltCd","E0011");
			return resultMap;
		}
		String chkSetlApvReq = getSetlApvReqInfo.get("RSPS_CD").toString();
		if(!"00".equals(chkSetlApvReq)){
			resultMap.put("rsltCd","E0011");
			return resultMap;
		}
//		System.out.println("-- 5. 결제승인요청 조회 : " + chkSetlApvReq);
		
		//6. 승인번호 생성(008 + 승인번호SEQ 8자리 DB조회)
		Map<String, Object> getApvNumSeqMap = new HashMap<String, Object>();
		apiMapper.getSeqApvNum(getApvNumSeqMap);
		String tmpApvNum = getApvNumSeqMap.get("seq").toString();
		// 번호 남는 자리 zero fill
		while (tmpApvNum.length() < 8) {
			tmpApvNum = "0" + tmpApvNum;
		}
		String apvNum = "008" + tmpApvNum;
		paramMap.put("apvNum", apvNum);
//		System.out.println("-- 6. 승인번호생성 : " + apvNum);
		
		//7. 승인일시 생성(SYSDATE : yyyyMMddhhmmssffff)
		String apvDtm = new SimpleDateFormat("yyyyMMddHHmmssSSSS").format(new Date());
		paramMap.put("apvDtm", apvDtm);
//		System.out.println("-- 7. 승인일시 생성 : " + apvDtm);
		
		//8. 등록일자 생성(SYSDATE : yyMMdd)
		String regDt = new SimpleDateFormat("yyMMdd").format(new Date());
		paramMap.put("regDt", regDt);
//		System.out.println("-- 8. 등록일자 생성 : " + regDt);
		
		//9. 승인구분코드 생성(결제요청유형코드 + OTC생성구분코드)
		String apvDivCod = paramMap.get("setlReqTypeCod").toString() + paramMap.get("otcCreDivCod").toString();
		paramMap.put("apvDivCod", apvDivCod);
//		System.out.println("-- 9. 승인구분코드 생성 : " + apvDivCod);
		
		//10. 거래고유번호생성(SEQ 12자리 왼쪽0으로 채음. 00000000001)
		Map<String, Object> getTrscUniqNumSeqMap = new HashMap<String, Object>();
		apiMapper.getSeqTrscUniqNum(getTrscUniqNumSeqMap);
		String trscUniqNum = getTrscUniqNumSeqMap.get("seq").toString();
		// 번호 남는 자리 zero fill
		while (trscUniqNum.length() < 12) {
			trscUniqNum = "0" + trscUniqNum;
		}
		paramMap.put("trscUniqNum", trscUniqNum);
//		System.out.println("-- 10. 거래고유번호생성 : " + trscUniqNum);
		
		//11. h앱검증Hash 생성(APP: HASH(가맹점회원 연락처 + UUID), WEB: HASH(가맹점회원 연락처 + IP + OS))
		String tmpVrfcIfo="";
		if(paramMap.get("setlReqTypeCod").equals("0")) {	//APP
			tmpVrfcIfo = paramMap.get("afltstorMembContct").toString() + paramMap.get("uuid").toString();
		}else {	//WEB
			tmpVrfcIfo = paramMap.get("afltstorMembContct").toString() + paramMap.get("ip").toString() + paramMap.get("os").toString();
		}
		String vrfcIfo = HASH.sha3String(tmpVrfcIfo);
		paramMap.put("vrfcIfo", vrfcIfo);
//		System.out.println("-- 11. h앱검증Hash 생성 : " + vrfcIfo);
		
		//12. 승인정보 저장(chaincode)
		paramMap.put("balIfo", "0");	//잔액정보는 0 고정
		paramMap.put("itlmMms", "002" + paramMap.get("itlmMmsCnt"));	//승인할부개월수 : 002 + itlmMms
		Map<String, Object> apvIfoMap = new HashMap<String, Object>();
		apvIfoMap = crtApvIfo(paramMap);
		if(!"00".equals(apvIfoMap.get("rsltCd"))){
			resultMap.put("rsltCd","E1005");
			return resultMap;
		}
//		System.out.println("-- 12. 승인정보 저장 : " + apvIfoMap);
		String apvIfoTxHash = apvIfoMap.get("txHash").toString();
		
		//13. FDS수집정보 저장(chaincode)
		Map<String, Object> fdsClctIfoMap = new HashMap<String, Object>();
		fdsClctIfoMap = crtFdsClctIfo(paramMap);
		if(!"00".equals(fdsClctIfoMap.get("rsltCd"))){
			resultMap.put("rsltCd","E1006");
			return resultMap;
		}
//		System.out.println("-- 13. FDS수집정보 저장 : " + fdsClctIfoMap);
		String fdsClctIfoTxHash = fdsClctIfoMap.get("txHash").toString();
		
		//14. 정상완료시 승인번호/승인일시/승인금액 리턴) 
		resultMap.put("rsltCd", "S");
		resultMap.put("apvNum", paramMap.get("apvNum"));
		resultMap.put("apvDtm", paramMap.get("apvDtm"));
		resultMap.put("apvAmot", paramMap.get("setlReqAmot"));

		//TXHASH 및 결제TXID 리턴
		resultMap.put("setlReqIfoTxHash", setlReqIfoTxHash);
		resultMap.put("setlMeanCertIfoTxHash", setlMeanCertIfoTxHash);
		resultMap.put("apvIfoTxHash", apvIfoTxHash);
		resultMap.put("fdsClctIfoTxHash", fdsClctIfoTxHash);
		resultMap.put("setlTxId", paramMap.get("setlTxId"));
		resultMap.put("afltstorRcgntKey", paramMap.get("afltstorRcgntKey"));
		resultMap.put("blkchnDivCod", paramMap.get("blkchnDivCod"));
		resultMap.put("trscNum", paramMap.get("trscNum"));
		logger.info("-- FIN. 결제승인요청 결과 : " + resultMap);
		return resultMap;
	}
	

	/**
	 * 결제취소요청
	 * @param paramMap
	 * @return
	 */
	public Map<String, Object> setlCanApvReq(Map<String, Object> paramMap) throws Exception{
		Map<String, Object> resultMap = new HashMap<String, Object>();
		logger.info("-- START. 결제취소요청 데이터 : " + paramMap);
		
		//1. 가맹점 정보조회
		Map<String, Object> afltstorMap = new HashMap<String, Object>();
		afltstorMap = getAlftstorIfo(paramMap);
		if(!"00".equals(afltstorMap.get("rsltCd"))){
			resultMap.putAll(ErrorException(ResultCode.E1000)); 
			return resultMap;
		}
//		System.out.println("-- 1. 가맹점 정보조회 : " + afltstorMap);

		//2. 결제승인정보 검증
		Map<String, Object> chkApvMap = new HashMap<String, Object>();
		chkApvMap = chkApvIfo(paramMap);
		if(!"00".equals(chkApvMap.get("rsltCd"))){
			resultMap.putAll(ErrorException(ResultCode.E0013)); 
			return resultMap;
		}
//		System.out.println("-- 2. 결제승인정보 검증 : " + chkApvMap);
		
		//3. 결제수단OTC발급요청(처리계연동가정 DB 조회)
		Map<String, Object> getSetlMeanOtcIssuInfo = new HashMap<String, Object>();
		//Map<String, Object> getSetlMeanOtcIssuInfo = apiMapper.getSetlMeanOtcIssuInfo(paramMap);
		//DB연동 성공 조건으로 데이터 하드코딩
		getSetlMeanOtcIssuInfo.put("rsltCd", "00");
		getSetlMeanOtcIssuInfo.put("balIfo", "0");
		
		if(getSetlMeanOtcIssuInfo == null){
			resultMap.put("rsltCd","E0010");
			return resultMap;
		}
		String balIfo = getSetlMeanOtcIssuInfo.get("balIfo").toString();
		paramMap.put("balIfo", balIfo);
//		System.out.println("-- 3. 결제수단OTC발급요청 잔액조회 : " + balIfo);

		//4. 취소 승인정보 생성
		Map<String, Object> getCanApvNumSeqMap = new HashMap<String, Object>();
		apiMapper.getSeqCanApvNum(getCanApvNumSeqMap);
		String tmpCanApvNum = getCanApvNumSeqMap.get("seq").toString();
		// 번호 남는 자리 zero fill
		while (tmpCanApvNum.length() < 8) {
			tmpCanApvNum = "0" + tmpCanApvNum;
		}
		String canApvNum = "008" + tmpCanApvNum;
		paramMap.put("apvNum", canApvNum);
//		System.out.println("-- 4. 취소승인번호생성 : " + canApvNum);
		
		//5. 취소승인일시 생성
		String canApvDtm = new SimpleDateFormat("yyyyMMddHHmmssSSSS").format(new Date());
		paramMap.put("apvDtm", canApvDtm);
//		System.out.println("-- 5. 취소승인일시 생성 : " + canApvDtm);
		
		//6. 결제취소TxId 생성
		String setlTxId = HASH.sha3String(afltstorMap.get("afltstorNum").toString() + canApvDtm + canApvNum);
		paramMap.put("setlTxId", setlTxId);
//		System.out.println("-- 6. 결제취소TxId 생성 : " + setlTxId);
		
		//7. 등록일자 생성
		String regDt = new SimpleDateFormat("yyMMdd").format(new Date());
		paramMap.put("regDt", regDt);
//		System.out.println("-- 7. 등록일자 생성 : " + regDt);
		
		//8. 취소승인에 필요한 데이터 넣기
		paramMap.put("cnntTxId", chkApvMap.get("setlTxId"));
		paramMap.put("apvDivCod", chkApvMap.get("apvDivCod"));
		paramMap.put("afltstorNum", chkApvMap.get("afltstorNum"));
		paramMap.put("trscUniqNum", chkApvMap.get("trscUniqNum"));
		paramMap.put("crdId", chkApvMap.get("crdId"));
		paramMap.put("itlmMms", chkApvMap.get("itlmMms"));
		paramMap.put("curcyCod", chkApvMap.get("curcyCod"));
		paramMap.put("crdtCrdNum", chkApvMap.get("crdtCrdNum"));
		paramMap.put("otc", chkApvMap.get("otc"));
//		System.out.println("-- 8. 취소승인정보 Insert paramMap : " + paramMap);
		
		//9. 취소승인정보 저장 요청
		Map<String, Object> canApvIfoMap = new HashMap<String, Object>();
		canApvIfoMap = crtCanApvIfo(paramMap);
		if(!"00".equals(canApvIfoMap.get("rsltCd"))){
			resultMap.put("rsltCd","E0014");
			return resultMap;
		}
//		System.out.println("-- 9. 승인정보 저장 : " + canApvIfoMap);
		String canApvIfoTxHash = canApvIfoMap.get("txHash").toString();
		
		resultMap.put("rsltCd", "S");
		//TXHASH 및 결제TXID 리턴
		resultMap.put("canApvIfoTxHash", canApvIfoTxHash);
		resultMap.put("setlTxId", setlTxId);
		
		logger.info("-- FIN. 결제취소요청 결과 : " + resultMap);
		return resultMap;
	}
	
	/**
	 * 가맹점 정보조회
	 * @param paramMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getAlftstorIfo(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> params = new HashMap<>();
		Map<String, Object> resultMap = new HashMap<>();
		
		params.put("chaincode", PropertiesUtil.getString("CHAINCODE_AFTLSTOR"));
		params.put("funcName", PropertiesUtil.getString("FUNC_AFTLSTOR_SELECT"));
		params.put("channel",  PropertiesUtil.getString("CHANNEL_NAME_CH1"));
		
		JSONObject args = new JSONObject();
		args.put("afltstorRcgntKey", paramMap.get("afltstorRcgntKey"));
		params.put("args", args.toJSONString());
		
		resultMap = connectService.query(params);
		printComLog(PropertiesUtil.getString("CHAINCODE_AFTLSTOR"), resultMap.get("runTime").toString(), resultMap.get("rsltCd").toString());
		if(!"00".equals(resultMap.get("rsltCd"))) {
			resultMap.put("rsltCd", "E");
			resultMap.put("rsltMsg", "조회실패");
			return resultMap;
		}
		
		return resultMap;
	}
	
	/**
	 * 가맹점리스트 조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	//cli ex : '{"Args":["getAfltstorIfoList", "{\"selector\":{\"afltstorRcgntKey\":{\"$ne\":null}}}"]}'
	public Map<String, Object> getAlftstorListIfo(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> params = new HashMap<>();
		Map<String, Object> resultMap = new HashMap<>();

		params.put("chaincode"	, PropertiesUtil.getString("CHAINCODE_AFTLSTOR"));
		params.put("funcName"	, PropertiesUtil.getString("FUNC_AFTLSTOR_SELECT_QUERY"));
		params.put("channel"	, PropertiesUtil.getString("CHANNEL_NAME_CH1"));
		
		JSONObject wrapObj = new JSONObject();
		wrapObj.put("selector", setArgsToJSONObj("afltstorRcgntKey", PropertiesUtil.getString("NE"), null));
		params.put("args", wrapObj.toJSONString());
		
		resultMap = connectService.query(params);
		logger.info("-- getAfltstorIfo res : " + resultMap);
		if(!"00".equals(resultMap.get("rsltCd"))) {
			resultMap.put("rsltCd", "E");
			resultMap.put("rsltMsg", "조회실패");
			return resultMap;
		}
		resultMap.put("rsltCd", "S");
		return resultMap;
	}
	
	
	/**
	 * @param key
	 * @param operator
	 * @param comparedValue
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public JSONObject setArgsToJSONObj(String key, String operator, Object comparedValue) throws Exception {
		JSONObject outerObj = new JSONObject();
		JSONObject innerObj = new JSONObject();
		innerObj.put(operator, comparedValue);
		outerObj.put(key, innerObj);
		
		return outerObj;
	}
	
	
	/**
	 * FDS검증
	 * @param paramMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> chkFdsClctIfo(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> params = new HashMap<>();
		Map<String, Object> resultMap = new HashMap<>();
		
		params.put("chaincode"	, PropertiesUtil.getString("CHAINCODE_FDSCLCT"));
		params.put("funcName"	, PropertiesUtil.getString("FUNC_FDSCLCT_CHECK"));
		params.put("channel"	,  PropertiesUtil.getString("CHANNEL_NAME_CH1"));

		JSONObject args = new JSONObject();
		args.put("afltstorMembContct", paramMap.get("afltstorMembContct"));
		args.put("vrfcTgtDivCod", paramMap.get("setlReqTypeCod"));
		args.put("uuid", paramMap.get("uuid"));
		args.put("ip", paramMap.get("ip"));
		args.put("os", paramMap.get("os"));
		params.put("args", args.toJSONString());
		
		resultMap = connectService.query(params);	//00:있음 or 99:없음
		printComLog(PropertiesUtil.getString("CHAINCODE_FDSCLCT"), resultMap.get("runTime").toString(), resultMap.get("rsltCd").toString());
		return resultMap;
	}
	
	/**
	 * FDS정보 저장
	 * @param paramMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> crtFdsClctIfo(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> params = new HashMap<>();
		Map<String, Object> resultMap = new HashMap<>();
		
		params.put("chaincode", PropertiesUtil.getString("CHAINCODE_FDSCLCT"));
		params.put("funcName", PropertiesUtil.getString("FUNC_FDSCLCT_CREATE"));
		params.put("channel",  PropertiesUtil.getString("CHANNEL_NAME_CH1"));
		
		JSONObject args = new JSONObject();
		args.put("vrfcIfo", paramMap.get("vrfcIfo"));
		args.put("vrfcTgtDivCod", paramMap.get("setlReqTypeCod"));
		args.put("fstClctDay", paramMap.get("regDt"));
//		args.put("nrstClctDay", paramMap.get("nrstClctDay"));
//		args.put("clctTotcnt", paramMap.get("clctTotcnt"));
		params.put("args", args.toJSONString());
		
		resultMap = connectService.invoke(params);
		printComLog(PropertiesUtil.getString("CHAINCODE_FDSCLCT"), resultMap.get("runTime").toString(), resultMap.get("rsltCd").toString());
		if(!"00".equals(resultMap.get("rsltCd"))) {
			resultMap.put("rsltCd", "E0001");
			resultMap.put("rsltMsg", "저장실패");
			return resultMap;
		}
		
		return resultMap;
	}
	
	/**
	 * 결제요청정보 저장
	 * @param paramMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> crtSetlReqIfo(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> params = new HashMap<>();
		Map<String, Object> resultMap = new HashMap<>();
		
		params.put("chaincode", PropertiesUtil.getString("CHAINCODE_SETLREQ"));
		params.put("funcName", PropertiesUtil.getString("FUNC_SETLREQ_CREATE"));
		params.put("channel",  PropertiesUtil.getString("CHANNEL_NAME_CH1"));
		
		JSONObject args = new JSONObject();
		args.put("setlTxId", paramMap.get("setlTxId"));
		args.put("afltstorRcgntKey", paramMap.get("afltstorRcgntKey"));
		args.put("afltstorNum", paramMap.get("afltstorNum"));
		args.put("trscNum", paramMap.get("trscNum"));
		args.put("reqDtm", paramMap.get("reqDtm"));
		args.put("itlmMms", paramMap.get("itlmMms"));
		args.put("curcyCod", paramMap.get("curcyCod"));
		args.put("setlReqAmot", paramMap.get("setlReqAmot"));
		args.put("prdIfo", paramMap.get("prdIfo"));
		args.put("afltstorMembContct", paramMap.get("afltstorMembContct1"));
		params.put("args", args.toJSONString());
		
		resultMap = connectService.invoke(params);
		printComLog(PropertiesUtil.getString("CHAINCODE_SETLREQ"), resultMap.get("runTime").toString(), resultMap.get("rsltCd").toString());
		if(!"00".equals(resultMap.get("rsltCd"))) {
			resultMap.put("rsltCd", "E0001");
			resultMap.put("rsltMsg", "저장실패");
			return resultMap;
		}
		
		return resultMap;
	}
	

	/**
	 * 결제수단인증정보 저장
	 * @param paramMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> crtSetlMeanCertIfo(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> params = new HashMap<>();
		Map<String, Object> resultMap = new HashMap<>();
		
		params.put("chaincode", PropertiesUtil.getString("CHAINCODE_SETLMEANCERT"));
		params.put("funcName", PropertiesUtil.getString("FUNC_SETLMEANCERT_CREATE"));
		params.put("channel",  PropertiesUtil.getString("CHANNEL_NAME_CH1"));
		
		JSONObject args = new JSONObject();
		args.put("setlTxId", paramMap.get("setlTxId"));
		args.put("otc", paramMap.get("otc"));
		args.put("otcCreDivCod", paramMap.get("otcCreDivCod"));
		args.put("crdId", paramMap.get("crdId"));
		args.put("custNum", paramMap.get("custNum"));
		params.put("args", args.toJSONString());
		
		resultMap = connectService.invoke(params);
		printComLog(PropertiesUtil.getString("CHAINCODE_SETLMEANCERT"), resultMap.get("runTime").toString(), resultMap.get("rsltCd").toString());
		if(!"00".equals(resultMap.get("rsltCd"))) {
			resultMap.put("rsltCd", "E0001");
			resultMap.put("rsltMsg", "저장실패");
			return resultMap;
		}
		
		return resultMap;
	}
	
	/**
	 * 승인정보 검증
	 * @param paramMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> chkApvIfo(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> params = new HashMap<>();
		Map<String, Object> resultMap = new HashMap<>();
		
		params.put("chaincode", PropertiesUtil.getString("CHAINCODE_APV"));
		params.put("funcName", PropertiesUtil.getString("FUNC_APV_CHECK"));
		params.put("channel",  PropertiesUtil.getString("CHANNEL_NAME_CH1"));

		JSONArray jsonArray = new JSONArray();
		jsonArray.add(setArgsToJSONObj("apvNum", PropertiesUtil.getString("EQ"), paramMap.get("apvNum")));
		jsonArray.add(setArgsToJSONObj("trscNum", PropertiesUtil.getString("EQ"), paramMap.get("trscNum")));
		jsonArray.add(setArgsToJSONObj("apvAmot", PropertiesUtil.getString("EQ"), paramMap.get("apvAmot")));
		jsonArray.add(setArgsToJSONObj("stCod", PropertiesUtil.getString("EQ"), "0"));
		
		JSONObject wrapObjSel = new JSONObject();
		wrapObjSel.put(PropertiesUtil.getString("AND"), jsonArray);
		
		JSONObject wrapObj = new JSONObject();
		wrapObj.put("selector", wrapObjSel);
		params.put("args", wrapObj.toJSONString());
		
		resultMap = connectService.query(params);
		printComLog(PropertiesUtil.getString("CHAINCODE_APV"), resultMap.get("runTime").toString(), resultMap.get("rsltCd").toString());
		if(!"00".equals(resultMap.get("rsltCd"))) {
			resultMap.put("rsltCd", "E");
			resultMap.put("rsltMsg", "조회실패");
			return resultMap;
		}
		return resultMap;
	}
	

	/**
	 * 승인정보 저장
	 * @param paramMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> crtApvIfo(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> params = new HashMap<>();
		Map<String, Object> resultMap = new HashMap<>();
		
		params.put("chaincode", PropertiesUtil.getString("CHAINCODE_APV"));
		params.put("funcName", PropertiesUtil.getString("FUNC_APV_CREATE"));
		params.put("channel",  PropertiesUtil.getString("CHANNEL_NAME_CH1"));
		
		JSONObject args = new JSONObject();
		args.put("setlTxId", paramMap.get("setlTxId"));
		args.put("apvNum", paramMap.get("apvNum"));
		args.put("apvDivCod", paramMap.get("apvDivCod"));
		args.put("afltstorNum", paramMap.get("afltstorNum"));
		args.put("trscNum", paramMap.get("trscNum"));
		args.put("trscUniqNum", paramMap.get("trscUniqNum"));
		args.put("crdId", paramMap.get("crdId"));
		args.put("itlmMms", paramMap.get("itlmMms"));
		args.put("curcyCod", paramMap.get("curcyCod"));
		args.put("apvAmot", paramMap.get("setlReqAmot"));
		args.put("balIfo", paramMap.get("balIfo"));
		args.put("crdtCrdNum", paramMap.get("crdtCrdNum1"));
		args.put("otc", paramMap.get("otc"));
		args.put("apvDtm", paramMap.get("apvDtm"));
		args.put("vrfcIfo", paramMap.get("vrfcIfo"));
		args.put("regDt", paramMap.get("regDt"));
		params.put("args", args.toJSONString());
		
		resultMap = connectService.invoke(params);
		printComLog(PropertiesUtil.getString("CHAINCODE_APV"), resultMap.get("runTime").toString(), resultMap.get("rsltCd").toString());
		if(!"00".equals(resultMap.get("rsltCd"))) {
			resultMap.put("rsltCd", "E0001");
			resultMap.put("rsltMsg", "저장실패");
			return resultMap;
		}
		
		return resultMap;
	}
	
	/**
	 * 취소승인정보 저장
	 * @param paramMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> crtCanApvIfo(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> params = new HashMap<>();
		Map<String, Object> resultMap = new HashMap<>();
		
		params.put("chaincode", PropertiesUtil.getString("CHAINCODE_APV"));
		params.put("funcName", PropertiesUtil.getString("FUNC_CANAPV_CREATE"));
		params.put("channel",  PropertiesUtil.getString("CHANNEL_NAME_CH1"));
		
		JSONObject args = new JSONObject();
		args.put("setlTxId", paramMap.get("setlTxId"));
		args.put("cnntTxId", paramMap.get("cnntTxId"));
		args.put("apvNum", paramMap.get("apvNum"));
		args.put("apvDivCod", paramMap.get("apvDivCod"));
		args.put("afltstorNum", paramMap.get("afltstorNum"));
		args.put("trscNum", paramMap.get("trscNum"));
		args.put("trscUniqNum", paramMap.get("trscUniqNum"));
		args.put("crdId", paramMap.get("crdId"));
		args.put("itlmMms", paramMap.get("itlmMms"));
		args.put("curcyCod", paramMap.get("curcyCod"));
		args.put("apvAmot", paramMap.get("apvAmot"));
		args.put("balIfo", paramMap.get("balIfo"));
		args.put("crdtCrdNum", paramMap.get("crdtCrdNum"));
		args.put("otc", paramMap.get("otc"));
		args.put("apvDtm", paramMap.get("apvDtm"));
		args.put("regDt", paramMap.get("regDt"));
		params.put("args", args.toJSONString());
		
		resultMap = connectService.invoke(params);
		printComLog(PropertiesUtil.getString("CHAINCODE_APV"), resultMap.get("runTime").toString(), resultMap.get("rsltCd").toString());
		if(!"00".equals(resultMap.get("rsltCd"))) {
			resultMap.put("rsltCd", "E0001");
			resultMap.put("rsltMsg", "저장실패");
			return resultMap;
		}
		
		return resultMap;
	}
	
	/**
	 * 가맹점 등록
	 * @param paramMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> crtAfltstorIfo(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> params = new HashMap<>();
		Map<String, Object> resultMap = new HashMap<>();
		
		params.put("chaincode", PropertiesUtil.getString("CHAINCODE_AFTLSTOR"));
		params.put("funcName", PropertiesUtil.getString("FUNC_AFTLSTOR_CREATE"));
		params.put("channel",  PropertiesUtil.getString("CHANNEL_NAME_CH1"));
		
		Map<String, Object> rsaKeyMap = RSA.requestKey();
		
//		rsaKeyMap.put("publicKey", "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAkK6NN+HHb6pVF2YdBu5FLFw2D+KFJaeFlYOVm59x195YKNC0y9e409jDUnNKMa5zZ/rjoDdt2y+pyksXKY7Zfp1iu3b7fm+jKdMG/Dy7H+JlO7WLLTqwkG7mCxouF4HLbBoKh6IXZF5Ludx0crxK1REW5xMMll3FeRdm12AxMmhONl9uEQ+j4TbxtEuOpdcJGz2yKyezxkXn7cNdqEem+GD8n6AjxuqjonlGTOCLOoIAbyMCZx2Y5SUb49kUH/AgbjNpaWmQyKtKvOj/6dDWJieJMwHHAf2GdjCM1hiEILxJfHutC9k/BLO6ASWQRFU4WwM/WJTV4gwTrbw4U+pWBwIDAQAB");
//		rsaKeyMap.put("privateKey", "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCQro034cdvqlUXZh0G7kUsXDYP4oUlp4WVg5Wbn3HX3lgo0LTL17jT2MNSc0oxrnNn+uOgN23bL6nKSxcpjtl+nWK7dvt+b6Mp0wb8PLsf4mU7tYstOrCQbuYLGi4XgctsGgqHohdkXku53HRyvErVERbnEwyWXcV5F2bXYDEyaE42X24RD6PhNvG0S46l1wkbPbIrJ7PGReftw12oR6b4YPyfoCPG6qOieUZM4Is6ggBvIwJnHZjlJRvj2RQf8CBuM2lpaZDIq0q86P/p0NYmJ4kzAccB/YZ2MIzWGIQgvEl8e60L2T8Es7oBJZBEVThbAz9YlNXiDBOtvDhT6lYHAgMBAAECggEAdSei2nlM0TMQLB5XquENylnwvPQj9NWt6d/fKWD3AmHc6U/21okb9Q81OnJ4xboJYU1bPMUlg7MYveIII3K00z4GUN6Lz1n+uUNUCLP0omDksHDU/g7kIlBqntrYL9i+LA76L9IlNvTURPAqqy2lOtrXvM1ECA5udRtp/0S5EaMBDvS6/ZRclRQ/meWauhbUQRQUXrsIWqFJ3gGoKO3+Z8hCQ7tGrpO0qb4CwEvZOfeK8qdojB1wgN3v/dMVHWVYNzHKWTE+pBbwL/ghRDTVmBMq3UsFb7qYKuSxJg5jTbmUzvNMfP/HdW3UAvd9nhZFe+1u/CT4DnsZTiKlXrazgQKBgQDhF2VjfMEMBAmTozqrCE2nd6Un7XmawOrtv60sBbJ9VYlAAJCiNczYFQB9MpkAfomnhPoAkA7zziI14GpoAXxC4L35ndkdTnBtu7P7tQZKgkafC8H1hyJnwOnZiFS3dqLaMxxzXMMjAQY7Re9peVq6XLIxds2v19CMvaHpnFl99wKBgQCkjIfgdt1Lxd0XwtCcdAYAwRkLWy/AgI1qI4i5d2g5uGwOJZ+Y1DJyDQFmGmH4WJeN3yF8UGZwTeCa+PCT/ySDNMPAcCRMPgTQWhBKSaPZKsXxqEDT6s+onAN5ExGTOD+qyvuQb/LViiBCvHfaAanz5pGHA701zrVc8aCcKiUkcQKBgQCPxITCtZUL2qxPOyumwCfxNe8sAHbyTBQBhLLT8lOdz/tZAMNgh+JthYSJcr7c3PWJJNsVbu3N9TROmyb7hS9b+/X7MEHTgR04SXJc67O39VusDDN3Yjesc0Ap6/TINaMwoQKkAHS2r9nOFA4lnRhA/iHmjA42KS8QJbL75hwW4wKBgESm+T3WeefWSrNSLZVI9wQzjE5nohmvyNCCuNWgkxSZ9ENGwTqTNeTNtkqyMTPbSof/k0n5hXPPmikuObmVltdDGXTdlNdWfM2s9eT4qmq95M45Q6rm6fLP/Mb0BL4mlrGcdK7Ddk5PjbwqQ9rqHM1M4mSxU6zybEq16SuejdaRAoGATxl8WeDnPaAoG5dkcPzbEV0IPrKrP0hMH6jxrGkYTPzysGseI3eOnNMe9RZ66K9bfTuobjAwNRsfP0p02o7hPLzU2dZqwGKwdDgdnGalnbnVVdXRqEofRJ3g4U5vNQgmZv3Yy0WZU/hw2LVogNKeP3E/3mwK5K+twSp5TSfMMx0=");
		
		JSONObject args = new JSONObject();
		args.put("afltstorRcgntKey", getAfltstorRcgntKey());
		args.put("afltstorNum", paramMap.get("afltstorNum"));
		args.put("afltstorBcKey", rsaKeyMap.get("privateKey"));
		args.put("afltstorPbKey", rsaKeyMap.get("publicKey"));
		args.put("bzregno", paramMap.get("bzregno"));
		params.put("args", args.toJSONString());
		
		resultMap = connectService.invoke(params);
		System.out.println("-- crtAfltstorIfo res : " + resultMap);
		if(!"00".equals(resultMap.get("rsltCd"))) {
			resultMap.put("rsltCd", ResultCode.E);
			resultMap.put("rsltMsg", "등록실패");
			return resultMap;
		}
		resultMap.put("rsltCd", ResultCode.S);
		return resultMap;
	}
	
	/**
	 * 가맹점 수정
	 * @param paramMap
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> uptAfltstorIfo(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> params = new HashMap<>();
		Map<String, Object> resultMap = new HashMap<>();

		params.put("chaincode", PropertiesUtil.getString("CHAINCODE_AFTLSTOR"));
		params.put("funcName", PropertiesUtil.getString("FUNC_AFTLSTOR_UPDATE"));
		params.put("channel",  PropertiesUtil.getString("CHANNEL_NAME_CH1"));
		
		JSONObject args = new JSONObject();
		args.put("afltstorRcgntKey", paramMap.get("afltstorRcgntKey"));
		args.put("afltstorNum", paramMap.get("afltstorNum"));
		args.put("bzregno", paramMap.get("bzregno"));
		args.put("useYn", paramMap.get("useYn"));
		params.put("args", args.toJSONString());
		
		resultMap = connectService.invoke(params);
		System.out.println("-- uptAfltstorIfo res : " + resultMap);
		if(!"00".equals(resultMap.get("rsltCd"))) {
			resultMap.put("rsltCd", ResultCode.E);
			resultMap.put("rsltMsg", "수정실패");
			return resultMap;
		}
		resultMap.put("rsltCd", ResultCode.S);
		return resultMap;
	}
	
	public Map<String, Object> getSetlTxId(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> params = new HashMap<String, Object>();
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		params.put("chaincode", PropertiesUtil.getString("CHAINCODE_APV"));
		params.put("funcName", PropertiesUtil.getString("FUNC_APV_SELECT"));
		params.put("channel",  PropertiesUtil.getString("CHANNEL_NAME_CH1"));
		
		JSONObject args = new JSONObject();
		args.put("setlTxId", paramMap.get("setlTxId"));
		params.put("args", args.toJSONString());
		
		resultMap = connectService.query(params);
		System.out.println("-- getSetlTxId res : " + resultMap);
		if(!"00".equals(resultMap.get("rsltCd"))) {
			resultMap.putAll(ErrorException(ResultCode.E1001));
			return resultMap;
		}
		resultMap.put("rsltCd", ResultCode.S);
		return resultMap;
	}
	
	private String getAfltstorRcgntKey() {
		return HASH.sha3String(RandomStringUtils.randomAlphanumeric(32));
	}
	
	private Map<String, Object> ErrorException(String code) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("rsltCd", ResultCode.E);
		resultMap.put("rsltMsg", MessageUtil.getMessage(code));
		logger.info("Error Code : " + code + ", Error Message : " + MessageUtil.getMessage(code));
		return resultMap;
	}
	
	private void printComLog(String chaincodeName, String time, String rsltCd) {
		logger.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS Z").format(new Date()) +" ["+ chaincodeName +","+ PropertiesUtil.getString("REGION") +","+ PropertiesUtil.getString("GRADE") +"] " + time +" " + rsltCd);
	}
	
}
