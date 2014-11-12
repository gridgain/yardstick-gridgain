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

package org.yardstickframework.gridgain.compute.model;

import org.gridgain.grid.*;
import org.gridgain.grid.compute.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static org.gridgain.grid.compute.GridComputeJobResultPolicy.*;

/**
 * Assigns {@link NoopJob "empty"} job for each node.
 */
public class JobMapperTask implements GridComputeTask<Object, Object> {
    /** Number of jobs */
    private int jobs;

    /**
     * @param jobs Number of jobs
     */
    public JobMapperTask(int jobs) {
        assert jobs > 0;

        this.jobs = jobs;
    }

    /** {@inheritDoc} */
    @Override public GridComputeJobResultPolicy result(
        GridComputeJobResult res,
        List<GridComputeJobResult> rcvd
    ) throws GridException {
        return WAIT;
    }

    /** {@inheritDoc} */
    @Nullable @Override public Map<? extends GridComputeJob, GridNode> map(
        List<GridNode> subgrid,
        @Nullable Object arg
    ) throws GridException {
        Map<GridComputeJob, GridNode> map = new HashMap<>((int)(subgrid.size() * jobs / 0.75));

        for (GridNode gridNode : subgrid) {
            //assigns jobs for each node
            for (int i = 0; i < jobs; ++i)
                map.put(new NoopJob(), gridNode);
        }

        return map;
    }

    /** {@inheritDoc} */
    @Nullable @Override public Object reduce(List<GridComputeJobResult> results) throws GridException {
        return null;
    }
}
