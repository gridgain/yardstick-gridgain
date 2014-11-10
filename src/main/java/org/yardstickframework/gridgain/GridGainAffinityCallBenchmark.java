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

import org.yardstickframework.*;
import org.yardstickframework.gridgain.computemodel.*;

import java.util.*;

/**
 * GridGain benchmark that performs affinity call operations.
 */
public class GridGainAffinityCallBenchmark extends GridGainAbstractBenchmark {
    /** */
    public static final String CACHE_NAME = "compute";

    public GridGainAffinityCallBenchmark() {
        // Use cache "compute" for this benchmark. Configuration for the cache can be found
        // in 'config/gridgain-config.xml' file.
        super(CACHE_NAME);
    }

    /** {@inheritDoc} */
    @Override public void setUp(BenchmarkConfiguration cfg) throws Exception {
        super.setUp(cfg);
        for (int i = 0; i < args.nodes() * 2; ++i) {
            cache.putx(i, i);
        }
    }

    /** {@inheritDoc} */
    @Override public boolean test(Map<Object, Object> ctx) throws Exception {
        for (int key = 0; key < args.nodes() * 2; ++key) {
            grid().compute().affinityCall(CACHE_NAME, key, new SampleCallableJob()).get();
        }
        return true;
    }
}
