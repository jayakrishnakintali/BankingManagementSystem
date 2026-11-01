<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%-- JSP DECLARATION: becomes a field/method of the generated servlet class --%>
<%! private int pageViews = 0;
    private synchronized int nextView() { return ++pageViews; } %>
<c:set var="pageTitle" value="Home" />
<%@ include file="/WEB-INF/views/header.jspf" %>

<section class="hero">
  <h1>Simple, safe online banking</h1>
  <p>Register an account, deposit, withdraw, transfer money and track every transaction.</p>
  <c:choose>
    <c:when test="${empty sessionScope.user}">
      <a class="btn" href="${ctx}/register">Open an account</a>
      <a class="btn btn-outline" href="${ctx}/login">Login</a>
    </c:when>
    <c:otherwise>
      <a class="btn" href="${ctx}/${sessionScope.user.admin ? 'admin' : 'dashboard'}">
        Go to ${sessionScope.user.admin ? 'admin dashboard' : 'my dashboard'}</a>
    </c:otherwise>
  </c:choose>
</section>

<section class="grid3">
  <div class="card"><h3>&#128274; Secure</h3><p>PBKDF2 password hashing, sessions and role-based access.</p></div>
  <div class="card"><h3>&#128184; Transfers</h3><p>Every transfer runs as a single database transaction with rollback.</p></div>
  <div class="card"><h3>&#128221; Audited</h3><p>A database trigger logs every balance change automatically.</p></div>
</section>

<p class="muted small">
  <%-- JSP EXPRESSIONS and a JSP IMPLICIT OBJECT (application) --%>
  Server time: <%= new java.util.Date() %> &middot;
  Page views since server start: <%= nextView() %> &middot;
  Server: <%= application.getServerInfo() %>
</p>

<%@ include file="/WEB-INF/views/footer.jspf" %>
