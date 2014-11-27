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

import org.yardstickframework.*;
import org.yardstickframework.gridgain.*;

import java.io.*;
import java.util.*;

/**
 * GridGain benchmark that performs run operations.
 */
public class GridGainRunBenchmark extends GridGainAbstractBenchmark {
    /** Jobs for run */
    private List<Runnable> jobs;

    /** {@inheritDoc} */
    @Override public void setUp(BenchmarkConfiguration cfg) throws Exception {
        super.setUp(cfg);

        assert args.jobs() > 0;

        jobs = new ArrayList<>(args.jobs());

        for (int i = 0; i < args.jobs(); ++i)
            jobs.add(new NoopRunnable());
    }

    /** {@inheritDoc} */
    @Override public boolean test(Map<Object, Object> ctx) throws Exception {
        grid().compute().run(jobs).get();
        
        return true;
    }

    /**
     *
     */
    public static class NoopRunnable implements Runnable, Externalizable {
        /** {@inheritDoc} */
        @Override public void run() {
            //No-op
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
