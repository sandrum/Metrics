package com.ivansanchez.metrics;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Metrics singleton to capture and keep metrics. Primarily, the concept is to use a concurrent
 * map of maps, where the key to the outer map is based on the current timestamp
 * (rounded down to 1 second) and the value is a map of metrics by metric name.  It will be up
 * to the user to understand that the metrics names should be unique for each type they wish to
 * count.
 */
@Slf4j
public class Metrics {

    /** Metrics map used for storing metrics. This should ostensibly temporary as metrics should be logged on disk.**/
    private final Map<Long, Map<String, MetricsItem>> metricsMap = new ConcurrentHashMap<>();
    /** keep track of time ingested. **/
    private final PriorityBlockingQueue<Long> timeKeeper = new PriorityBlockingQueue<>();
    private static Metrics metrics = null;

    /** The service's name. **/
    @Setter
    private String serviceName;

    /** Time for metrics to live. **/
    private final int timeToLive = 300;

    private Metrics() {
        //singleton
    }

    /**
     * Return a singleton metrics object
     * @return - metrics singleton
     */
    public static Metrics getMetricsInstance() {
        if(metrics == null) {
            metrics = new Metrics();
        }
        return metrics;
    }

    /**
     * Log metrics data by name. A count of 1 is implied by not using a count parameter.
     * @param name - name of the metrics data.
     */
    public boolean putMetricData(String name) {
       return putMetricData(name, 1);
    }

    /**
     * Log metrics data by name with a count specified.
     * @param name - name of the metrics data.
     * @param count - number to augment the metrics count by.
     */
    public boolean putMetricData(final String name, final long count) {
        return putMetricData("", name, count);
    }

    /**
     * Log metrics data by operation/method name, name of the metric and count
     * @param operation - name of method/operation this metric is being counted for
     * @param name - name of the metrics data.
     * @param count - number to augment the metrics count by.
     */
    public boolean putMetricData(final String operation, final String name, final long count) {
        if(name.isEmpty()) return false;

        long ts = getTimeStamp();
        if(!metricsMap.containsKey(ts)) {
            timeKeeper.add(ts);
        }
        Map<String, MetricsItem> metricsRecords = metricsMap.getOrDefault(ts, new ConcurrentHashMap<>());
        MetricsItem item = metricsRecords.getOrDefault(name, new MetricsItem());
        item.setServiceName(serviceName);
        item.setName(name);
        item.setOperationName(operation);
        item.updateCount(count);
        metricsRecords.put(name, item);
        metricsMap.put(ts, metricsRecords);
        
        //remove older bits
        cleanMap();
        return true;
    }

    /**
     * Return timestamp in seconds
     * @return time in seconds
     */
    private long getTimeStamp(){
        Calendar timestamp = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        return timestamp.getTimeInMillis()/1000;
    }

    /**
     * Return all metrics within the maximum time bounds of TimeToLive(300) seconds.
     * @return - map of metrics values tied to the name of the metric and the total count of that metric.
     */
    public Map<String, Long> getMetricsData() {
        return getMetricsDataWithTime(timeToLive);
    }

    /**
     * Retrieve ALL metrics within a given time. This maxes out at 5 minutes in length (300 seconds).
     * Then aggregate the metrics based on metric name and return the result.
     * @param seconds - high bound value in seconds from the present to retrieve metrics.
     * @return - map of metrics values tied to the name of the metric and the total count of that metric.
     */
    public Map<String, Long> getMetricsDataWithTime(final long seconds) {
        return getMetricsDataWithNameAndTime("", seconds);
    }

    /**
     * Retrieve ALL metrics within a given time AND name. This maxes out at 5 minutes in length (TimeToLive seconds) .
     * @param name - filterable name of the specific metric to retrieve data for. This value can be blank.
     * @param seconds - high bound value in seconds from the present to retrieve metrics.
     * @return - map of metrics values tied to the name of the metric and the total count of that metric.
     */
    public Map<String, Long> getMetricsDataWithNameAndTime(final String name, final long seconds) {
        Map<String, Long> metricslist = new HashMap<>();
        long timestmp = getTimeStamp();
        long pastTimestamp = timestmp - seconds;
        while(timestmp > pastTimestamp) {
            if(metricsMap.containsKey(timestmp)) {
                Map<String, MetricsItem> mapItem = metricsMap.get(timestmp);
                for(Map.Entry<String, MetricsItem> entry: mapItem.entrySet()) {
                    if(name.isEmpty() || (!name.isEmpty() && name.equals(entry.getKey()))) {
                        if (!metricslist.containsKey(entry.getKey())) {
                            metricslist.put(entry.getKey(), entry.getValue().getCount());
                        } else {
                            metricslist.put(entry.getKey(), metricslist.get(entry.getKey())+entry.getValue().getCount());
                        }
                    }
                }
            }
            timestmp--;
        }
        return metricslist;
    }

    /**
     * Remove items older than TimeToLive seconds from metrics map.
     */
    private void cleanMap() {
         long ts = getTimeStamp();
         long oldest = ts - timeToLive;
         while(timeKeeper.size() > 0 && timeKeeper.peek() < oldest) {
             metricsMap.remove(timeKeeper.poll());
         }
    }

}
