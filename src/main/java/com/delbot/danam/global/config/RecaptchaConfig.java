package com.delbot.danam.global.config;

import java.io.IOException;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.delbot.danam.global.common.exception.GlobalErrorCode;
import com.google.gson.JsonParser;

@Configuration
public class RecaptchaConfig {
  //
  public static final String url = "https://www.google.com/recaptcha/api/siteverify";
  private final static String USER_AGENT = "Mozilla/5.0";

  private static String secret;

  public static void setSecretKey(String key){
    secret = key;
  }

  public static boolean verify(String gRecaptchaResponse) throws IOException {
    if (gRecaptchaResponse == null || gRecaptchaResponse.isBlank()) {
      return false;
    }

    try  {
      RestTemplate restTemplate = new RestTemplate();

      HttpHeaders headers = new HttpHeaders();
      headers.add("User-Agent", USER_AGENT);
      headers.add("Accept-Language", "en-US,en;q=0.5");
      headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

      MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
      body.add("secret", secret);
      body.add("response", gRecaptchaResponse);

      // RestTemplate communication 
      ResponseEntity<String> responseEntity = restTemplate.postForEntity(
              url, 
              new HttpEntity<>(body, headers), 
              String.class);
      
      // get success value
      if (responseEntity.getStatusCode() == HttpStatus.OK) {
        return JsonParser.parseString(responseEntity.getBody())
                .getAsJsonObject()
                .getAsJsonPrimitive("success")
                .getAsBoolean();
      } else {
        return false;
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw GlobalErrorCode.RECAPTCHA_SERVER_ERROR.defaultException();
    } 
  }
}
