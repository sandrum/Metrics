package com.ivansanchez.metrics;


/**
 * The metrics item encapsulates a metric as a value object in the metric map.
 * An instance of this object is created and updated as new metrics are created specific to
 * this instance's member values.
 **/
public class MetricsItem {
    /**The service's name.**/
    private String serviceName;
    /**The method name currently being executed in the service.**/
    private String operationName;
    /**The name of the metric being counted.**/
    private String name;
    /**The count of the metric.**/
    private long count;
    /*TODO: Assess possibility of logging/metricising latency. */

    public String getServiceName(){
        return this.serviceName;
    }

    public void setServiceName(final String sName) {
        this.serviceName = sName;
    }

    public String getOperationName() {
        return this.operationName;
    }

    public void setOperationName(final String oName) {
        this.operationName =  oName;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String n) {
        this.name = n;
    }

    public long getCount() {
        return this.count;
    }

    public void updateCount(final long c) {
        this.count += c;
    }
}
