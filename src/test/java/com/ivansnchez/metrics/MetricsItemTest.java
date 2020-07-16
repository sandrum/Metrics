package com.ivansnchez.metrics;


import com.ivansanchez.metrics.Metrics;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class MetricsItemTest {

    private Metrics metrics;
    private final String testString1 = "Ivansanchez.com/home";
    private final String testString2 = "Ivansanchez.com/top";
    private final String testString3 = "Ivansanchez.com/API/getNewestPosts";

    @Before
    public void setup() {
        metrics = Metrics.getMetricsInstance();
    }

    @Test
    public void testMetricsPutAndGet() throws Exception {
        Thread.sleep(5000);

        addMetricsForTest1();

        Map<String, Long> result1 =
                metrics.getMetricsDataWithNameAndTime(testString1, 2);
        Map<String, Long> result2 =
                metrics.getMetricsDataWithNameAndTime(testString2, 2);
        Map<String, Long> result3 =
                metrics.getMetricsDataWithNameAndTime(testString3, 2);

        Map<String, Long> totalResults = metrics.getMetricsData();

        Assert.assertNotNull(result1);
        Assert.assertNotNull(result2);
        Assert.assertNotNull(result3);
        Assert.assertNotNull(totalResults);

        long res1 = result1.getOrDefault(testString1, 0L);
        long res2 = result2.getOrDefault(testString2, 0L);
        long res3 = result3.getOrDefault(testString3, 0L);
        long res4 = totalResults.getOrDefault(testString3, 0L);

        Assert.assertEquals(25L, res1);
        Assert.assertEquals(1L, res2);
        Assert.assertEquals(26L, res3);
        Assert.assertEquals(res4, res3);
        Thread.sleep(1000);

    }

    @Test
    public void testMetricsPutAndGetWithWait() throws Exception {
        Thread.sleep(5000);

        addMetricsForTest2();

        Map<String, Long> result1 =
                metrics.getMetricsDataWithNameAndTime(testString1, 2);

        Map<String, Long> totalResults = metrics.getMetricsData();

        Assert.assertNotNull(result1);
        Assert.assertNotNull(totalResults);

        long res1 = result1.getOrDefault(testString1, 0L);
        long res2 = totalResults.getOrDefault(testString1, 0L);

        Assert.assertEquals(20L, res1);
        Assert.assertNotEquals(res2, res1);
        Thread.sleep(1000);

    }

    @Test
    public void testMetricsPutAndGetWithWaitAndMultiThread() throws Exception {
        //set a wait here since metrics in the entire suite are collected here.
        Thread.sleep(7000);
        ExecutorService executorService = Executors.newCachedThreadPool();
        List<CallableTask> tasklist = new ArrayList<>();
        for(int i=0;i<5;i++) {
            tasklist.add(new CallableTask());
        }
        List<Future<String>> resultList = executorService.invokeAll(tasklist);
        for(Future<String> done: resultList){
            String dn = done.get();
            System.out.println(dn);
        }
        Map<String, Long> result1 =
                metrics.getMetricsDataWithNameAndTime(testString1, 7);
        Map<String, Long> result2 = metrics.getMetricsDataWithTime(7);
        long res1 = result1.getOrDefault(testString1, 0L);
        long res2 = result2.getOrDefault(testString1, 0L);
        Assert.assertEquals(res2, res1);
    }

    private void addMetricsForTest1() {
        metrics.putMetricData(testString1);
        metrics.putMetricData(testString2);
        metrics.putMetricData(testString3);
        metrics.putMetricData(testString3, 5);
        metrics.putMetricData(testString1, 20);
        metrics.putMetricData("Main thing", testString3, 20);
        metrics.putMetricData(testString1);
        metrics.putMetricData(testString1);
        metrics.putMetricData(testString1);
        metrics.putMetricData(testString1);
    }

    private void addMetricsForTest2() throws Exception {
        metrics.putMetricData(testString1);
        metrics.putMetricData(testString1);
        metrics.putMetricData(testString1);
        metrics.putMetricData(testString1);
        metrics.putMetricData(testString1);
        Thread.sleep(4000);
        metrics.putMetricData(testString1, 20);
    }

    class CallableTask implements Callable<String>
    {

        public CallableTask() {
        }

        @Override
        public String call() throws Exception
        {
            addMetricsForTest1();
            addMetricsForTest1();
            addMetricsForTest1();
            return "Done";
        }
    }

}
