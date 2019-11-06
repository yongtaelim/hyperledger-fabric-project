package hyperledger.api.mapper;

import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public interface ApiMapper {
	public Map<String, Object> test(Map<String, Object> paramMap);
	
	
	/* FDS 검증정보 조회 */
	public Map<String, Object> getFdsVrfcInfo();
	
	/* 결제수단OTC발급요청 */
	public Map<String, Object> getSetlMeanOtcIssuInfo(Map<String, Object> paramMap);
	
	/* 결제승인요청 OTC 조회 */
	public Map<String, Object> getSetlApvReqInfo(Map<String, Object> paramMap);
	
	/* 승인번호 시퀀스 조회 */
	public Integer getSeqApvNum(Map<String, Object> paramMap);
	
	/* 취소승인번호 시퀀스 조회 */
	public Integer getSeqCanApvNum(Map<String, Object> paramMap);
	
	/* 거래고유번호 시퀀스 조회 */
	public Integer getSeqTrscUniqNum(Map<String, Object> paramMap);
	
}
