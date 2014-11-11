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
 * GridGain benchmark that performs call operations.
 */
public class GridGainCallBenchmark extends GridGainAbstractBenchmark {
    /** */
    private List<NoopCallable> calls;

    public GridGainCallBenchmark() {
        // Use cache "compute" for this benchmark. Configuration for the cache can be found
        // in 'config/gridgain-config.xml' file.
        super("compute");
    }

    /** {@inheritDoc} */
    @Override public void setUp(BenchmarkConfiguration cfg) throws Exception {
        super.setUp(cfg);
        calls = new ArrayList<>(args.nodes() * 2);
        for (int i = 0; i < args.nodes() * 2; ++i)
            calls.add(new NoopCallable());
    }

    /** {@inheritDoc} */
    @Override public boolean test(Map<Object, Object> ctx) throws Exception {
        grid().compute().call(calls).get();

        return true;
    }
}
