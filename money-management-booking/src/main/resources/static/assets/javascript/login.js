
function report(error, turn) {
  if (error !== null) {
    if (error == "login-fail") {
      if(turn !== null){
        if (turn > 0) {
          $('#report').html("Warning: Email or password not correct!! </br> Your account has been locked due to "+turn+" failed attempts");
  
        }else{
          $('#report').html(" Warning: Your account has been locked after 24 hours");
        }
      }
     
    }
    else if (error == "disabled") {
      $('#report').html(" Warning: Your account has been disabled!!");
    }else if(error == 'null'){
      $('#report').html(" Warning: Data cannot be null!!");
    }else if(error == 'captcha'){
      $('#report').html(" Warning: Captcha invalid");
    }
    else{
      $('#report').html("Warning: Email or password not correct!!");
    }
    $('.alert').addClass("show");
    $('.alert').removeClass("hide");
    $('.alert').addClass("showAlert");
    setTimeout(function () {
      $('.alert').removeClass("show");
      $('.alert').addClass("hide");
    }, 5000);
    setTimeout(function () {
      $('.alert').css("display","none");
    }, 6000);
    $('.alert_close-btn').click(function () {
      $('.alert').removeClass("show");
      $('.alert').addClass("hide");
    });
  }

}