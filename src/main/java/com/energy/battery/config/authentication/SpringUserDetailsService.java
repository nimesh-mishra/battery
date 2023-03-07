package com.energy.battery.config.authentication;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.energy.battery.config.authentication.ApplicationID.ApplicationIDType;

@Service
public class SpringUserDetailsService implements UserDetailsService {

  private static final Logger LOGGER = LoggerFactory.getLogger(SpringUserDetailsService.class);

  private static final String TYPE = "type";
  private static final String HEADER_AUTH_TYPE = "Bearer";

  @Value("${jwt.token.secret}")
  private String jwtTokenSecret;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    throw new NotImplementedException(HttpStatus.NOT_IMPLEMENTED.getReasonPhrase());
  }

  public static ApplicationID authenticatedPrincipal() {
    return (ApplicationID) Objects
        .requireNonNull(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
  }

  public static String getAuthenticationToken() {
    return Objects.requireNonNull(
        SecurityContextHolder.getContext().getAuthentication().getCredentials().toString());
  }

  private Optional<ApplicationID> getUserFromToken(String token) {
    try {
      DecodedJWT decodedJWT = JWT.require(getAlgorithm()).build().verify(token);
      String type = decodedJWT.getClaim(TYPE).asString();
      if (ApplicationIDType.SYSTEM_USER.name().equals(type)) {
        ApplicationID applicationID = ApplicationID.systemDefault();
        return Optional.of(applicationID);
      }
      return Optional.empty();

    } catch (Exception e) {
      LOGGER.error("Could not verify the given token: {}", token, e);
      return Optional.empty();
    }
  }

  public String generateSystemUserToken() {
    return JWT.create().withClaim(TYPE, ApplicationIDType.SYSTEM_USER.name())
        .withExpiresAt(Date.from(LocalDateTime.now().plusHours(24).toInstant(ZoneOffset.UTC)))
        .sign(getAlgorithm());
  }

  public void setAuthentication(ApplicationID appId, String token) {
    // User is always system user..
    SecurityContext authenticatedContext = SecurityContextHolder.createEmptyContext();
    authenticatedContext.setAuthentication(
        new UsernamePasswordAuthenticationToken(appId, token, Collections.emptyList())
    );
    SecurityContextHolder.setContext(authenticatedContext);
  }

  public Optional<Pair<ApplicationID, String>> getUserFromAuthorizationHeader(
      HttpServletRequest request) {
    String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (authorizationHeader == null || !authorizationHeader.startsWith(HEADER_AUTH_TYPE + " ")) {
      LOGGER.warn("Authorization header must be provided");
      return Optional.empty();
    }
    String token = authorizationHeader.substring(HEADER_AUTH_TYPE.length()).trim();
    return getUserFromToken(token).map(user -> Pair.of(user, token));
  }

  private Algorithm getAlgorithm() {
    return Algorithm.HMAC256(jwtTokenSecret.getBytes());
  }
}
