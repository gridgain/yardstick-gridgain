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

import org.gridgain.grid.cache.*;
import org.gridgain.grid.dataload.*;
import org.yardstickframework.*;

import java.math.*;
import java.util.*;

import static org.gridgain.grid.cache.GridCacheTxConcurrency.*;
import static org.gridgain.grid.cache.GridCacheTxIsolation.*;
import static org.yardstickframework.BenchmarkUtils.*;

/**
 * GridGain benchmark that performs bulk update operation.
 */
public class GridGainBulkUpdateBenchmark extends GridGainCacheAbstractBenchmark<String, BigDecimal> {
    /** */
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

        println(cfg, "Finished populating data in " + ((System.nanoTime() - start) / 1_000_000) + " ms.");
    }

    /** {@inheritDoc} */
    @Override public boolean test(Map<Object, Object> ctx) throws Exception {
        Map<String, BigDecimal> changesMap = generateBatch();

        try (GridCacheTx tx = cache().txStart(PESSIMISTIC, REPEATABLE_READ)) {
            final Map<String, BigDecimal> oldVals = cache().getAll(changesMap.keySet());

            final Map<String, BigDecimal> newVals = new HashMap<>(oldVals.size());

            for (Map.Entry<String, BigDecimal> ent : oldVals.entrySet())
                newVals.put(ent.getKey(), ent.getValue().add(changesMap.get(ent.getKey())));

            cache().putAll(newVals);

            tx.commit();
        }

        return true;
    }

    /**
     * @return Batch.
     */
    private Map<String, BigDecimal> generateBatch() {
        SortedMap<String, BigDecimal> batch = new TreeMap<>();

        while (batch.size() < args.batchSize()) {
            String key = String.valueOf(nextRandom(SHIFT, SHIFT + args.range()));

            batch.put(key, BigDecimal.valueOf(nextRandom(1000)));
        }

        return batch;
    }

    /** {@inheritDoc} */
    @Override protected GridCache<String, BigDecimal> cache() {
        return grid().cache("tx");
    }
}
