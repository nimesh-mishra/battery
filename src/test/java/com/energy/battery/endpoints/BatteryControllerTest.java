package com.energy.battery.endpoints;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.MethodMode;
import org.springframework.test.web.servlet.MvcResult;
import com.energy.battery.config.TestConfig;
import com.energy.battery.entities.Battery;
import com.energy.battery.io.ApiPaginatedResponse;
import com.energy.battery.io.BatteryApiFilter;
import com.energy.battery.io.BatteryIO;
import com.energy.battery.io.BatteryNamesWithStatsIO;
import com.energy.battery.repositories.BatteryRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

@DirtiesContext(methodMode = MethodMode.BEFORE_METHOD)
class BatteryControllerTest extends TestConfig {

  private static final String TOKEN =
      "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0eXBlIjoiU1lTVEVNX1VTRVIifQ.ObfUaZmOOP2JhKkBKtwCtN8Xzf3C1CBMPuwmxtWhetQ";
  private static final String BATTERY_DATA_LOCATION = "/test-data/battery.json";

  @Autowired
  private BatteryRepository batteryRepository;

  @BeforeEach
  public void setUp() {
    batteryRepository.deleteAll();
  }

  @Test
  void testCreateWithValidDataShoudCreateBattery() throws Exception {
    // Given
    BatteryIO batteryIO = buildBatteryIO("Amaron", 780, 6800);
    // When
     MvcResult mvcResult= this.mockMvc.perform(
       post("/batteries/create")
       .contentType(APPLICATION_JSON_VALUE)
       .content(this.objectMapper.writeValueAsString(batteryIO))
       .header("Authorization", TOKEN)
     )
     .andExpect(status().isOk())
     .andReturn();
     // Then
     BatteryIO result = this.objectMapper
         .readValue(mvcResult.getResponse().getContentAsString(), BatteryIO.class);
     assertEquals(1, result.getId());
     assertEquals(batteryIO.getName(), result.getName());
     assertEquals(batteryIO.getPostCode(), result.getPostCode());
     assertEquals(batteryIO.getCapacity(), result.getCapacity());
     assertNotNull(result.getCreatedDate());
     assertNotNull(result.getUpdatedDate());
     batteryRepository.findById(1L).map(b -> {
       assertEquals(batteryIO.getName(), b.getName());
       assertEquals(batteryIO.getCapacity(), b.getCapacity());
       assertEquals(batteryIO.getPostCode(), b.getPostCode());
       return b;
     })
     .orElseGet(() -> {
           fail("No battery found.");
           return null;
     });
  }

  @Test
  void testFindByPostCodeRangeWithValidRangeShouldReturnBatteriesBelongingToThePostCodeRange()
      throws JsonProcessingException, Exception {
    // Given
    loadData();
    BatteryApiFilter filter = new BatteryApiFilter();
    filter.setStartPostCode(6800);
    filter.setEndPostCode(7800);
    // When
    MvcResult mvcResult= this.mockMvc.perform(
        post("/batteries/find-by-postcode-range")
        .contentType(APPLICATION_JSON_VALUE)
        .content(this.objectMapper.writeValueAsString(filter))
        .header("Authorization", TOKEN)
      )
      .andExpect(status().isOk())
      .andReturn();
      // Then
      ApiPaginatedResponse<BatteryIO> response = this.objectMapper.readValue(
          mvcResult.getResponse().getContentAsString(),
          new TypeReference<ApiPaginatedResponse<BatteryIO>>() {}
      );
      assertNotNull(response);
      List<BatteryIO> batteries = response.getContents();
      System.out.println(batteries);
      assertEquals(5, batteries.size());
      assertEquals(1, response.getPageDetails().getCurrentPage());
      assertEquals(5, response.getPageDetails().getPageCount());
      assertEquals(5, response.getPageDetails().getTotalCount());
      // Validate order and data
      assertEquals("Amaron", batteries.get(0).getName());
      assertEquals(6800, batteries.get(0).getPostCode());
      assertEquals(780, batteries.get(0).getCapacity());
      assertEquals("Amaron E", batteries.get(1).getName());
      assertEquals(6803, batteries.get(1).getPostCode());
      assertEquals(790, batteries.get(1).getCapacity());
      assertEquals("Amaron Z", batteries.get(2).getName());
      assertEquals(6801, batteries.get(2).getPostCode());
      assertEquals(780, batteries.get(2).getCapacity());
      assertEquals("Amaron ZZ", batteries.get(3).getName());
      assertEquals(6802, batteries.get(3).getPostCode());
      assertEquals(790, batteries.get(3).getCapacity());
      assertEquals("Duracel", batteries.get(4).getName());
      assertEquals(7800, batteries.get(4).getPostCode());
      assertEquals(780, batteries.get(4).getCapacity());
  }

