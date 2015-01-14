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
import org.yardstickframework.gridgain.cache.model.*;

import java.util.*;
import java.util.concurrent.atomic.*;

/**
 * GridGain benchmark that performs data loader operations.
 */
public class GridGainLoaderObjectBenchmark extends GridGainCacheAbstractBenchmark {
    /** */
    private GridDataLoader<Integer, PortableSampleValue> dataLoader;

    /** */
    private AtomicInteger identityGenerator = new AtomicInteger(0);

    /** {@inheritDoc} */
    @Override public void setUp(BenchmarkConfiguration cfg) throws Exception {
        super.setUp(cfg);

        dataLoader = grid().dataLoader(cache.name());

        dataLoader.perNodeParallelLoadOperations(2);

        dataLoader.perNodeBufferSize(1000);
    }

    /** {@inheritDoc} */
    @Override public boolean test(Map<Object, Object> ctx) throws Exception {
        Integer lastKey = (Integer) ctx.get(0);

        if (lastKey == null)
            lastKey = identityGenerator.getAndIncrement();

        for (int i = 0; i < args.range(); i++) {
            dataLoader.addData(lastKey, new PortableSampleValue(lastKey, lastKey.toString()));

            lastKey += cfg.threads();
        }

        ctx.put(0, lastKey);

        return true;
    }

    /** {@inheritDoc} */
    @Override public void tearDown() throws Exception {
        dataLoader.close();
    }

    /** {@inheritDoc} */
    @Override protected GridCache<Integer, Object> cache() {
        return grid().cache("atomic-portable");
    }
}
