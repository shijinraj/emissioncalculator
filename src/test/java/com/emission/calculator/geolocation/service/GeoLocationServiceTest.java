package com.emission.calculator.geolocation.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.emission.calculator.geolocation.service.GeoLocationService;

@SpringBootTest
@DisplayName("Testing distance between two citiess")
@ActiveProfiles("test")
class GeoLocationServiceTest {

	@Autowired
	private GeoLocationService geoLocationService;

	@DisplayName("Test Find Distance")
	@ParameterizedTest(name = "Test Find Distance from {0} to {1} for {2} KM")
	@MethodSource("provideStartAndEndCityDetails")
	final void testFindDistance(String start, String end, Double expectedDistanceInKM) {
		Double actualDistanceInKM = geoLocationService.findDistance(start, end);
		assertEquals(expectedDistanceInKM, actualDistanceInKM);

	}

	@DisplayName("Test Find Distance with Invalid Cities")
	@ParameterizedTest(name = "Test Find Distance from {0} to {1}")
	@MethodSource("provideInvalidStartAndEndCityDetails")
	final void testFindDistanceWithInvalidCities(String start, String end, Double expectedDistanceInKM) {
		Double actualDistanceInKM = geoLocationService.findDistance(start, end);
		assertEquals(expectedDistanceInKM, actualDistanceInKM);

	}

	@DisplayName("Test Calculate CO2 Emission")
	@ParameterizedTest(name = "Test Calculate CO2 Emission for the transportation method {0} of distance {1} is {2}")
	@MethodSource("provideCO2EmissionDetails")
	final void testCalculateCO2Emission(String transportationMethod, Double distance, Double expectedCO2EmissionValue) {
		assertEquals(expectedCO2EmissionValue, geoLocationService.calculateCO2Emission(transportationMethod, distance));
	}

	@DisplayName("Test Calculate CO2 Emission with Invalid Transportation Method")
	@ParameterizedTest(name = "Test Calculate CO2 Emission for the transportation method {0} of distance {1} is {2}")
	@MethodSource("provideInvalidCO2EmissionDetails")
	final void testCalculateCO2EmissionWithInvalidTransportationMethod(String transportationMethod, Double distance,
			Double expectedCO2EmissionValue) {
		assertEquals(expectedCO2EmissionValue, geoLocationService.calculateCO2Emission(transportationMethod, distance));
	}

	/**
	 * creates Stream<Arguments> for Start And End City Details
	 * 
	 * @return Stream<Arguments> Start And End City Details
	 */
	@SuppressWarnings("unused")
	private static Stream<Arguments> provideStartAndEndCityDetails() {

		return Stream.of(Arguments.of("Hamburg", "Berlin", 288.0), Arguments.of("Los Angeles", "New York", 4505.18),
				Arguments.of("New York", "Los Angeles", 4505.18), Arguments.of("Hamburg", "Hamburg", 0d));
	}

	/**
	 * creates Stream<Arguments> for CO2 Emission Details
	 * 
	 * @return Stream<Arguments> CO2 Emission Details
	 */
	@SuppressWarnings("unused")
	private static Stream<Arguments> provideCO2EmissionDetails() {

		return Stream.of(Arguments.of("small-diesel-car", 283.94, 40.319480000000006),
				Arguments.of("small-petrol-car", 4501.34, 693.20636),
				Arguments.of("small-plugin-hybrid-car", 4501.34, 328.59782),
				Arguments.of("small-electric-car", 555.89, 27.7945),

				Arguments.of("medium-diesel-car", 17111.45, 2926.0579500000003),
				Arguments.of("medium-petrol-car", 2058.89, 395.30688),
				Arguments.of("medium-plugin-hybrid-car", 35.89, 3.9479),
				Arguments.of("medium-electric-car", 18.89, 1.09562),

				Arguments.of("large-diesel-car", 5058.57, 1057.2411299999999),
				Arguments.of("large-petrol-car", 8278.89, 2334.64698),
				Arguments.of("large-plugin-hybrid-car", 312.89, 39.42414),
				Arguments.of("large-electric-car", 118.75, 8.66875),

				Arguments.of("bus", 5010.05, 135.27135),

				Arguments.of("train", 8716.41, 52.29846));
	}

	/**
	 * creates Stream<Arguments> for Invalid CO2 Emission Details
	 * 
	 * @return Stream<Arguments> Invalid CO2 Emission Details
	 */
	@SuppressWarnings("unused")
	private static Stream<Arguments> provideInvalidCO2EmissionDetails() {

		return Stream.of(Arguments.of("InvalidTransportationMethod", null, null), Arguments.of(" ", null, null),
				Arguments.of(null, 4501.34, null));
	}

	/**
	 * creates Stream<Arguments> for Invalid Start And End City Details
	 * 
	 * @return Stream<Arguments> Invalid Start And End City Details
	 */
	@SuppressWarnings("unused")
	private static Stream<Arguments> provideInvalidStartAndEndCityDetails() {

		return Stream.of(Arguments.of("Hamburg", "TestInvalidCity", null),
				Arguments.of("TestInvalidCity", "New York", null),
				Arguments.of("TestInvalidCity", "TestInvalidCity", null), Arguments.of("TestInvalidCity", " ", null));
	}

}
