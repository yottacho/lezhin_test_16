<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page session="false"%>
<html>
<head>
<meta charset="utf-8">
<title>Add user</title>

</head>
<body>
  <h1>테스트 사용자 등록</h1>

  <p>REST API 용 테스트 사용자를 등록합니다.</p>

  <form method="post" action="adduser.do">
  <table>
    <tr>
      <td>*사용자ID</td>
      <td><input type="text" name="name" maxlength="20"> (영문숫자,20자)</td>
    </tr>
    <tr>
      <td>*표시할 이름</td>
      <td><input type="text" name="showname" maxlength="50"> (50자)</td>
    </tr>
  </table>
  <input type="submit">
  </form>

</body>
</html>
