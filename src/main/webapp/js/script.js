// Basic client-side validation. The servlets validate again on the server.
(function () {
  function show(box, msg) { box.textContent = msg; box.classList.remove('hidden'); }

  // Amount forms (deposit / withdraw / transfer)
  document.querySelectorAll('.money-form').forEach(function (form) {
    form.addEventListener('submit', function (e) {
      var box = form.querySelector('.clientError');
      var amount = form.querySelector('[name=amount]').value.trim();
      var acc = form.querySelector('[name=toAccount]');
      var msg = '';
      if (acc && !/^\d{12}$/.test(acc.value.trim())) msg = 'Receiver account number must be 12 digits.';
      else if (!/^\d+(\.\d{1,2})?$/.test(amount) || parseFloat(amount) <= 0) msg = 'Enter a positive amount (max 2 decimals).';
      if (msg) { e.preventDefault(); show(box, msg); }
    });
  });

  // Registration form
  var reg = document.getElementById('registerForm');
  if (reg) {
    reg.addEventListener('submit', function (e) {
      var box = document.getElementById('clientError'), v = function (n) { return reg.elements[n].value.trim(); };
      var msg = '';
      if (v('fullName').length < 2) msg = 'Please enter your full name.';
      else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v('email'))) msg = 'Please enter a valid email.';
      else if (!/^\d{10}$/.test(v('phone'))) msg = 'Phone number must be exactly 10 digits.';
      else if (v('address').length < 5) msg = 'Please enter your address.';
      else if (reg.elements.password.value.length < 8 || !/[A-Za-z]/.test(reg.elements.password.value) || !/\d/.test(reg.elements.password.value))
        msg = 'Password needs 8+ characters with letters and digits.';
      else if (reg.elements.password.value !== reg.elements.confirmPassword.value) msg = 'Passwords do not match.';
      if (msg) { e.preventDefault(); show(box, msg); }
    });
  }
})();
