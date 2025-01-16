package org.cftoolsuite.cfapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = "cf.butlers.example=https://cf-butler.example.com")
public class CfHooverApplicationTests {

	@Test
	public void contextLoads() {
	}

}
