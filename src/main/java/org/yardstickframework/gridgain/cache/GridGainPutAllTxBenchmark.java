/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.yardstickframework.gridgain.cache;

import org.gridgain.grid.*;
import org.gridgain.grid.cache.*;
import org.gridgain.grid.cache.affinity.*;
import org.yardstickframework.*;

import java.util.*;

/**
 *
 */
public class GridGainPutAllTxBenchmark extends GridGainCacheAbstractBenchmark {
    /** Affinity mapper. */
    private GridCacheAffinity<Integer> aff;

    /** {@inheritDoc} */
    @Override public void setUp(BenchmarkConfiguration cfg) throws Exception {
        super.setUp(cfg);

        aff = grid().<Integer, Integer>cache(args.cacheName()).affinity();
    }

    /** {@inheritDoc} */
    @Override public boolean test(Map<Object, Object> ctx) throws Exception {
        SortedMap<Integer, Integer> vals = new TreeMap<>();

        GridNode node = args.collocated() ? aff.mapKeyToNode(nextRandom(args.range())) : null;

        for (int i = 0; i < args.batchSize(); ) {
            int key = nextRandom(args.range());

            if (args.collocated() && !aff.isPrimary(node, key))
                continue;

            ++i;

            vals.put(key, key);
        }

        try (GridCacheTx tx = cache.txStart(GridCacheTxConcurrency.PESSIMISTIC, GridCacheTxIsolation.REPEATABLE_READ)) {
            cache.getAll(vals.keySet());

            cache.putAll(vals);

            tx.commit();
        }

        return true;
    }

    /** {@inheritDoc} */
    @Override protected GridCache<Integer, Object> cache() {
        return grid().cache(args.cacheName());
    }
}
