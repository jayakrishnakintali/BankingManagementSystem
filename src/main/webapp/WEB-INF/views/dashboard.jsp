<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Dashboard" />
<%@ include file="/WEB-INF/views/header.jspf" %>

<h2>Welcome, <c:out value="${sessionScope.user.fullName}"/></h2>

<div class="balance-card">
  <span>Current balance</span>
  <strong>&#8377; <fmt:formatNumber value="${account.balance}" minFractionDigits="2" maxFractionDigits="2"/></strong>
</div>

<div class="card">
  <h3>Account details</h3>
  <table class="details">
    <tr><th>Account number</th><td><c:out value="${account.accountNumber}"/></td></tr>
    <tr><th>Account type</th><td><c:out value="${account.accountType}"/></td></tr>
    <tr><th>Status</th><td><span class="badge status-${account.status}"><c:out value="${account.status}"/></span></td></tr>
    <tr><th>Branch</th><td><c:out value="${account.branchName}"/> (IFSC: <c:out value="${account.ifscCode}"/>)</td></tr>
    <tr><th>Opened on</th><td><fmt:formatDate value="${account.createdAt}" pattern="dd MMM yyyy"/></td></tr>
  </table>
</div>

<div class="grid4">
  <a class="tile" href="${ctx}/deposit">&#10133;<span>Deposit</span></a>
  <a class="tile" href="${ctx}/withdraw">&#10134;<span>Withdraw</span></a>
  <a class="tile" href="${ctx}/transfer">&#128257;<span>Transfer</span></a>
  <a class="tile" href="${ctx}/transactions">&#128196;<span>History</span></a>
</div>

<%@ include file="/WEB-INF/views/footer.jspf" %>
