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

package org.yardstickframework.gridgain.cache;

import org.gridgain.grid.cache.GridCache;
import org.gridgain.grid.cache.GridCacheTx;
import org.gridgain.grid.dataload.GridDataLoader;
import org.yardstickframework.BenchmarkConfiguration;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.yardstickframework.BenchmarkUtils.println;

/**
 * GridGain benchmark that performs bulk update operation with integer key.
 */
public class GridGainBulkUpdateIntegerBenchmark extends GridGainCacheAbstractBenchmark<Integer, BigDecimal> {

    public static final int SHIFT = 1_000_000;

    /** {@inheritDoc} */
    @Override public void setUp(BenchmarkConfiguration cfg) throws Exception {
        super.setUp(cfg);

        println(cfg, "Populating data...");

        long start = System.nanoTime();

        try (GridDataLoader<String, BigDecimal> dataLdr = grid().dataLoader(cache.name())) {
            for (int i = 0; i < args.range() && !Thread.currentThread().isInterrupted(); i++) {
                dataLdr.addData(String.valueOf(SHIFT + i), BigDecimal.valueOf(i));

                if (i % 100000 == 0)
                    println(cfg, "Populated cache: " + i);
            }
        }

        println(cfg, "Finished populating data in  " + ((System.nanoTime() - start) / 1_000_000) + " ms.");
    }

    /** {@inheritDoc} */
    @Override public boolean test(Map<Object, Object> ctx) throws Exception {
        Map<Integer, BigDecimal> changesMap = generateBatch();

        try (GridCacheTx tx = cache().txStart()) {
            final Map<Integer, BigDecimal> oldVals = cache().getAll(changesMap.keySet());

            final Map<Integer, BigDecimal> newVals = new HashMap<>(oldVals.size());

            for (Map.Entry<Integer, BigDecimal> ent: oldVals.entrySet()) {
                newVals.put(ent.getKey(), ent.getValue().add(changesMap.get(ent.getKey())));
            }

            cache().putAll(newVals);

            tx.commit();
        }

        return true;
    }

    /**
     * @return Batch
     */
    private Map<Integer, BigDecimal> generateBatch() {
        Map<Integer, BigDecimal> batch = new HashMap<>();

        while (batch.size() < args.batchSize()) {
            int key = nextRandom(SHIFT, SHIFT + args.range());

            batch.put(key, BigDecimal.valueOf(nextRandom(SHIFT, SHIFT + args.range())));
        }

        return batch;
    }

    /** {@inheritDoc} */
    @Override protected GridCache<Integer, BigDecimal> cache() {
        return grid().cache("tx");
    }
}
