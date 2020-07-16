# Metrics
### Java exercise

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

To build the package via command line, use: 
 ```
  mvn package
  ```
This will create a jar file that is accessible as a library for your projects.

Installing the JAR file to your maven project is as follows (in your project directory needing to use metrics):
```
mvn install:install-file -Dfile=<path-to-metrics-jar-generated-above> -DgroupId=com.ivansanchez -DMetrics -Dversion=1.0-SNAPSHOT -Dpackage=jar -DgeneratePom=true
```


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
