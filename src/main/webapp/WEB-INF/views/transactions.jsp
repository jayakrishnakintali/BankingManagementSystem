<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Transaction History" />
<%@ include file="/WEB-INF/views/header.jspf" %>

<h2>Transaction History</h2>
<p class="muted">Account <strong><c:out value="${account.accountNumber}"/></strong> &middot; latest 200 transactions</p>

<div class="table-wrap">
<table class="data">
  <thead><tr><th>ID</th><th>Type</th><th>Amount (&#8377;)</th><th>Balance after (&#8377;)</th><th>Date &amp; time</th><th>Status</th><th>Description</th></tr></thead>
  <tbody>
  <c:forEach var="t" items="${transactions}">
    <c:set var="credit" value="${t.transactionType == 'DEPOSIT' or t.transactionType == 'TRANSFER_CREDIT'}"/>
    <tr>
      <td>${t.transactionId}</td>
      <td><span class="badge type-${t.transactionType}"><c:out value="${t.transactionType}"/></span></td>
      <td class="${credit ? 'pos' : 'neg'}">${credit ? '+' : '-'}<fmt:formatNumber value="${t.amount}" minFractionDigits="2" maxFractionDigits="2"/></td>
      <td><fmt:formatNumber value="${t.balanceAfter}" minFractionDigits="2" maxFractionDigits="2"/></td>
      <td><fmt:formatDate value="${t.transactionDate}" pattern="dd MMM yyyy, HH:mm:ss"/></td>
      <td><span class="badge status-${t.status}"><c:out value="${t.status}"/></span></td>
      <td><c:out value="${t.description}"/></td>
    </tr>
  </c:forEach>
  <c:if test="${empty transactions}"><tr><td colspan="7" class="muted">No transactions yet.</td></tr></c:if>
  </tbody>
</table>
</div>

<%@ include file="/WEB-INF/views/footer.jspf" %>
