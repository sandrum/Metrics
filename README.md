# Metrics
### Java Exercise

##### Aggregate metrics for a particular metric name.

The project has 2 primary interactions:
1) Add a metric
2) Retrieve metrics based on a metric name


Since the project does not come with a database, the metrics are kept in memory for a maximum of 300 seconds.

How to use:
To run unit tests via command line, use: 
```
  mvn test
```

To run unit tests/integration tests via command line, use: 
```
  mvn clean verify
```

To build the package via command line, use: 
```
  mvn package
```
This will create a jar file that is accessible as a library for your projects.

Installing the JAR file to your maven project is as follows (in your project directory needing to use metrics):
```
mvn install:install-file -Dfile=<path-to-metrics-jar-generated-above> -DgroupId=com.ivansanchez -DartifactId=Metrics -Dversion=1.0-SNAPSHOT -Dpackaging=jar -DgeneratePom=true
```

Alternatively, after building the JAR file, a developer can also copy the jar file to a folder in the project they want to use it in.
The POM.xml file can be updated to reflect the local JAR availability.
The example below moved the JAR file to a file directly at the project root called "lib".:
```
<dependency>
    <groupId>com.ivansanchez</groupId>
    <artifactId>Metrics</artifactId>
    <version>1.0-SNAPSHOT</version>
    <scope>system</scope>
    <systemPath>${project.basedir}/lib/Metrics-1.0-SNAPSHOT-jar-with-dependencies.jar</systemPath>
</dependency>
```
-------------------------
#### Interaction
A metrics instance can be requested using the following command:      

```     
 Metrics = Metrics.getMetricsInstance();
```                                                

The instance has the following capabilities:
* putMetricData() -- with a metric name supplied: Returns false if a name is not supplied.
```     
 boolean success = metrics.putMetricData("homepage access");
```      
* putMetricData() -- with a name and a count supplied: Returns false if a name is not supplied.
```     
 boolean success = metrics.putMetricData("homepage access", 2);
```      
* putMetricData() -- with an operation/method name, a metric name and a count supplied: Returns false if a name is not supplied.
```     
 boolean success = metrics.putMetricData("api service pagemethod","homepage access", 2);
``` 
* getMetricsData() -- retrieve ALL aggregated metrics data within the maximum time limit
```     
 Map<String,Long> metricsData = metrics.getMetricsData();
``` 
* getMetricsDataWithTime() --  retrieve ALL aggregated metrics data with a specified limit (up to the maximum)   
```     
 Map<String,Long> metricsData = metrics.getMetricsData(200);
```                                                 
* getMetricsDataWithNameAndTime() -- retrieve aggregated metrics based on the name of the metric specified and the time.                                                                          
```     
 Map<String,Long> metricsData = metrics.getMetricsData("homepage access", 200);
```   
##### Future capabilities.

* Annotation based metrics
* Latency metrics

