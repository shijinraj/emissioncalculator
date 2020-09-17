package com.emission.calculator.geolocation.feign.client;

import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.emission.calculator.EmissioncalculatorApplication;
import com.emission.calculator.geolocation.domain.Feature;
import com.emission.calculator.geolocation.domain.Geocode;
import com.emission.calculator.geolocation.domain.Geometry;
import com.emission.calculator.geolocation.feign.client.GeoCoordinates;

import feign.FeignException;

@DisplayName("Testing Search for a city by name to get the coordinates")
@ExtendWith({ SpringExtension.class })
@ContextConfiguration(classes = {
		EmissioncalculatorApplication.class }, initializers = ConfigFileApplicationContextInitializer.class)
class GeoCoordinatesTest {

	@Autowired
	private GeoCoordinates coordinates;

	@DisplayName("Test Search with valid cities")
	@ParameterizedTest(name = "Test Search with city - {0} ")
	@MethodSource("provideValidCities")
	final void testGetCoordinatesWithValidCities(String city, List<Double> expectedCoordinates) {

		Geocode geocode = coordinates.getCoordinates(System.getenv("ORS_TOKEN"), city, "locality");

		List<Double> coordinatesActual = ofNullable(geocode).map(Geocode::getFeatures).map(Collection::stream)
				.orElseGet(Stream::empty).findFirst().map(Feature::getGeometry).map(Geometry::getCoordinates)
				.orElse(null);

		assertEquals(expectedCoordinates, coordinatesActual);
	}

	@DisplayName("Test Search with invalid cities")
	@ParameterizedTest(name = "Test Search with invalid city - {0} ")
	@MethodSource("provideInvalidCities")
	final void testGetCoordinatesWithInvalidCities(String city) {

		Geocode geocode = coordinates.getCoordinates(System.getenv("ORS_TOKEN"), city, "locality");

		List<Double> coordinatesActual = ofNullable(geocode).map(Geocode::getFeatures).map(Collection::stream)
				.orElseGet(Stream::empty).findFirst().map(Feature::getGeometry).map(Geometry::getCoordinates)
				.orElse(null);

		assertNull(coordinatesActual, " ");
	}

	@Test
	final void testGetCoordinatesWithNullCity() {
		assertThrows(FeignException.class,
				() -> coordinates.getCoordinates(System.getenv("ORS_TOKEN"), null, "locality"));
	}

	/**
	 * creates Stream<Arguments> for Valid Cities
	 * 
	 * @return Stream<Arguments> Valid Cities
	 */
	@SuppressWarnings("unused")
	private static Stream<Arguments> provideValidCities() {

		List<Double> hamburg = new ArrayList<>();
		hamburg.add(10.007046);
		hamburg.add(53.576158);

		List<Double> berlin = new ArrayList<>();
		berlin.add(13.40732);
		berlin.add(52.52045);

		List<Double> newYork = new ArrayList<>();
		newYork.add(-73.9708);
		newYork.add(40.68295);

		List<Double> losAngeles = new ArrayList<>();
		losAngeles.add(-118.25703);
		losAngeles.add(34.05513);

		List<Double> frankfurt = new ArrayList<>();
		frankfurt.add(8.584764);
		frankfurt.add(50.041821);

		return Stream.of(Arguments.of("Hamburg", hamburg), Arguments.of("Berlin", berlin),
				Arguments.of("New York", newYork), Arguments.of("Los Angeles", losAngeles),
				Arguments.of("Frankfurt", frankfurt));
	}

	/**
	 * creates Stream<Arguments> for InValid Cities
	 * 
	 * @return Stream<Arguments> InValid Cities
	 */
	@SuppressWarnings("unused")
	private static Stream<Arguments> provideInvalidCities() {

		return Stream.of(Arguments.of("TestCity1"), Arguments.of("TestCity2"), Arguments.of("TestCity3"),
				Arguments.of("TestCity4"), Arguments.of("TestCity5"));
	}

}
