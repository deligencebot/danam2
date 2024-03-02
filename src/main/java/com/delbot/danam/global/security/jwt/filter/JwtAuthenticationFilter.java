package com.delbot.danam.global.security.jwt.filter;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.delbot.danam.global.security.jwt.exception.JwtExceptionCode;
import com.delbot.danam.global.security.jwt.token.JwtAuthenticationToken;
import com.delbot.danam.global.security.jwt.util.JwtProperties;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  //
  private final AuthenticationManager authenticationManager;

  @Override
  public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws AuthenticationException {
    String token = "";
    try {
        token = getToken(request);
      if (StringUtils.hasText(token)) {
        getAuthentication(token);
      }
      filterChain.doFilter(request, response);
    } catch (NullPointerException | IllegalStateException e) {
      handleException(request, JwtExceptionCode.NOT_FOUND_TOKEN, "토큰을 찾을 수 없음", token, e);
    } catch (SecurityException | MalformedJwtException e) {
      handleException(request, JwtExceptionCode.INVALID_TOKEN, "잘못된 토큰", token, e);
    } catch (ExpiredJwtException e) {
      handleException(request, JwtExceptionCode.EXPIRED_TOKEN, "만료된 토큰", token, e);
    } catch (UnsupportedJwtException e) {
      handleException(request, JwtExceptionCode.UNSUPPORTED_TOKEN, "지원되지 않는 토큰", token, e);
    } catch (Exception e) {
      logException("JwtFilter - doFilterInternal()에서 오류 발생!", token, request, e);
      throw new BadCredentialsException("예외 발생");
    }
}

  private void handleException(HttpServletRequest request, JwtExceptionCode errorCode, String logMessage,
                                String token, Exception e) {
      request.setAttribute("exception", errorCode.getCode());
      logException(logMessage, token, request, e);
      throw new BadCredentialsException(errorCode.name().toLowerCase().replace("_", " ") + " 예외 발생");
  }

private void logException(String logMessage, String token, HttpServletRequest request, Exception e) {
    log.error("=============================================");
    log.error(logMessage + " // 토큰 : {}", token);
    log.error("요청 예외 코드 설정 : {}", request.getAttribute("exception"));
    log.error("예외 메시지 : {}", e.getMessage());
    log.error("예외 스택 트레이스 : {");
    e.printStackTrace();
    log.error("}");
    log.error("=============================================");
}

  private void getAuthentication(String token) {
    JwtAuthenticationToken authenticationToken = new JwtAuthenticationToken(token);
    Authentication authenticate = authenticationManager.authenticate(authenticationToken);
    SecurityContextHolder.getContext().setAuthentication(authenticate);
  }

  private String getToken(HttpServletRequest request) {
    String authorization = request.getHeader(JwtProperties.HEADER_STRING);
    if (StringUtils.hasText(authorization) && authorization.startsWith(JwtProperties.TOKEN_PREFIX)) {
      String[] arr = authorization.split(" ");
      return arr[1];
    }
    return null;
  }
}
