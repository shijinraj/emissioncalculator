package com.emission.calculator.geolocation.service;

import java.util.HashMap;
import java.util.Map;

public interface GeoLocationService {

	public static Map<String, Integer> transportationMethodCO2Map = new HashMap<>();
	/**
	 * find distance between two cities
	 * 
	 * @param start
	 * @param end
	 * @return distance in KM and null if the cities are invalid
	 */
	public Double findDistance(String start, String end);

	/**
	 * calculate CO2 Emission
	 * 
	 * @param transportationMethod
	 * @param distance
	 * @return CO2 Emission amount in KG
	 */
	public Double calculateCO2Emission(String transportationMethod, Double distance);

}
