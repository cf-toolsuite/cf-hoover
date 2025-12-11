package org.cftoolsuite.cfapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "cf.butlers.example=https://cf-butler.example.com")
public class CfHooverApplicationTests {

	@Test
	public void contextLoads() {
	}

}
