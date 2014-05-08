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

package org.yardstick.gridgain;

import org.gridgain.grid.*;
import org.gridgain.grid.cache.*;
import org.gridgain.grid.cache.eviction.lru.*;
import org.gridgain.grid.spi.communication.tcp.*;
import org.gridgain.grid.util.typedef.*;
import org.yardstick.*;
import org.yardstick.impl.util.*;

import static org.gridgain.grid.cache.GridCacheMemoryMode.*;

/**
 * Standalone GridGain node.
 */
public class GridGainNode implements BenchmarkServer {
    /** Grid instance. */
    private Grid grid;

    /** {@inheritDoc} */
    @Override public void start(BenchmarkConfiguration cfg) throws Exception {
        GridGainBenchmarkArguments args = new GridGainBenchmarkArguments();

        BenchmarkUtils.jcommander(cfg.commandLineArguments(), args, "<gridgain-node>");

        GridConfiguration c = G.loadConfiguration(args.configuration()).get1();

        assert c != null;

        for (GridCacheConfiguration cc : c.getCacheConfiguration()) {
            cc.setWriteSynchronizationMode(args.syncMode());
            cc.setDistributionMode(args.distributionMode());

            if (args.orderMode() != null)
                cc.setAtomicWriteOrderMode(args.orderMode());

            cc.setBackups(args.backups());

            if (args.restTcpPort() != 0) {
                c.setRestEnabled(true);
                c.setRestTcpPort(args.restTcpPort());

                if (args.restTcpHost() != null)
                    c.setRestTcpHost(args.restTcpHost());
            }

            if (args.isOffHeap()) {
                cc.setOffHeapMaxMemory(0);

                if (args.isOffheapValues())
                    cc.setMemoryMode(OFFHEAP_VALUES);
                else
                    cc.setEvictionPolicy(new GridCacheLruEvictionPolicy(50000));
            }
        }

        GridTcpCommunicationSpi commSpi = (GridTcpCommunicationSpi)c.getCommunicationSpi();

        if (commSpi == null)
            commSpi = new GridTcpCommunicationSpi();

        boolean asyncSnd = !args.isSyncSend();

        commSpi.setAsyncSend(asyncSnd);

        c.setCommunicationSpi(commSpi);

        grid = G.start(c);
    }

    /** {@inheritDoc} */
    @Override public void stop() throws Exception {
        G.stopAll(true);
    }

    /** {@inheritDoc} */
    @Override public String usage() {
        return BenchmarkUtils.usage(new GridGainBenchmarkArguments());
    }

    /**
     * @return Grid.
     */
    public Grid grid() {
        return grid;
    }
}
