package com.energy.battery.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;
import com.energy.battery.BatteryApplication;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = WebEnvironment.MOCK, classes = BatteryApplication.class)
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("IT")
@DirtiesContext
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class TestConfig extends MongoConfig {

  @Autowired
  public MockMvc mockMvc;

  @Autowired
  public ObjectMapper objectMapper;

}
