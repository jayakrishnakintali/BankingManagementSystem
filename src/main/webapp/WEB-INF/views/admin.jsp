<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Admin Dashboard" />
<%@ include file="/WEB-INF/views/header.jspf" %>

<h2>Administrator Dashboard <span class="badge status-ACTIVE">read-only</span></h2>

<div class="grid3">
  <div class="stat"><span>Registered customers</span><strong>${customerCount}</strong></div>
  <div class="stat"><span>Total accounts</span><strong>${accountCount}</strong></div>
  <div class="stat"><span>Total transactions</span><strong>${transactionCount}</strong></div>
</div>

<h3>Customers</h3>
<div class="table-wrap"><table class="data">
  <thead><tr><th>ID</th><th>Name</th><th>Email</th><th>Phone</th><th>Address</th><th>Registered</th></tr></thead>
  <tbody>
  <c:forEach var="cu" items="${customers}">
    <tr><td>${cu.customerId}</td><td><c:out value="${cu.fullName}"/></td><td><c:out value="${cu.email}"/></td>
        <td><c:out value="${cu.phone}"/></td><td><c:out value="${cu.address}"/></td>
        <td><fmt:formatDate value="${cu.createdAt}" pattern="dd MMM yyyy"/></td></tr>
  </c:forEach>
  </tbody>
</table></div>

<h3>Accounts</h3>
<div class="table-wrap"><table class="data">
  <thead><tr><th>ID</th><th>Account no.</th><th>Owner</th><th>Type</th><th>Balance (&#8377;)</th><th>Status</th><th>Branch</th></tr></thead>
  <tbody>
  <c:forEach var="a" items="${accounts}">
    <tr><td>${a.account_id}</td><td><c:out value="${a.account_number}"/></td><td><c:out value="${a.full_name}"/></td>
        <td><c:out value="${a.account_type}"/></td>
        <td><fmt:formatNumber value="${a.balance}" minFractionDigits="2" maxFractionDigits="2"/></td>
        <td><span class="badge status-${a.status}"><c:out value="${a.status}"/></span></td>
        <td><c:out value="${a.branch_name}"/></td></tr>
  </c:forEach>
  </tbody>
</table></div>

<h3>Transactions (latest 200)</h3>
<div class="table-wrap"><table class="data">
  <thead><tr><th>ID</th><th>Account</th><th>Type</th><th>Amount (&#8377;)</th><th>Balance after</th><th>Reference</th><th>Date</th><th>Status</th></tr></thead>
  <tbody>
  <c:forEach var="t" items="${transactions}">
    <tr><td>${t.transaction_id}</td><td><c:out value="${t.account_number}"/></td>
        <td><span class="badge type-${t.transaction_type}"><c:out value="${t.transaction_type}"/></span></td>
        <td><fmt:formatNumber value="${t.amount}" minFractionDigits="2" maxFractionDigits="2"/></td>
        <td><fmt:formatNumber value="${t.balance_after}" minFractionDigits="2" maxFractionDigits="2"/></td>
        <td><c:out value="${t.reference_account}"/></td>
        <td><fmt:formatDate value="${t.transaction_date}" pattern="dd MMM yyyy, HH:mm:ss"/></td>
        <td><c:out value="${t.status}"/></td></tr>
  </c:forEach>
  <c:if test="${empty transactions}"><tr><td colspan="8" class="muted">No transactions yet.</td></tr></c:if>
  </tbody>
</table></div>

<h3>Audit logs (latest 200)</h3>
<div class="table-wrap"><table class="data">
  <thead><tr><th>ID</th><th>Account</th><th>Action</th><th>Amount (&#8377;)</th><th>Old balance</th><th>New balance</th><th>Description</th><th>Time</th></tr></thead>
  <tbody>
  <c:forEach var="l" items="${auditLogs}">
    <tr><td>${l.audit_id}</td><td><c:out value="${l.account_number}"/></td><td><c:out value="${l.action_type}"/></td>
        <td><fmt:formatNumber value="${l.amount}" minFractionDigits="2" maxFractionDigits="2"/></td>
        <td><fmt:formatNumber value="${l.old_balance}" minFractionDigits="2" maxFractionDigits="2"/></td>
        <td><fmt:formatNumber value="${l.new_balance}" minFractionDigits="2" maxFractionDigits="2"/></td>
        <td><c:out value="${l.description}"/></td>
        <td><fmt:formatDate value="${l.created_at}" pattern="dd MMM yyyy, HH:mm:ss"/></td></tr>
  </c:forEach>
  <c:if test="${empty auditLogs}"><tr><td colspan="8" class="muted">No audit records yet.</td></tr></c:if>
  </tbody>
</table></div>

<%@ include file="/WEB-INF/views/footer.jspf" %>
