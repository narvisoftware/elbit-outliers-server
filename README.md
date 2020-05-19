# Outliers detection

Because percentile computation for unbounded values of data cannot be scaled, I've used the approximate calculation of percentile of the Elasticsearch's aggregate percentile function.

If the values of the sensor readings are in an reasonable number of values (for example only integers in range - 0 to 100), then the percentile can be computed incrementaly by storing a map with the number of readings for each sensor value (this will add .5 non integer values computed as a result of median comprimation of received redings).

### Outlier formula

For outlier detection, I've used the following formula: 

* Q1 – quartile 1, the median of the lower half of the data set
* Q3 – quartile 3, the median of the upper half of the data set
* IQR – interquartile range, the difference from Q3 to Q1 (IQR = Q3 – Q1)

Where  the outliers are the values:

* larger than Q3 by at least 1.5 times the interquartile range (IQR), or
* smaller than Q1 by at least 1.5 times the IQR.

## Running the server

For dev environment, the application starts the Elasticsearch and Kafka servers embedded inside the application.

The application can be run with the start-dev-server.bat batch file. For non windows machines, the content of the .bat file can be executed in the root folder of the application:

```mvnw --settings dev-settings.xml -pl outliers-server -am spring-boot:run -Dspring-boot.run.profiles=dev```