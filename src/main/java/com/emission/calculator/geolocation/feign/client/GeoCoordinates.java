package com.emission.calculator.geolocation.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.emission.calculator.geolocation.domain.Geocode;

/**
 * Geo coordinates REST Feign client
 * 
 * @author shijin.raj
 *
 */
@FeignClient(value = "coordinates", url = "https://api.openrouteservice.org")
public interface GeoCoordinates {
	@GetMapping("/geocode/search")
	public Geocode getCoordinates(@RequestParam(name = "api_key") String apiKey,
			@RequestParam(name = "text") String cityName, @RequestParam(name = "layers") String layers);

}
