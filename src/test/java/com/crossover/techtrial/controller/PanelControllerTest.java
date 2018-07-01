package com.crossover.techtrial.controller;

import com.crossover.techtrial.dto.DailyElectricity;
import com.crossover.techtrial.model.HourlyElectricity;
import com.crossover.techtrial.model.Panel;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;


/**
 * PanelControllerTest class will test all APIs in PanelController.java.
 * @author Crossover
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PanelControllerTest {
  
  MockMvc mockMvc;
  
  @Mock
  private PanelController panelController;
  
  @Autowired
  private TestRestTemplate template;

  @Before
  public void setup() throws Exception {
    mockMvc = MockMvcBuilders.standaloneSetup(panelController).build();
  }

  @Test
  public void testPanelShouldBeRegistered() throws Exception {
    HttpEntity<Object> panel = getHttpEntity(
        "{\"serial\": \"232323\", \"longitude\": \"54.123232\"," 
            + " \"latitude\": \"54.123232\",\"brand\":\"tesla\" }");
    ResponseEntity<Panel> response = template.postForEntity(
        "/api/register", panel, Panel.class);
    // Changed due to serial number validation
    Assert.assertEquals(400,response.getStatusCode().value());
  }

  private HttpEntity<Object> getHttpEntity(Object body) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return new HttpEntity<Object>(body, headers);
  }
  
  /**
	 * Test method for {@link com.crossover.techtrial.controller.PanelController#registerPanel(com.crossover.techtrial.model.Panel)}.
	 */
	@Test
	public void testValidRegisterPanel() {
	    HttpEntity<Object> panel = getHttpEntity(
	        "{\"serial\": \"1234567890ABCDEF\", \"longitude\": \"54.123232\"," 
	            + " \"latitude\": \"54.123232\",\"brand\":\"tesla\" }");
	    ResponseEntity<Panel> response = template.postForEntity(
	        "/api/register", panel, Panel.class);
	    Assert.assertEquals(202,response.getStatusCode().value());
	}

	@Test
	public void testBadRequestRegisterPanel() {
	    HttpEntity<Object> panel = getHttpEntity(
	            "{\"serial\": \"654321\", \"longitude\": \"54.123232\"," 
	                + " \"latitude\": \"54.123232\",\"brand\":\"teslar\" }");
	        ResponseEntity<Panel> response = template.postForEntity(
	            "/api/register", panel, Panel.class);
	        Assert.assertEquals(400,response.getStatusCode().value());
	}

	/**
	 * Test method for {@link com.crossover.techtrial.controller.PanelController#saveHourlyElectricity(java.lang.String, com.crossover.techtrial.model.HourlyElectricity)}.
	 */
	@Test
	public void testSaveHourlyElectricity() {
		HttpEntity<Object> hourly = getHttpEntity(
				"{ \"panel\": {\"id\": \"1\"}, "
				+ "\"generatedElectricity\": \"50\", "
				+ "\"readingAt\": \"2018-06-30T00:00:00.000Z\" }");
		ResponseEntity<HourlyElectricity> response = template.postForEntity(
		        "/api/panels/1234567890123456/hourly", hourly, HourlyElectricity.class);
		Assert.assertEquals(200, response.getStatusCode().value());
	}

	/**
	 * Test method for {@link com.crossover.techtrial.controller.PanelController#hourlyElectricity(java.lang.String, org.springframework.data.domain.Pageable)}.
	 */
	@Test
	public void testHourlyElectricity() {
		String url = "/api/panels/1234567890123456/hourly-history";
		
		ResponseEntity<PagedResources<HourlyElectricity>> response = template.exchange(url,
                HttpMethod.GET, null, new ParameterizedTypeReference<PagedResources<HourlyElectricity>>() {});

		Assert.assertEquals(200, response.getStatusCode().value());
	}
	
	@Test
	public void testInvalidSerialHourlyElectricity() {
		String url = "/api/panels/123456/hourly-history";
		
		ResponseEntity<PagedResources<HourlyElectricity>> response = template.exchange(url,
                HttpMethod.GET, null, new ParameterizedTypeReference<PagedResources<HourlyElectricity>>() {});

		Assert.assertEquals(404, response.getStatusCode().value());
	}

	/**
	 * Test method for {@link com.crossover.techtrial.controller.PanelController#allDailyElectricityFromYesterday(java.lang.String)}.
	 */
	@Test
	public void testAllDailyElectricityFromYesterday() {
		String url = "/api/panels/1234567890123456/daily";
		
		ResponseEntity<List<DailyElectricity>> response = template.exchange(url,
                HttpMethod.GET, null, new ParameterizedTypeReference<List<DailyElectricity>>() {});

		Assert.assertEquals(200, response.getStatusCode().value());
	}
}
