# Spring boot Non-Web application Case Study
*   ***CO2 emission calculator***
*   Program that returns the amount of CO2-equivalent that will be caused when traveling between two cities using
    a given transportation method.
    ***CO2 data***
*   The following average values used for calculation of  Transportation methods in CO2e per passenger per km:
*    Small cars:
    small-diesel-car : 142g
    small-petrol-car : 154g
    small-plugin-hybrid-car : 73g
    small-electric-car : 50g
*   Medium cars:
    medium-diesel-car : 171g
    medium-petrol-car : 192g
    medium-plugin-hybrid-car : 110g
    medium-electric-car : 58g
*    Large cars:
    large-diesel-car : 209g
    large-petrol-car : 282g
    large-plugin-hybrid-car : 126g
    large-electric-car : 73g
*   bus : 27g
*   train : 6g

    ***Geocode and distance API***
*    Use the openrouteservice to get the distance between 2 cities. 
*   Please create a free account to get an API Token. 
    Reads the value of the token from an environment variable called ORS_TOKEN .
*   Used the following endpoints:
    https://openrouteservice.org/dev/#/api-docs/geocode/search/get : Search for a city by name to get the coordinates
    Provide parameters api_key and text (the city name)
    Optionally provide parameter layers="locality" (to limit the search to cities)
    It returns a list of matching locations ordered by confidence
    
*    https://openrouteservice.org/dev/#/api-docs/v2/matrix/{profile}/post : Get the time or distance between two cities
    In the body, provide locations (list of coordinates) and metrics=["distance"]
    Provide URL parameter profile=driving-car
    Provide header Authorization=API_KEY
    It returns a matrix of distances for the given locations

*   The tool can be called with two cities start and end as well as a transportation-method . 
*   It outputs the amount of CO2-equivalent in kilogram.
*    $ ./co2-calculator --start Hamburg --end Berlin --transportation-method medium-diesel-car
    Your trip caused 49.2kg of CO2-equivalent.
*    Named parameters can be put in any order and either use a space ( ) or equal sign ( = ) between key and value.
*    $ ./co2-calculator --start "Los Angeles" --end "New York" --transportation-method=medium-diesel-car
    Your trip caused 770.4kg of CO2-equivalent.
*    $ ./co2-calculator --end "New York" --start "Los Angeles" --transportation-method=large-electric-car
    Your trip caused 328.9kg of CO2-equivalent.
## Setup
*   JDK 8 or higher version
*   Maven 3.5.X
*   Build application using maven command **mvn clean install**
*   Create an environment variable called **ORS_TOKEN** for openrouteservice token
*   Run application using command line from the project directory using *co2-calculator.cmd*
*   Example *co2-calculator.cmd --start Hamburg --end Berlin --transportation-method medium-diesel-car*

## Application Features
*   Spring Boot project with Spring cloud openfeign, Lombok, JUnit 5.

## Design Assumptions
*   Program checks for **start, end and transportation-method** either separated by '=' or space.
*   Program output *Invalid Arguments* message if the inputs are less than 3 in size.
*   Program output *Invalid Arguments* message if the inputs are more than 6 in size.
*   Program trims the empty space in the inputs
*   Program checks valid **transportation-method** from the given Transportation methods in CO2e per passenger per km chart.
*   Program checks valid city using **openrouteservice** service
*   For **openrouteservice** Search for a city by name to get the coordinates - Assuming the results are sorted by quality and considered very first result.
*   For **openrouteservice** Get the time or distance between two cities - *{profile}* is used as *driving-car* and Maximum distance between the cities is considered.
*   Program output exception message if **openrouteservice** services unavailable.

## Non-Functional requirements
*   The implemented features are unit tested - Unit Tests are available under */src/test* package for all layers.
*   Errors and edge-cases are considered - scenarios are covered in the respective unit tests.
*   The implementation uses a dependency management tool which allows easy compilation and test execution (e.g. in an CI/CD environment) - Maven project.
*   The README.md file contains clear instructions on how to compile, test and execute the tool is available.
*   Compilation is possible with Windows, Linux and macOS
*   The API token is stored and read from an environment variable called ORS_TOKEN
*   Best practices regarding architecture and code style are considered - Spring boot with Open feign client and Junit 5
