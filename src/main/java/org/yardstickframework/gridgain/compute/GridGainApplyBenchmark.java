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

package org.yardstickframework.gridgain.compute;

import org.gridgain.grid.lang.*;
import org.yardstickframework.*;
import org.yardstickframework.gridgain.*;

import java.io.*;
import java.util.*;

/**
 * GridGain benchmark that performs apply operations.
 */
public class GridGainApplyBenchmark extends GridGainAbstractBenchmark {
    /** Args for apply. */
    private List<Integer> applyArgs;

    /** {@inheritDoc} */
    @Override public void setUp(BenchmarkConfiguration cfg) throws Exception {
        super.setUp(cfg);

        assert args.jobs() > 0;

        applyArgs = new ArrayList<>(args.jobs());

        for (int i = 0; i < args.jobs(); ++i)
            applyArgs.add(null);
    }

    /** {@inheritDoc} */
    @Override public boolean test(Map<Object, Object> ctx) throws Exception {
        grid().compute().apply(new NoopClosure(), applyArgs).get();
        
        return true;
    }

    /**
     *
     */
    public static class NoopClosure implements GridClosure<Integer, Object>, Externalizable {
        /** {@inheritDoc} */
        @Override public Object apply(Integer o) {
            return null;
        }

        /** {@inheritDoc} */
        @Override public void writeExternal(ObjectOutput out) throws IOException {
            //No-op
        }

        /** {@inheritDoc} */
        @Override public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            //No-op
        }
    }
}
