/*
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package org.yardstickframework.gridgain;

import org.gridgain.grid.*;
import org.gridgain.grid.cache.*;
import org.gridgain.grid.lang.*;
import org.gridgain.grid.spi.discovery.tcp.*;
import org.gridgain.grid.spi.discovery.tcp.ipfinder.vm.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

/**
 * Benchmark warmup closure.
 */
public class GridGainPutGetWarmupClosure implements GridInClosure<GridConfiguration> {
    /** {@inheritDoc} */
    @Override public void apply(GridConfiguration c) {
        GridConfiguration c0 = new GridConfiguration(c);

        c0.setGridName("warmup-" + UUID.randomUUID().toString());
        c0.setLocalHost("127.0.0.1");

        GridTcpDiscoverySpi discoSpi = new GridTcpDiscoverySpi();

        // Avoid any topology intersection.
        discoSpi.setIpFinder(new GridTcpDiscoveryVmIpFinder(true));
        discoSpi.setLocalPort(50000);

        c0.setDiscoverySpi(discoSpi);

        c0.setCommunicationSpi(null);
        c0.setLoadBalancingSpi();

        c0.setWarmupClosure(null);

        int cpuCnt = Runtime.getRuntime().availableProcessors();

        ExecutorService execSvc = Executors.newFixedThreadPool(cpuCnt);

        final AtomicBoolean finish = new AtomicBoolean();

        try (Grid g = GridGain.start(c0)) {
            for (int i = 0; i < cpuCnt; i++) {
                execSvc.submit(new Callable<Object>() {
                    @Override public Object call() throws Exception {
                        System.out.println("Starting warmup: " + Thread.currentThread().getId());

                        ThreadLocalRandom rnd = ThreadLocalRandom.current();

                        try {
                            while (!finish.get()) {
                                for (GridCache<?, ?> cache : g.caches()) {
                                    GridCache<Integer, SampleValue> cache0 = (GridCache<Integer, SampleValue>)cache;

                                    for (int j = 0; j < 1000; j++) {
                                        int key = rnd.nextInt();

                                        cache0.putx(key, new SampleValue(key));
                                        cache0.get(key);
                                        cache0.removex(key);
                                    }
                                }
                            }
                        }
                        finally {
                            System.out.println("Finished warmup: " + Thread.currentThread().getId());
                        }

                        return null;
                    }
                });
            }

            Thread.sleep(60_000);
        }
        catch (GridException e) {
            throw new RuntimeException(e);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            finish.set(true);

            execSvc.shutdownNow();

            try {
                execSvc.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Force GC after warming up.
        System.gc();
        System.gc();
        System.gc();
    }
}
