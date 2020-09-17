package com.emission.calculator.component;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.emission.calculator.EmissioncalculatorApplication;
import com.emission.calculator.component.EmissionCalculator;
import com.emission.calculator.geolocation.feign.client.GeoCoordinates;
import com.emission.calculator.geolocation.feign.client.MatrixDistance;

/**
 * Emission Calculator Test
 * @author shijin.raj
 *
 */
@ExtendWith({ SpringExtension.class, OutputCaptureExtension.class })
@ContextConfiguration(classes = {
		EmissioncalculatorApplication.class }, initializers = ConfigFileApplicationContextInitializer.class)
class EmissionCalculatorTest {

	@Autowired
	EmissionCalculator emissioncalculator;

	@Autowired
	GeoCoordinates coordinates;

	@Autowired
	MatrixDistance matrixDistance;

	CapturedOutput output;

	public EmissionCalculatorTest(CapturedOutput output) {
		super();
		this.output = output;
	}

	@Test
	final void testRun(CapturedOutput output) {
		emissioncalculator.run(null);
		assertThat(output).contains("Invalid arguments");

	}

	@DisplayName("Test with valid arguments")
	@ParameterizedTest(name = "{0} {1} {2} {3} {4} {5} for {6}")
	@MethodSource("provideValidArguments")
	final void testRunMethodWithValidArguments(String inputArgs1, String inputArgs2, String inputArgs3,
			String inputArgs4, String inputArgs5, String inputArgs6, String inputArgs7) {
		String expectedMessage = null;
		if (inputArgs7.equals("")) {
			expectedMessage = new String(inputArgs6);
			inputArgs6 = null;
		} else {
			expectedMessage = inputArgs7;
		}

		emissioncalculator.run(inputArgs1, inputArgs2, inputArgs3, inputArgs4, inputArgs5, inputArgs6);
		assertThat(output).contains(expectedMessage);
	}

	/**
	 * creates Stream<Arguments> for valid arguments
	 * 
	 * @return Stream<Arguments> for valid arguments
	 */
	@SuppressWarnings("unused")
	private static Stream<Arguments> provideValidArguments() {

		return Stream.of(
				Arguments.of("--start", "Hamburg", "--end", "Berlin", "--transportation-method", "medium-diesel-car",
						"Your trip caused 49.2kg of CO2-equivalent."),
				Arguments.of("--start", "Los Angeles", "--end", "New York", "--transportation-method=medium-diesel-car",
						"Your trip caused 770.4kg of CO2-equivalent.", ""),
				Arguments.of("--end", "New York", "--start", "Los Angeles",
						"--transportation-method=large-electric-car", "Your trip caused 328.9kg of CO2-equivalent.",
						""),
				Arguments.of("--start", "Berlin", "--end", "Berlin", "--transportation-method", "medium-diesel-car",
						"Your trip caused 0.0kg of CO2-equivalent."),
				Arguments.of("--start=Hamburg", "--end=Berlin", "--transportation-method=medium-diesel-car", null, null,
						null, "Your trip caused 49.2kg of CO2-equivalent."),
				Arguments.of("--start=Los Angeles", "--end", "New York", "--transportation-method=medium-diesel-car",
						null, "Your trip caused 770.4kg of CO2-equivalent.", ""),
				Arguments.of("--start=Hamburg", "--end=Berlin", "--transportation-method=medium-diesel-car",null,null,null,
						"Your trip caused 49.2kg of CO2-equivalent."));
	}

	@DisplayName("Test with invalid arguments --start , --end , --transportation-method ")
	@ParameterizedTest(name = "{6} is {0} {1} {2} {3} {4} {5}")
	@MethodSource("provideInvalidArguments")
	final void testRunMethodWithInvalidArguments(String inputArgs1, String inputArgs2, String inputArgs3,
			String inputArgs4, String inputArgs5, String inputArgs6, String expectedMessage) {
		emissioncalculator.run(inputArgs1, inputArgs2, inputArgs3, inputArgs4, inputArgs5, inputArgs6);
		assertThat(output).contains(expectedMessage);
	}

	/**
	 * creates Stream<Arguments> for invalid input arguments
	 * 
	 * @return Stream<Arguments> for invalid input arguments
	 */
	@SuppressWarnings("unused")
	private static Stream<Arguments> provideInvalidArguments() {

		return Stream.of(
				Arguments.of("--start1", "Hamburg", "--end", "Berlin", "--transportation-method", "medium-diesel-car",
						"Invalid start"),
				Arguments.of("--start", " ", "--end", "Berlin", "--transportation-method", "medium-diesel-car",
						"Invalid start"),
				Arguments.of("--start", "Hamburg", "--end1", "Berlin", "--transportation-method", "medium-diesel-car",
						"Invalid end"),
				Arguments.of("--start", "Hamburg", "--end", " ", "--transportation-method", "medium-diesel-car",
						"Invalid end"),
				Arguments.of("--start", "Hamburg", "--end", "Berlin", "--transportation-method1", "medium-diesel-car",
						"Invalid transportation-method"),
				Arguments.of("--start", "Hamburg", "--end", "Berlin", "--transportation-method", " ",
						"Invalid transportation-method"),
				Arguments.of("--start", "Hamburg", "--end", "Berlin", "--transportation-method", "medium-bike",
						"Invalid transportation-method"),
				Arguments.of("   --end", "   New York", "   --start", "   Los Angeles",
						"  --transportation-method=large-electric-car", "--transportation-method=large-diesel-car",
						"Invalid arguments"),
				Arguments.of(null, "Hamburg", "--end", "Berlin", "--transportation-method", "medium-diesel-car",
						"Invalid arguments"),
				Arguments.of("--start", "aerospace", "--end", "Berlin", "--transportation-method", "medium-diesel-car",
						"Invalid city aerospace"));
	}

	@DisplayName("Test with additional argument values")
	@Test
	final void testRunMethodWithAdditionalArgumentValues() {
		emissioncalculator.run("--start", "Hamburg", "--end", "Berlin", "--transportation-method", "medium-diesel-car",
				"--start", "Hamburg", "--end", "Berlin", "--transportation-method", "medium-diesel-car");
		assertThat(output).contains("Invalid arguments");
	}

}
