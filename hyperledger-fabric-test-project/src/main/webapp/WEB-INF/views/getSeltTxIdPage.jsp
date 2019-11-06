<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<html>
<head>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
<script type="text/javascript">
	var path = location.origin;

	function getSetlTxId() {
		$.ajax({
			type : "POST",
			url : path+"/getSetlTxId.json",
			data : {
				setlTxId : $("#setlTxIdInput").val()
			},
			dataType : "json",
			success : function(response) {
				if(response.rsltCd != "S") {
					alert("["+response.rsltMsg+"] 조회에 실패하였습니다.")
				} else {
					setData(response);
				}
			},
			error : function (xhr, status, error) {
				
			}
		})	
	}
	
	function setData(data) {
		for ( key in data ) {
			$("#"+key).val(data[key]);
		}
	}
</script>
</head>
<body>
<div>
	<span>결제 TX ID : </span>
	<input type="text" name="setlTxIdInput" id="setlTxIdInput"/>
	<a href="javascript:getSetlTxId()">조회</a>
</div>
<br/>
<div>
	<span>결제TXID : </span>
	<input type="text" name="setlTxId" id="setlTxId"/>
</div>
<div>
	<span>승인번호 : </span>
	<input type="text" name="apvNum" id="apvNum"/>
</div>
<div>
	<span>승인구분코드 : </span>
	<input type="text" name="apvDivCod" id="apvDivCod"/>
</div>
<div>
	<span>가맹점번호 : </span>
	<input type="text" name="afltstorNum" id="afltstorNum"/>
</div>
<div>
	<span>거래번호 : </span>
	<input type="text" name="trscNum" id="trscNum"/>
</div>
<div>
	<span>거래고유번호 : </span>
	<input type="text" name="trscUniqNum" id="trscUniqNum"/>
</div>
<div>
	<span>카드ID : </span>
	<input type="text" name="crdId" id="crdId"/>
</div>
<div>
	<span>할부개월 : </span>
	<input type="text" name="itlmMms" id="itlmMms"/>
</div>
<div>
	<span>통화코드 : </span>
	<input type="text" name="curcyCod" id="curcyCod"/>
</div>
<div>
	<span>승인금액 : </span>
	<input type="text" name="apvAmot" id="apvAmot"/>
</div>
<div>
	<span>신용카드번호 : </span>
	<input type="text" name="crdtCrdNum" id="crdtCrdNum"/>
</div>
<div>
	<span>OTC : </span>
	<input type="text" name="otc" id="otc"/>
</div>
<div>
	<span>승인일시 : </span>
	<input type="text" name="apvDtm" id="apvDtm"/>
</div>
<div>
	<span>연관TXID : </span>
	<input type="text" name="cnntTxId" id="cnntTxId"/>
</div>
<div>
	<span>취소일시 : </span>
	<input type="text" name="canDtm" id="canDtm"/>
</div>
<div>
	<span>등록일자 : </span>
	<input type="text" name="regDt" id="regDt"/>
</div>
</body>
</html>