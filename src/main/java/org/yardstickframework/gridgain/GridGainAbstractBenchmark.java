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
import org.gridgain.grid.events.*;
import org.gridgain.grid.lang.*;
import org.yardstickframework.*;

import java.util.concurrent.*;

import static org.gridgain.grid.cache.GridCacheDistributionMode.*;
import static org.gridgain.grid.events.GridEventType.*;
import static org.yardstickframework.BenchmarkUtils.*;

/**
 * Abstract class for GridGain benchmarks.
 */
public abstract class GridGainAbstractBenchmark extends BenchmarkDriverAdapter {
    /** Cache name. */
    private final String cacheName;

    /** Arguments. */
    protected final GridGainBenchmarkArguments args = new GridGainBenchmarkArguments();

    /** Node. */
    private GridGainNode node;

    /** Cache. */
    protected GridCache<Integer, Object> cache;

    /**
     * @param cacheName Cache name.
     */
    protected GridGainAbstractBenchmark(String cacheName) {
        this.cacheName = cacheName;
    }

    /** {@inheritDoc} */
    @Override public void setUp(BenchmarkConfiguration cfg) throws Exception {
        super.setUp(cfg);

        jcommander(cfg.commandLineArguments(), args, "<gridgain-driver>");

        node = new GridGainNode(args.distributionMode() == CLIENT_ONLY);

        node.start(cfg);

        cache = node.grid().cache(cacheName);

        assert cache != null;

        waitForNodes();
    }

    /** {@inheritDoc} */
    @Override public void tearDown() throws Exception {
        node.stop();
    }

    /** {@inheritDoc} */
    @Override public String description() {
        return cfg.description().isEmpty() ?
            cfg.driverName() + args.description() + cfg.defaultDescription() : cfg.description();
    }

    /** {@inheritDoc} */
    @Override public String usage() {
        return BenchmarkUtils.usage(args);
    }

    /**
     * @return Grid.
     */
    protected Grid grid() {
        return node.grid();
    }

    /**
     * @throws Exception If failed.
     */
    private void waitForNodes() throws Exception {
        final CountDownLatch nodesStartedLatch = new CountDownLatch(1);

        grid().events().localListen(new GridPredicate<GridEvent>() {
            @Override public boolean apply(GridEvent gridEvent) {
                if (nodesStarted())
                    nodesStartedLatch.countDown();

                return true;
            }
        }, EVT_NODE_JOINED);

        if (!nodesStarted()) {
            println(cfg, "Waiting for " + (args.nodes() - 1) + " nodes to start...");

            nodesStartedLatch.await();
        }
    }

    /**
     * @return {@code True} if all nodes are started, {@code false} otherwise.
     */
    private boolean nodesStarted() {
        return grid().nodes().size() >= args.nodes();
    }

    /**
     * @param max Key range.
     * @return Next key.
     */
    protected int nextRandom(int max) {
        return ThreadLocalRandom.current().nextInt(max);
    }

    /**
     * @param min Minimum key in range.
     * @param max Maximum key in range.
     * @return Next key.
     */
    protected int nextRandom(int min, int max) {
        return ThreadLocalRandom.current().nextInt(max - min) + min;
    }
}
