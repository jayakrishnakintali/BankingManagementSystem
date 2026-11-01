<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Withdraw Money" />
<%@ include file="/WEB-INF/views/header.jspf" %>

<div class="form-card">
  <h2>Withdraw Money</h2>
  <p class="muted">Account <strong><c:out value="${account.accountNumber}"/></strong> &middot;
     Balance <strong>&#8377; <fmt:formatNumber value="${account.balance}" minFractionDigits="2" maxFractionDigits="2"/></strong></p>
  <c:if test="${not empty message}"><div class="alert success"><c:out value="${message}"/></div></c:if>
  <c:if test="${not empty error}"><div class="alert error"><c:out value="${error}"/></div></c:if>

  <form action="${ctx}/withdraw" method="post" class="money-form" novalidate>
    <label for="amount">Amount (&#8377;)</label>
    <input type="text" id="amount" name="amount" inputmode="decimal" placeholder="e.g. 1000.00" required>
    <div class="alert error hidden clientError"></div>
    <button type="submit" class="btn">Withdraw</button>
  </form>
  <p><a href="${ctx}/dashboard">&larr; Back to dashboard</a></p>
</div>

<%@ include file="/WEB-INF/views/footer.jspf" %>
