package com.energy.battery.config.authentication;

import java.io.IOException;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.energy.battery.exceptions.InvalidDataException;

@Component
public class AuthenticationFilter extends OncePerRequestFilter {

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationFilter.class);

  @Autowired
  private SpringUserDetailsService springUserDetailsService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
    Optional<Pair<ApplicationID, String>> authenticationPair =
        springUserDetailsService.getUserFromAuthorizationHeader(request);
    if (authenticationPair.isPresent()) {
      Pair<ApplicationID, String> userAndToken = authenticationPair.get();
      try {
        springUserDetailsService.setAuthentication(userAndToken.getLeft(), userAndToken.getRight());
        filterChain.doFilter(request, response);
      } catch (IOException | ServletException e) {
        LOGGER.error("Error processing servlet chain. URI: {}:{}", 
            request.getMethod(), request.getRequestURI(), e
        );
        throw new InvalidDataException("Authentication failed.");
      }
    } else {
      LOGGER.error("Authentication failed for request: {}", request);
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
    }
  }

}
