<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page isELIgnored="false"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title>CommentWidget</title>
    <script type="text/javascript" language="javascript" src="../../baotwits/baotwits.nocache.js"></script>
    <script type="text/javascript" language="javascript" src="../../com.appspot.baotwits.CommentWidget/com.appspot.baotwits.CommentWidget.nocache.js"></script>
    <script type="text/javascript" language="javascript" src="../../User/User.nocache.js"></script>
  </head>

  <body>
    <iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1' style="position:absolute;width:0;height:0;border:0"></iframe>
    <h3><c:out value='${userId}'/> Tweets </h3>
     <input type="hidden" id="userId" value="<c:out value='${userId}'/>"/>
  </body>
</html>
