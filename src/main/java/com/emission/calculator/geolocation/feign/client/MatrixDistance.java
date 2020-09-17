package com.emission.calculator.geolocation.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.emission.calculator.geolocation.domain.GeoDistances;
import com.emission.calculator.geolocation.domain.Matrix;

/**
 * Matrix Distance REST Feign client
 * 
 * @author shijin.raj
 *
 */
@FeignClient(value = "matrixDistance", url = "https://api.openrouteservice.org")
public interface MatrixDistance {

	@PostMapping("/v2/matrix/{profile}")
	public GeoDistances findDistance(@RequestHeader(value = "Authorization") String apiKey,
			@PathVariable(name = "profile") String profile, @RequestBody Matrix matrix);
}