  @Test
  void testfindNamesByPostCodeRangeWithValidInbetweenRangePostCodeWillReturnNamesAndStatsOfBatteries()
      throws JsonProcessingException, Exception {
    // Given
    loadData();
    BatteryApiFilter filter = new BatteryApiFilter();
    filter.setStartPostCode(6800);
    filter.setEndPostCode(7800);
    // When
    MvcResult mvcResult= this.mockMvc.perform(
        post("/batteries/find-names-by-postcode-range")
        .contentType(APPLICATION_JSON_VALUE)
        .content(this.objectMapper.writeValueAsString(filter))
        .header("Authorization", TOKEN)
      )
      .andExpect(status().isOk())
      .andReturn();
 // Then
    ApiPaginatedResponse<BatteryNamesWithStatsIO> response = this.objectMapper.readValue(
        mvcResult.getResponse().getContentAsString(),
        new TypeReference<ApiPaginatedResponse<BatteryNamesWithStatsIO>>() {}
    );
    BatteryNamesWithStatsIO stats = response.getContent();
    assertNotNull(stats);
    assertEquals(5, stats.getNames().size());
    assertIterableEquals(
        asList("Amaron", "Amaron E", "Amaron Z", "Amaron ZZ", "Duracel"),
        stats.getNames()
    );
    assertEquals(3920, stats.getStats().getTotalCapacity());
    assertEquals(784.0, stats.getStats().getAverageCapacity());
  }
  
  @Test
  void testfindNamesByPostCodeRangeWithOutOfRangePostCodeWillReturnEmptyNamesAndStatsOfBatteries()
      throws JsonProcessingException, Exception {
    // Given
    loadData();
    BatteryApiFilter filter = new BatteryApiFilter();
    filter.setStartPostCode(4800);
    filter.setEndPostCode(5800);
    // When
    MvcResult mvcResult= this.mockMvc.perform(
        post("/batteries/find-names-by-postcode-range")
        .contentType(APPLICATION_JSON_VALUE)
        .content(this.objectMapper.writeValueAsString(filter))
        .header("Authorization", TOKEN)
        )
        .andExpect(status().isOk())
        .andReturn();
    // Then
    ApiPaginatedResponse<BatteryNamesWithStatsIO> response = this.objectMapper.readValue(
        mvcResult.getResponse().getContentAsString(),
        new TypeReference<ApiPaginatedResponse<BatteryNamesWithStatsIO>>() {}
    );
    BatteryNamesWithStatsIO stats = response.getContent();
    assertNotNull(stats);
    assertEquals(0, stats.getNames().size());
    assertEquals(0, stats.getStats().getTotalCapacity());
    assertEquals(0, stats.getStats().getAverageCapacity());
  }

  @Test
  void testfindNamesByPostCodeRangeWithEdgeCasePostCodeWillReturnNamesAndStatsOfBatteries()
      throws JsonProcessingException, Exception {
    // Given
    loadData();
    BatteryApiFilter filter = new BatteryApiFilter();
    filter.setStartPostCode(5800);
    filter.setEndPostCode(6800);
    // When
    MvcResult mvcResult = this.mockMvc
        .perform(post("/batteries/find-names-by-postcode-range").contentType(APPLICATION_JSON_VALUE)
            .content(this.objectMapper.writeValueAsString(filter)).header("Authorization", TOKEN))
        .andExpect(status().isOk()).andReturn();
    // Then
    ApiPaginatedResponse<BatteryNamesWithStatsIO> response =
        this.objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
            new TypeReference<ApiPaginatedResponse<BatteryNamesWithStatsIO>>() {});
    BatteryNamesWithStatsIO stats = response.getContent();
    assertNotNull(stats);
    assertEquals(1, stats.getNames().size());
    assertEquals(780, stats.getStats().getTotalCapacity());
    assertEquals(780, stats.getStats().getAverageCapacity());
  }

  private void loadData() {
    try {
      List<Battery> batteries = asList(
          this.objectMapper.readValue(
              BatteryControllerTest.class.getResource(BATTERY_DATA_LOCATION), Battery[].class
          )
      );
      batteryRepository.saveAll(batteries);
    } catch (IOException e) {
      e.printStackTrace();
      fail("Cannot load data file battery.json");
    }
  }

  private BatteryIO buildBatteryIO(String name, Integer capacity, Integer postCode) {
    BatteryIO batteryIO = new BatteryIO();
    batteryIO.setName(name);
    batteryIO.setCapacity(capacity);
    batteryIO.setPostCode(postCode);
    return batteryIO;
  }
}
