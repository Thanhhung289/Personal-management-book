
function confirmOTP() {
  var otpInput = document.getElementById("otp").value;
  $.ajax({
    url: "/otps/confirm",
    type: "POST",
    data: {
      otp: otpInput
    },
    success: function (data) {
      if (data !== null && data !== "") {
        var report = data;
        if (report.includes('login')) {
          window.location = "/login";
        } else if (report.includes('resetPassword')) {
          window.location = "/forgot-password";
        } else if (report.includes('logout')) {
          window.location = "/logout";
        } else {
          openPopup(data);
        }
      }
    },

  });
}


function sendOTP() {
  $.ajax({
    url: "/otps/sendOTP",
    type: "GET",
    error: function (xhr, status, error) {

      var errorMessage = xhr.responseText;

      document.getElementById('body').innerHTML = errorMessage
    }
  });

}
var timer;
var countdown;

function startTimer() {
  sendOTP()
  var sendButton = document.getElementById('sendOTP');
  sendButton.disabled = true;

  var message = document.getElementById('otp-message');
  message.style.display = 'block';

  countdown = 60;
  var countdownDisplay = document.getElementById('countdown');
  countdownDisplay.textContent = countdown;

  timer = setInterval(function () {
    countdown--;
    countdownDisplay.textContent = countdown;

    if (countdown <= 0) {
      clearInterval(timer);
      sendButton.disabled = false;
      message.style.display = 'none';
    }
  }, 1000);
}
var popupCount = 0.8;

function openPopup(text) {
  var newPopup = document.createElement("div");
  newPopup.className = "report_popup";
  newPopup.innerHTML = "<i class = 'fa-solid fa-bell' style = 'display:inline; margin-right:12px; font-size:20px'></i>" + "<p style='display:inline;'>" + text + "</p>";
  document.body.appendChild(newPopup);
  setTimeout(function () {
    newPopup.style.display = "block";
    newPopup.style.top = (popupCount * (newPopup.offsetHeight + 10)) + "px";
    newPopup.style.right = "0";
    popupCount++;

    setTimeout(function () {
      newPopup.classList.add("hide_report");
    }, 4000);
    setTimeout(function () {
      setTimeout(function () {
        newPopup.remove();
        popupCount--;
      }, 100);
    }, 4900);

    newPopup.addEventListener("click", function () {
      setTimeout(function () {
        newPopup.remove();
        popupCount--;
      }, 100);
    });
    newPopup.classList.add("show_report");


  }, 100);

}