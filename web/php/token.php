<?php

  /*
    generates new random token and stores it in the session
  */
  function createNewToken() {
    $token = sha1(mt_rand());
    if(!isset($_SESSION['tokens'])) {
      $_SESSION['tokens'] = array($token => true);
    }
    else {
      $_SESSION['tokens'][$token] = true;
    }
    return $token;
  }

  /*
    check if the token is valid. If the token is valid, remove its validity for next use.
  */
  function tokenIsValid($token) {
    if(!empty($_SESSION['tokens'][$token])) {
      unset($_SESSION['tokens'][$token]);
      return true;
    }
    else {
      return false;
    }
  }
?>
