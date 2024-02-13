package com.delbot.danam.global.security.recaptcha;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.delbot.danam.global.config.RecaptchaConfig;

@RestController
@RequestMapping("/recaptcha")
public class RecaptchaController {
  //
  @Value("${recaptcha.secretKey}")
  private String secret;

  @GetMapping("/verify")
  public ResponseEntity<?> verifyMember(String gRecaptchaResponse) {
    RecaptchaConfig.setSecretKey(secret);
    try {
      boolean verifyResult = RecaptchaConfig.verify(gRecaptchaResponse);
      return new ResponseEntity<>(verifyResult, HttpStatus.OK);
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException();
    }
  }
}
