<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Login" />
<%@ include file="/WEB-INF/views/header.jspf" %>

<div class="form-card">
  <h2>Login</h2>
  <c:if test="${param.registered == '1'}"><div class="alert success">Registration successful! Please log in.</div></c:if>
  <c:if test="${not empty error}"><div class="alert error"><c:out value="${error}"/></div></c:if>

  <form action="${ctx}/login" method="post">
    <label for="username">Email (or admin username)</label>
    <input type="text" id="username" name="username" required autofocus>
    <label for="password">Password</label>
    <input type="password" id="password" name="password" required>
    <button type="submit" class="btn">Login</button>
  </form>
  <p class="muted">New here? <a href="${ctx}/register">Create an account</a></p>
</div>

<%@ include file="/WEB-INF/views/footer.jspf" %>
