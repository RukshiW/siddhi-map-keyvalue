/*
 * Copyright (c)  2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.extension.siddhi.map.keyvalue.sourcemapper;

import org.apache.log4j.Logger;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.siddhi.core.SiddhiAppRuntime;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.stream.output.StreamCallback;
import org.wso2.siddhi.core.util.EventPrinter;
import org.wso2.siddhi.core.util.SiddhiTestHelper;
import org.wso2.siddhi.core.util.transport.InMemoryBroker;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class KeyValueSourceMapperTestCase {
    private static final Logger log = Logger.getLogger(KeyValueSourceMapper.class);
    private AtomicInteger count = new AtomicInteger();

    @BeforeMethod
    public void init() {
        count.set(0);
    }

    /*
    Default Mapping Data
     */
    @Test
    public void keyvalueSourceMapperDefaultTest1() throws InterruptedException {
        log.info("KeyValueSourceMapper-Default 1");

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "@source(type='inMemory', topic='stock', @map(type='keyvalue')) " +
                "define stream FooStream (symbol string, price float, volume long); " +
                "define stream BarStream (symbol string, price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);

        siddhiAppRuntime.addCallback("BarStream", new StreamCallback() {

            @Override
            public void receive(Event[] events) {
                EventPrinter.print(events);
                for (Event event : events) {
                    switch (count.incrementAndGet()) {
                        case 1:
                            AssertJUnit.assertEquals(55.6f, event.getData(1));
                            break;
                        case 2:
                            AssertJUnit.assertEquals(55.678f, event.getData(1));
                            break;
                        case 3:
                            AssertJUnit.assertEquals(100L, event.getData(2));
                            break;
                        case 4:
                            AssertJUnit.assertEquals("WSO2@#$%^*", event.getData(0));
                            break;
                        default:
                            AssertJUnit.fail("Received more than expected number of events. Expected maximum : 4," +
                                    "Received : " + count.get());
                    }
                }
            }
        });

        siddhiAppRuntime.start();

        HashMap<String, Object> msg1 = new HashMap<>();
        msg1.put("symbol", "WSO2");
        msg1.put("price", 55.6f);
        msg1.put("volume", 100L);
        InMemoryBroker.publish("stock", msg1);

        HashMap<String, Object> msg2 = new HashMap<>();
        msg2.put("symbol", "WSO2");
        msg2.put("price", 55.678f);
        msg2.put("volume", 100L);
        InMemoryBroker.publish("stock", msg2);

        HashMap<String, Object> msg3 = new HashMap<>();
        msg3.put("symbol", "WSO2");
        msg3.put("price", 55.678f);
        msg3.put("volume", 100L);
        InMemoryBroker.publish("stock", msg3);

        HashMap<String, Object> msg4 = new HashMap<>();
        msg4.put("symbol", "WSO2@#$%^*");
        msg4.put("price", 55.678f);
        msg4.put("volume", 100L);
        InMemoryBroker.publish("stock", msg4);

        SiddhiTestHelper.waitForEvents(100, 4, count, 200);

        //assert event count
        AssertJUnit.assertEquals("Number of events", 4, count.get());
        siddhiAppRuntime.shutdown();
    }

    @Test
    public void keyvalueSourceMapperDefaultTest2() throws InterruptedException {
        log.info("KeyValueSourceMapper-Default 1");

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "@source(type='inMemory', topic='stock', @map(type='keyvalue')) " +
                "define stream FooStream (symbol string, price float, volume long); " +
                "define stream BarStream (symbol string, price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);

        siddhiAppRuntime.addCallback("BarStream", new StreamCallback() {

            @Override
            public void receive(Event[] events) {
                EventPrinter.print(events);
                for (Event event : events) {
                    switch (count.incrementAndGet()) {
                        case 1:
                            AssertJUnit.assertEquals("WSO2", event.getData(0));
                            break;
                        default:
                            AssertJUnit.fail("Received more than expected number of events. Expected maximum : 1," +
                                    "Received : " + count.get());
                    }
                }
            }
        });

        siddhiAppRuntime.start();

        HashMap<String, Object> msg1 = new HashMap<>();
        msg1.put("symbol", "IBM");
        msg1.put("volume", 100L);
        InMemoryBroker.publish("stock", msg1);

        HashMap<String, Object> msg3 = new HashMap<>();
        msg3.put("price", 50.0f);
        msg3.put("volume", 100L);
        InMemoryBroker.publish("stock", msg3);

        HashMap<String, Object> msg4 = new HashMap<>();
        msg4.put("symbol", "WSO2");
        msg4.put("price", 50.0f);
        msg4.put("volume", 100L);
        InMemoryBroker.publish("stock", msg4);


        SiddhiTestHelper.waitForEvents(100, 1, count, 200);

        //assert event count
        AssertJUnit.assertEquals("Number of events", 1, count.get());
        siddhiAppRuntime.shutdown();
    }

    @Test
    public void keyvalueSourceMapperDefaultTest3() throws InterruptedException {
        log.info("KeyValueSourceMapper-Default 3");

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "@source(type='inMemory', topic='stock', @map(type='keyvalue' ,fail.on.missing.attribute='false')) " +
                "define stream FooStream (symbol string, price float, volume long); " +
                "define stream BarStream (symbol string, price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);

        siddhiAppRuntime.addCallback("BarStream", new StreamCallback() {

            @Override
            public void receive(Event[] events) {
                EventPrinter.print(events);
                for (Event event : events) {
                    switch (count.incrementAndGet()) {
                        case 1:
                            AssertJUnit.assertEquals(null, event.getData(0));
                            AssertJUnit.assertEquals(55.6f, event.getData(1));
                            AssertJUnit.assertEquals(100L, event.getData(2));
                            break;
                        case 2:
                            AssertJUnit.assertEquals(null, event.getData(1));
                            break;

                        case 3:
                            AssertJUnit.assertEquals(null, event.getData(0));
                            break;

                        default:
                            AssertJUnit.fail("Received more than expected number of events. Expected maximum : 3," +
                                    "Received : " + count.get());
                    }
                }
            }
        });

        siddhiAppRuntime.start();

        HashMap<String, Object> msg1 = new HashMap<>();
        msg1.put("symbol", null);
        msg1.put("price", 55.6f);
        msg1.put("volume", 100L);
        InMemoryBroker.publish("stock", msg1);

        HashMap<String, Object> msg2 = new HashMap<>();
        msg2.put("symbol", "WSO2");
        msg2.put("price", null);
        msg2.put("volume", 100L);
        InMemoryBroker.publish("stock", msg2);

        HashMap<String, Object> msg3 = new HashMap<>();
        msg3.put("price", 50.0f);
        msg3.put("volume", 100L);
        InMemoryBroker.publish("stock", msg3);

        String msg4 = "price: 40,volume: 100, price 55.6";
        InMemoryBroker.publish("stock", msg4);


        SiddhiTestHelper.waitForEvents(100, 3, count, 200);


        //assert event count
        AssertJUnit.assertEquals("Number of events", 3, count.get());
        siddhiAppRuntime.shutdown();
    }

    @Test
    public void keyvalueSourceMapperDefaultTest4() throws InterruptedException {
        log.info("KeyValueSourceMapper-Default 4");

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "@source(type='inMemory', topic='stock', @map(type='keyvalue' ,fail.on.missing.attribute='true')) " +
                "define stream FooStream (symbol string, price float, volume int, state bool, amount long); " +
                "define stream BarStream (symbol string, price float, volume int, state bool, amount long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);

        siddhiAppRuntime.addCallback("BarStream", new StreamCallback() {

            @Override
            public void receive(Event[] events) {
                EventPrinter.print(events);
                for (Event event : events) {
                    switch (count.incrementAndGet()) {
                        case 1:
                            AssertJUnit.assertEquals("WSO2", event.getData(0));
                            AssertJUnit.assertEquals(55.6f, event.getData(1));
                            AssertJUnit.assertEquals(100, event.getData(2));
                            AssertJUnit.assertEquals(false, event.getData(3));
                            AssertJUnit.assertEquals(300L, event.getData(4));
                            break;
                        default:
                            AssertJUnit.fail("Received more than expected number of events. Expected maximum : 1," +
                                    "Received : " + count.get());
                    }
                }
            }
        });

        siddhiAppRuntime.start();

        HashMap<String, Object> msg1 = new HashMap<>();
        msg1.put("state", false);
        msg1.put("symbol", "WSO2");
        msg1.put("price", 55.6f);
        msg1.put("volume", 100);
        msg1.put("amount", 300L);
        InMemoryBroker.publish("stock", msg1);

        HashMap<String, Object> msg2 = new HashMap<>();
        msg2.put("state", "false");
        msg2.put("symbol", "WSO2");
        msg2.put("price", 55.6f);
        msg2.put("volume", 100);
        msg2.put("amount", 300L);
        InMemoryBroker.publish("stock", msg2);

        HashMap<String, Object> msg3 = new HashMap<>();
        msg3.put("state", false);
        msg3.put("symbol", "WSO2");
        msg3.put("price", 55.6);
        msg3.put("volume", 100);
        msg3.put("amount", 300L);
        InMemoryBroker.publish("stock", msg3);

        HashMap<String, Object> msg4 = new HashMap<>();
        msg4.put("state", false);
        msg4.put("symbol", "WSO2");
        msg4.put("price", 55.6f);
        msg4.put("volume", 100.0f);
        msg4.put("amount", 300L);
        InMemoryBroker.publish("stock", msg4);

        HashMap<String, Object> msg5 = new HashMap<>();
        msg5.put("state", false);
        msg5.put("symbol", "WSO2");
        msg5.put("price", 55.6f);
        msg5.put("volume", 100);
        msg5.put("amount", 300);
        InMemoryBroker.publish("stock", msg5);

        SiddhiTestHelper.waitForEvents(100, 1, count, 200);


        //assert event count
        AssertJUnit.assertEquals("Number of events", 1, count.get());
        siddhiAppRuntime.shutdown();
    }

    /*
    Custom Mapping Data
     */
    @Test
    public void keyvalueSourceMapperCustomTest1() throws InterruptedException {
        log.info("test JsonSourceMapper 1");

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "@source(type='inMemory', topic='stock', " +
                "@map(type='keyvalue', fail.on.missing.attribute='true', " +
                "@attributes(symbol = 's', price = 'p', volume = 'v')))" +
                "define stream FooStream (symbol string, price float, volume long); " +
                "define stream BarStream (symbol string, price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);

        siddhiAppRuntime.addCallback("BarStream", new StreamCallback() {

            @Override
            public void receive(Event[] events) {
                EventPrinter.print(events);
                for (Event event : events) {
                    switch (count.incrementAndGet()) {
                        case 1:
                            AssertJUnit.assertEquals(55.6f, event.getData(1));
                            break;
                        case 2:
                            AssertJUnit.assertEquals(55.678f, event.getData(1));
                            break;
                        case 3:
                            AssertJUnit.assertEquals("WSO2@#$%^*", event.getData(0));
                            break;
                        default:
                            AssertJUnit.fail("Received more than expected number of events. Expected maximum : 3," +
                                    "Received : " + count.get());
                    }
                }
            }
        });

        siddhiAppRuntime.start();

        HashMap<String, Object> msg1 = new HashMap<>();
        msg1.put("s", "WSO2");
        msg1.put("p", 55.6f);
        msg1.put("v", 100L);
        InMemoryBroker.publish("stock", msg1);

        HashMap<String, Object> msg2 = new HashMap<>();
        msg2.put("s", "WSO2");
        msg2.put("p", 55.678f);
        msg2.put("v", 100L);
        InMemoryBroker.publish("stock", msg2);

        HashMap<String, Object> msg3 = new HashMap<>();
        msg3.put("s", "WSO2@#$%^*");
        msg3.put("p", 55.678f);
        msg3.put("v", 100L);
        InMemoryBroker.publish("stock", msg3);

        SiddhiTestHelper.waitForEvents(100, 3, count, 200);

        //assert event count
        AssertJUnit.assertEquals("Number of events", 3, count.get());
        siddhiAppRuntime.shutdown();
    }

    @Test
    public void keyvalueSourceMapperCustomTest2() throws InterruptedException {
        log.info("test JsonSourceMapper 1");

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "@source(type='inMemory', topic='stock', " +
                "@map(type='keyvalue', fail.on.missing.attribute='false', " +
                "@attributes(symbol = 's', price = 'p', volume = 'v')))" +
                "define stream FooStream (symbol string, price float, volume long); " +
                "define stream BarStream (symbol string, price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);

        siddhiAppRuntime.addCallback("BarStream", new StreamCallback() {

            @Override
            public void receive(Event[] events) {
                EventPrinter.print(events);
                for (Event event : events) {
                    switch (count.incrementAndGet()) {
                        case 1:

                            AssertJUnit.assertEquals(55.6f, event.getData(1));
                            break;
                        case 2:
                            AssertJUnit.assertEquals(null, event.getData(1));
                            break;
                        case 3:
                            AssertJUnit.assertEquals(null, event.getData(0));
                            break;
                        default:
                            AssertJUnit.fail("Received more than expected number of events. Expected maximum : 3," +
                                    "Received : " + count.get());
                    }
                }
            }
        });

        siddhiAppRuntime.start();

        HashMap<String, Object> msg1 = new HashMap<>();
        msg1.put("s", null);
        msg1.put("p", 55.6f);
        msg1.put("v", 100L);
        InMemoryBroker.publish("stock", msg1);

        HashMap<String, Object> msg2 = new HashMap<>();
        msg2.put("s", "WSO2");
        msg2.put("p", null);
        msg2.put("v", 100L);
        InMemoryBroker.publish("stock", msg2);

        HashMap<String, Object> msg3 = new HashMap<>();
        msg3.put("p", 55.678f);
        msg3.put("v", 100L);
        InMemoryBroker.publish("stock", msg3);
        SiddhiTestHelper.waitForEvents(100, 3, count, 200);
        //assert event count
        AssertJUnit.assertEquals("Number of events", 3, count.get());
        siddhiAppRuntime.shutdown();
    }

    @Test
    public void keyvalueSourceMapperCustomTest3() throws InterruptedException {
        log.info("test JsonSourceMapper 1");

        String streams = "" +
                "@App:name('TestSiddhiApp')" +
                "@source(type='inMemory', topic='stock', " +
                "@map(type='keyvalue', fail.on.missing.attribute='true', " +
                "@attributes(symbol = 's', price = 'p', volume = 'v')))" +
                "define stream FooStream (symbol string, price float, volume long); " +
                "define stream BarStream (symbol string, price float, volume long); ";

        String query = "" +
                "from FooStream " +
                "select * " +
                "insert into BarStream; ";

        SiddhiManager siddhiManager = new SiddhiManager();
        SiddhiAppRuntime siddhiAppRuntime = siddhiManager.createSiddhiAppRuntime(streams + query);

        siddhiAppRuntime.addCallback("BarStream", new StreamCallback() {

            @Override
            public void receive(Event[] events) {
                EventPrinter.print(events);
                for (Event event : events) {
                    switch (count.incrementAndGet()) {
                        case 1:
                            AssertJUnit.assertEquals("WSO2@#$%^*", event.getData(0));
                            AssertJUnit.assertEquals(55.678f, event.getData(1));
                            AssertJUnit.assertEquals(100L, event.getData(2));
                            break;
                        default:
                            AssertJUnit.fail("Received more than expected number of events. Expected maximum : 3," +
                                    "Received : " + count.get());
                    }
                }
            }
        });

        siddhiAppRuntime.start();

        HashMap<String, Object> msg1 = new HashMap<>();
        msg1.put("s", "WSO2");
        msg1.put("p", 55.6);
        msg1.put("v", 100L);
        InMemoryBroker.publish("stock", msg1);

        HashMap<String, Object> msg2 = new HashMap<>();
        msg2.put("s", "WSO2");
        msg2.put("p", 55.678f);
        msg2.put("v", 100);
        InMemoryBroker.publish("stock", msg2);

        HashMap<String, Object> msg3 = new HashMap<>();
        msg3.put("s", "WSO2@#$%^*");
        msg3.put("p", 55.678f);
        msg3.put("v", 100L);
        InMemoryBroker.publish("stock", msg3);

        SiddhiTestHelper.waitForEvents(100, 1, count, 200);

        //assert event count
        AssertJUnit.assertEquals("Number of events", 1, count.get());
        siddhiAppRuntime.shutdown();
    }

}
