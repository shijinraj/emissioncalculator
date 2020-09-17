package com.emission.calculator.component;

import static com.emission.calculator.geolocation.service.GeoLocationService.transportationMethodCO2Map;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.emission.calculator.geolocation.service.GeoLocationService;

/**
 * Emission calculator
 * 
 * @author shijin.raj
 *
 */
@Component
public class EmissionCalculator implements CommandLineRunner {

	private static final String INVALID = "Invalid ";

	private static final String TRANSPORTATION_METHOD = "transportation-method";

	private static final String END = "end";

	private static final String START = "start";

	private static DecimalFormat df = new DecimalFormat("0.0");

	@Autowired
	private GeoLocationService geoLocationService;

	@Override
	public void run(String... args) {

		try {

			Map<String, String> argumentMap = ofNullable(args).map(this::getArgumentMap).orElse(null);

			if (isValidArgumentMap(argumentMap)) {
				of(argumentMap).map(argsMap -> geoLocationService.findDistance(argsMap.get(START), argsMap.get(END)))
						.filter(Objects::nonNull)
						.map(distance -> geoLocationService.calculateCO2Emission(argumentMap.get(TRANSPORTATION_METHOD),
								distance))
						.filter(Objects::nonNull).ifPresent(co2EmissionInKG -> System.out.println(
								String.format("Your trip caused %skg of CO2-equivalent.", df.format(co2EmissionInKG))));

			}

		} catch (Exception e) {
			System.out.println("Invalid arguments :" + args == null ? null : Arrays.toString(args));
		}

	}

	/**
	 * Checks Argument Map
	 * 
	 * @param argumentMap
	 * @return boolean whether valid argument map or not
	 */
	private boolean isValidArgumentMap(Map<String, String> argumentMap) {

		boolean isValidArgumentMap = true;

		if (CollectionUtils.isEmpty(argumentMap)) {
			isValidArgumentMap = false;
			System.out.println("Invalid arguments");
		} else if (argumentMap.size() != 3) {
			isValidArgumentMap = false;
			System.out.println("Invalid arguments");
		} else if (!argumentMap.containsKey(TRANSPORTATION_METHOD)) {
			isValidArgumentMap = false;
			System.out.println(INVALID + TRANSPORTATION_METHOD);
		} else if (StringUtils.isEmpty(argumentMap.get(TRANSPORTATION_METHOD))) {
			isValidArgumentMap = false;
			System.out.println(INVALID + TRANSPORTATION_METHOD);
		} else if (!transportationMethodCO2Map.containsKey(argumentMap.get(TRANSPORTATION_METHOD))) {
			isValidArgumentMap = false;
			System.out.println(INVALID + TRANSPORTATION_METHOD);
		} else if (!argumentMap.containsKey(START)) {
			isValidArgumentMap = false;
			System.out.println(INVALID + START);
		} else if (StringUtils.isEmpty(argumentMap.get(START))) {
			isValidArgumentMap = false;
			System.out.println(INVALID + START);
		} else if (!argumentMap.containsKey(END)) {
			isValidArgumentMap = false;
			System.out.println(INVALID + END);
		} else if (StringUtils.isEmpty(argumentMap.get(END))) {
			isValidArgumentMap = false;
			System.out.println(INVALID + END);
		}

		return isValidArgumentMap;
	}

	/**
	 * Extracts start, end and transportation-method from the argument
	 * 
	 * @param args
	 * @return Map<String, String> with start, end and transportation-method
	 */
	private Map<String, String> getArgumentMap(String[] args) {

		Map<String, String> argMap = null;
		if (args != null && args.length <= 6 && args.length >= 3) {
			argMap = new HashMap<>();
			for (int i = 0; i < args.length; i++) {
				if (args[i] != null && args[i].trim() != "") {
					String[] splitFormEqual = args[i].split("=");
					if (argMap.size() == 3) {
						argMap = null;
						break;
					}
					if (splitFormEqual.length == 2) {
						String key = splitFormEqual[0].substring(2).trim();
						String value = splitFormEqual[1].replaceAll("--", "").trim();
						argMap.put(key.toLowerCase(), value.toLowerCase());
					} else if (i + 1 < args.length) {
						String key = args[i].replaceAll("--", "").toLowerCase().trim();
						String value = args[i + 1].toLowerCase().trim();
						argMap.put(key, value);
						i++;
					} else {
						argMap = null;
						break;
					}
				}

			}
		}
		return argMap;
	}

}
