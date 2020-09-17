package com.emission.calculator.geolocation.feign.client;

import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
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
import org.springframework.util.CollectionUtils;

import com.emission.calculator.EmissioncalculatorApplication;
import com.emission.calculator.geolocation.domain.GeoDistances;
import com.emission.calculator.geolocation.domain.Matrix;
import com.emission.calculator.geolocation.feign.client.MatrixDistance;

@DisplayName("Testing distance between two citiess")
@ExtendWith({ SpringExtension.class})
@ContextConfiguration(classes = {
		EmissioncalculatorApplication.class }, initializers = ConfigFileApplicationContextInitializer.class)
class MatrixDistanceTest {

	@Autowired
	private MatrixDistance matrixDistance;

	@DisplayName("Test Find Distance")
	@ParameterizedTest(name = "Test Find Distance from {0} to {2} for {4} KM")
	@MethodSource("provideStartAndEndCityDetails")
	final void testFindDistance(String startCity, List<Double> startCityCoordinates, String endCity,
			List<Double> endCityCoordinates, Double expectedDistanceInKM) {

		Matrix matrix = Matrix.builder().metrics(Collections.singletonList("distance")).units("km")
				.locations(Arrays.asList(startCityCoordinates, endCityCoordinates)).build();

		GeoDistances distances = matrixDistance.findDistance(System.getenv("ORS_TOKEN"), "driving-car", matrix);

		List<Double> distanceInKms = ofNullable(distances).map(GeoDistances::getDistances)
				.filter(distanceList -> !CollectionUtils.isEmpty(distanceList)).map(Collection::stream)
				.orElseGet(Stream::empty).flatMap(Collection::stream).filter(Objects::nonNull)
				.filter(distance -> distance != 0).collect(Collectors.toList());

		Double minimumDistance = ofNullable(distanceInKms)
				.filter(distanceList -> !CollectionUtils.isEmpty(distanceList)).map(Collections::min)
				.orElseGet(() -> 0d);

		assertEquals(expectedDistanceInKM, minimumDistance);
	}

	@DisplayName("Test Find Distance With Invalid Data")
	@ParameterizedTest(name = "Test Find Distance With Invalid Data from {0} to {2} for {4} KM")
	@MethodSource("provideCoordinatesWithInvalidData")
	final void testFindDistanceWithInvalidData(String startCity, List<Double> startCityCoordinates, String endCity,
			List<Double> endCityCoordinates, Double expectedDistanceInKM) {

		Matrix matrix = Matrix.builder().metrics(Collections.singletonList("distance")).units("km")
				.locations(Arrays.asList(startCityCoordinates, endCityCoordinates)).build();

		GeoDistances distances = matrixDistance.findDistance(System.getenv("ORS_TOKEN"), "driving-car", matrix);

		List<Double> distanceInKms = ofNullable(distances).map(GeoDistances::getDistances)
				.filter(distanceList -> !CollectionUtils.isEmpty(distanceList)).map(Collection::stream)
				.orElseGet(Stream::empty).flatMap(Collection::stream).filter(Objects::nonNull)
				.filter(distance -> distance != 0).collect(Collectors.toList());

		Double minimumDistance = ofNullable(distanceInKms)
				.filter(distanceList -> !CollectionUtils.isEmpty(distanceList)).map(Collections::min)
				.orElseGet(() -> 0d);

		assertEquals(expectedDistanceInKM, minimumDistance);
	}

	@Test
	final void testFindDistanceWithNullCoordinates() {
		assertThrows(IllegalArgumentException.class,
				() -> matrixDistance.findDistance(System.getenv("ORS_TOKEN"), "driving-car", null));
	}

	/**
	 * creates Stream<Arguments> for Start And End City Details
	 * 
	 * @return Stream<Arguments> Start And End City Details
	 */
	@SuppressWarnings("unused")
	private static Stream<Arguments> provideStartAndEndCityDetails() {

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

		return Stream.of(Arguments.of("Hamburg", hamburg, "Berlin", berlin, 283.94),
				Arguments.of("Los Angeles", losAngeles, "New York", newYork, 4501.34),
				Arguments.of("New York", newYork, "Los Angeles", losAngeles, 4501.34),
				Arguments.of("Hamburg", hamburg, "Hamburg", hamburg, 0d));
	}

	/**
	 * creates Stream<Arguments> for invalid coordinates
	 * 
	 * @return Stream<Arguments> invalid coordinates
	 */
	@SuppressWarnings("unused")
	private static Stream<Arguments> provideCoordinatesWithInvalidData() {

		List<Double> invalidCoordinate1 = new ArrayList<>();
		invalidCoordinate1.add(23.123456);
		invalidCoordinate1.add(63.789123);

		List<Double> invalidCoordinate2 = new ArrayList<>();
		invalidCoordinate2.add(31.456789);
		invalidCoordinate2.add(25.567890);

		List<Double> invalidCoordinate3 = new ArrayList<>();
		invalidCoordinate3.add(-37.98765);
		invalidCoordinate3.add(14.678912);

		List<Double> invalidCoordinate4 = new ArrayList<>();
		invalidCoordinate4.add(-124.234561);
		invalidCoordinate4.add(43.012345);

		List<Double> hamburg = new ArrayList<>();
		hamburg.add(10.007046);
		hamburg.add(53.576158);

		return Stream.of(
				Arguments.of("invalidCoordinate1", invalidCoordinate1, "invalidCoordinate2", invalidCoordinate2, 0d),
				Arguments.of("invalidCoordinate3", invalidCoordinate3, "invalidCoordinate4", invalidCoordinate4, 0d),
				Arguments.of("invalidCoordinate4", invalidCoordinate4, "invalidCoordinate3", invalidCoordinate3, 0d),
				Arguments.of("invalidCoordinate1", invalidCoordinate1, "invalidCoordinate1", invalidCoordinate1, 0d),
				Arguments.of("invalidCoordinate2", invalidCoordinate2, "Hamburg", hamburg, 0d),
				Arguments.of("Hamburg", hamburg, "invalidCoordinate2", invalidCoordinate2, 0d));
	}

}
