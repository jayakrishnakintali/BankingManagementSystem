<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Register" />
<%@ include file="/WEB-INF/views/header.jspf" %>

<div class="form-card">
  <h2>Open a new account</h2>
  <c:if test="${not empty error}"><div class="alert error"><c:out value="${error}"/></div></c:if>

  <form action="${ctx}/register" method="post" id="registerForm" novalidate>
    <label for="fullName">Full name</label>
    <input type="text" id="fullName" name="fullName" value="<c:out value='${fullName}'/>" maxlength="100" required>
    <label for="email">Email address</label>
    <input type="email" id="email" name="email" value="<c:out value='${email}'/>" maxlength="120" required>
    <label for="phone">Phone number (10 digits)</label>
    <input type="text" id="phone" name="phone" value="<c:out value='${phone}'/>" maxlength="10" inputmode="numeric" required>
    <label for="address">Address</label>
    <input type="text" id="address" name="address" value="<c:out value='${address}'/>" maxlength="255" required>
    <label for="password">Password (8+ characters, letters and digits)</label>
    <input type="password" id="password" name="password" maxlength="64" required>
    <label for="confirmPassword">Confirm password</label>
    <input type="password" id="confirmPassword" name="confirmPassword" maxlength="64" required>
    <div id="clientError" class="alert error hidden"></div>
    <button type="submit" class="btn">Register</button>
  </form>
  <p class="muted">Already registered? <a href="${ctx}/login">Login</a></p>
</div>

<%@ include file="/WEB-INF/views/footer.jspf" %>
