@if "%DEBUG%" == "" @echo off

@rem Slurp the command line arguments.
set CMD_LINE_ARGS=%*

java -jar target/emissioncalculator-0.0.1-SNAPSHOT.jar %CMD_LINE_ARGS%