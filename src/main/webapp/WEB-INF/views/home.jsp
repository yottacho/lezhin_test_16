<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page session="false"%>
<html>
<head>
<meta charset="utf-8">
<title>Users List</title>

</head>
<body>
  <h1>테스트 사용자 목록</h1>

  <p>REST API 용 테스트 사용자 목록입니다.</p>

  <table border="1">
    <tr>
      <td>userid</td>
      <td>name</td>
      <td>showname</td>
      <td>api access key</td>
    </tr>
    <c:forEach var="item" items="${userList}">
      <tr>
        <td>${item.userid}</td>
        <td>${item.name}</td>
        <td>${item.showname}</td>
        <td>${item.api_accesskey}</td>
      </tr>
    </c:forEach>
  </table>

  <p><a href="adduser.do">Add user</a></p>

</body>
</html>
