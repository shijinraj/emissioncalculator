package com.emission.calculator;

import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class EmissioncalculatorApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(EmissioncalculatorApplication.class)
				.bannerMode(Banner.Mode.OFF).web(WebApplicationType.NONE).run(args);
	}

}
