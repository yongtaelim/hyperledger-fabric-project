<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<head>
  <title>가맹점 관리</title>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.4.0/js/bootstrap.min.js"></script>
  <script type="text/javascript">
	  var path = location.origin;
		function getAlftstorListIfo() {
			$.ajax({
				type : "POST",
				url : path+"/getAlftstorListIfo.json",
				data : {},
				dataType : "json",
				success : function(response) {
					if(response.rsltCd != "S") {
						alert("["+response.rsltMsg+"] select failed!!")
					} else {
						setData(response);
					}
				},
				error : function (xhr, status, error) {
					
				}
			})
		}
		
		function setData(response) {
			var tbody = $('#tbody'); 
			tbody.empty();
			for ( var index in response.resultData) {
				var data = response.resultData[index].Record;
				var check = data.useYn == 1 ? "checked=\"checked\"" : "";
				tbody.append('<tr>');
				tbody.append('<td><input type="text"  disabled="disabled" value="'+data.afltstorRcgntKey+'"/></td>');
				tbody.append('<td><input type="text"  id="'+data.afltstorRcgntKey+'afltstorNum" value="'+data.afltstorNum+'"/></td>');
				tbody.append('<td><input type="text"  disabled="disabled" value="'+data.afltstorBcKey+'"/></td>');
				tbody.append('<td><input type="text" disabled="disabled" value="'+data.afltstorPbKey+'"/></td>');
				tbody.append('<td><input type="text"  id="'+data.afltstorRcgntKey+'bzregno" value="'+data.bzregno+'"/></td>');
				tbody.append('<td><input type="checkbox" '+check+'  id="'+data.afltstorRcgntKey+'useYn" value="'+data.useYn+'"/></td>');
				tbody.append('<td><a href="javascript:update(\''+data.afltstorRcgntKey+'\')" id="'+data.afltstorRcgntKey+'">수정</a></td>');
				tbody.append('<tr>');
			}    
		}
		
		function update(afltstorRcgntKey) {
			var useYn = $("#"+afltstorRcgntKey+"useYn").is(":checked") == true ? "1" : "0"
			$.ajax({
				type : "POST",
				url : path+"/updateAfltstor.json",
				data : {
					afltstorRcgntKey : afltstorRcgntKey
					,afltstorNum : $("#"+afltstorRcgntKey+"afltstorNum").val()
					,bzregno : $("#"+afltstorRcgntKey+"bzregno").val()
					,useYn : useYn
				},
				dataType : "json",
				success : function(response) {
					if(response.rsltCd != "S") {
						alert("["+response.rsltMsg+"] update failed!!")
					} else {
						alert("update success!!")
						getAlftstorListIfo();
					}
				},
				error : function (xhr, status, error) {
					
				}
			})
		};
		
		function insert() {
			$.ajax({
				type : "POST",
				url : path+"/insertAfltstor.json",
				data : {
					afltstorNum : $("#afltstorNum").val()
					,bzregno : $("#bzregno").val()
				},
				dataType : "json",
				success : function(response) {
					if(response.rsltCd != "S") {
						alert("["+response.rsltMsg+"] insert failed!!");
					} else {
						alert("insert success!!");
						getAlftstorListIfo();
						$("#afltstorNum").val('');
						$("#bzregno").val('');
					}
				},
				error : function (xhr, status, error) {
					
				}
			})
		}
	
		$(document).ready(function() {
			getAlftstorListIfo();
		});
  </script>
</head>
<body>

<div class="container">
  <h2>가맹점 관리</h2>
  <table class="table table-striped">
  	<colgroup>
  		<col width="10%">
  		<col width="35%">
  		<col width="10%">
  		<col width="40%">
  		<col width="5%">
  	</colgroup>
    <thead>
      <tr>
        <th></th>
        <th></th>
        <th></th>
        <th></th>
        <th></th>
      </tr>
    </thead>
      <tr>
        <td>가맹점 번호 : </td>
        <td><input type="text" id="afltstorNum"/></td>
        <td>사업자 번호 : </td>
        <td><input type="text" id="bzregno"/></td>
        <td><a href="javascript:insert();"  >등록</a></td>
      </tr>
  </table>
  <table class="table table-striped">
    <thead>
      <tr>
        <th>가맹점식별키</th>
        <th>가맹점번호</th>
        <th>가맹점BC키</th>
        <th>가맹점공개키</th>
        <th>사업자번호</th>
        <th>사용여부</th>
        <th>비고</th>
      </tr>
    </thead>
    <tbody id="tbody">
      <tr>
        <td><input type="text"  name=""  value=""/></td>
        <td><input type="text"  class="afltstorNum" name="" value=""/></td>
        <td><input type="text"  name="" value=""/></td>
        <td><input type="text"  name="" value=""/></td>
        <td><input type="text" class="bzregno" name="" value=""/></td>
        <td><input type="checkbox" class="useYn" checked="checked" name="" value=""/></td>
        <td><a href="javascript:update('tadsfasdfasdfa')" id="test">수정</a></td>
      </tr>
    </tbody>
  </table>
</div>

</body>
</html>
