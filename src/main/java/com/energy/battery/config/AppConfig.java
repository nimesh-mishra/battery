package com.energy.battery.config;

import java.time.ZonedDateTime;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "auditingDateTimeProvider")
public class AppConfig {

  @Bean(name = "auditingDateTimeProvider")
  DateTimeProvider dateTimeProvider() {
    return () -> Optional.of(ZonedDateTime.now());
  }

  @Bean
  ObjectMapper objectMapper() {
    return JsonMapper.builder().addModule(new JavaTimeModule()).build();
  }
}
