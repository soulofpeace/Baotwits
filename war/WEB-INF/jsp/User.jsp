<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isELIgnored="false"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title>User Tweets</title>
    <script type="text/javascript" language="javascript" src="/User/User.nocache.js"></script>
    <link type="text/css" rel="stylesheet" href="/Baotwits.css">
  </head>

  <body class="bodyBg">
    <iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1' style="position:absolute;width:0;height:0;border:0"></iframe>
     <input type="hidden" id="userId" value="<c:out value='${userId}'/>"/>
     <input type="hidden" id="debug" value="false"/>
	<Div id="displayTwits" style="width:700px;"></div>
  </body>
</html>