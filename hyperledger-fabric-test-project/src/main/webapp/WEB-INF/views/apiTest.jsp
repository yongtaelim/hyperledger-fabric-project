<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
// 		alert("${test}");
	});
	
	function setlReqTest() {
		console.log("SetlReq!");
        $.ajax({
            url        :   '<c:url value="setlReqTest.do" />'
           ,type       :   'POST'
           ,dataType   :   'json'
           ,data       :   {}
           ,success    :   function(response) {
        	   console.log(response);
           }
       });

	}
	
	
</script>
</head>
<body>
<h1>
	API-TEST!!! 
</h1>

<P>  The time on the server is ${serverTime}. </P>

<span><a href="#none"onclick="setlReqTest();">setlReqTest</a></span>



</body>
</html>
