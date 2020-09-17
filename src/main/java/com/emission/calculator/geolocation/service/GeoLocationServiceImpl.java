package com.emission.calculator.geolocation.service;

import static java.util.Optional.ofNullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.emission.calculator.geolocation.domain.Feature;
import com.emission.calculator.geolocation.domain.GeoDistances;
import com.emission.calculator.geolocation.domain.Geocode;
import com.emission.calculator.geolocation.domain.Geometry;
import com.emission.calculator.geolocation.domain.Matrix;
import com.emission.calculator.geolocation.feign.client.GeoCoordinates;
import com.emission.calculator.geolocation.feign.client.MatrixDistance;

import feign.RetryableException;

/**
 * GeoLocation Service Implementation
 * 
 * @author shijin.raj
 *
 */
@Service
public class GeoLocationServiceImpl implements GeoLocationService {

	private static final String DISTANCE = "distance";

	private static final String KM = "km";

	private static final String DRIVING_CAR = "driving-car";

	private static final String ORS_TOKEN = "ORS_TOKEN";

	@Autowired
	private GeoCoordinates coordinates;

	@Autowired
	private MatrixDistance matrixDistance;

	@PostConstruct
	public void init() {
		transportationMethodCO2Map.put("small-diesel-car", 142);
		transportationMethodCO2Map.put("small-petrol-car", 154);
		transportationMethodCO2Map.put("small-plugin-hybrid-car", 73);
		transportationMethodCO2Map.put("small-electric-car", 50);

		transportationMethodCO2Map.put("medium-diesel-car", 171);
		transportationMethodCO2Map.put("medium-petrol-car", 192);
		transportationMethodCO2Map.put("medium-plugin-hybrid-car", 110);
		transportationMethodCO2Map.put("medium-electric-car", 58);

		transportationMethodCO2Map.put("large-diesel-car", 209);
		transportationMethodCO2Map.put("large-petrol-car", 282);
		transportationMethodCO2Map.put("large-plugin-hybrid-car", 126);
		transportationMethodCO2Map.put("large-electric-car", 73);

		transportationMethodCO2Map.put("bus", 27);

		transportationMethodCO2Map.put("train", 6);
	}

	private List<Double> getCoordinates(String cityName) {
		Geocode geocode = null;
		List<Double> coordinateList = null;
		try {
			geocode = coordinates.getCoordinates(System.getenv(ORS_TOKEN), cityName, "locality");
		} catch (Exception exception) {
			System.out.println("Exception occured while getting the geo coordinates for the city :" + cityName);
		}

		if (geocode != null && CollectionUtils.isEmpty(geocode.getFeatures())) {
			System.out.println("Invalid city " + cityName);
		} else {
			coordinateList = ofNullable(geocode).map(Geocode::getFeatures).map(Collection::stream)
					.orElseGet(Stream::empty).findFirst().map(Feature::getGeometry).map(Geometry::getCoordinates)
					.orElse(null);
		}

		return coordinateList;
	}

	/*
	 * @see
	 * com.emission.calculator.geolocation.GeoLocationService#findDistance(java.
	 * lang.String, java.lang.String)
	 */
	@Override
	public Double findDistance(String start, String end) {

		Double distanceInKM = null;
		List<Double> startCityCoordinates = getCoordinates(start);

		List<Double> endCityCoordinates = getCoordinates(end);

		GeoDistances distances = null;
		// Checks whether startCityCoordinates and endCityCoordinates is non-empty
		if (!CollectionUtils.isEmpty(startCityCoordinates) && !CollectionUtils.isEmpty(endCityCoordinates)) {
			Matrix matrix = Matrix.builder().metrics(Collections.singletonList(DISTANCE)).units(KM)
					.locations(Arrays.asList(startCityCoordinates, endCityCoordinates)).build();

			try {
				distances = matrixDistance.findDistance(System.getenv(ORS_TOKEN), DRIVING_CAR, matrix);
			} catch (Exception exception) {
				System.out.println("Exception occured while getting the geo distance");
			}

			List<Double> distanceInKms = ofNullable(distances).map(GeoDistances::getDistances)
					.filter(distanceList -> !CollectionUtils.isEmpty(distanceList)).map(Collection::stream)
					.orElseGet(Stream::empty).flatMap(Collection::stream).filter(Objects::nonNull)
					.filter(distance -> distance != 0).collect(Collectors.toList());

			distanceInKM = ofNullable(distanceInKms).filter(distanceList -> !CollectionUtils.isEmpty(distanceList))
					.map(Collections::max).orElseGet(() -> 0d);

		}

		return distanceInKM;
	}

	/*
	 * @see com.emission.calculator.geolocation.GeoLocationService#
	 * calculateCO2Emission(java.lang.String, java.lang.Double)
	 */
	@Override
	public Double calculateCO2Emission(String transportationMethod, Double distance) {
		return ofNullable(transportationMethodCO2Map.get(transportationMethod))
				.map(co2emission -> co2emission * distance / 1000).orElse(null);

	}

}
