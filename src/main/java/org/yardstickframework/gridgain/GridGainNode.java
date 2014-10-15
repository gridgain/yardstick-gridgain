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
import org.gridgain.grid.cache.eviction.lru.*;
import org.gridgain.grid.spi.communication.tcp.*;
import org.gridgain.grid.util.*;
import org.springframework.beans.*;
import org.springframework.beans.factory.xml.*;
import org.springframework.context.support.*;
import org.springframework.core.io.*;
import org.yardstickframework.*;

import java.net.*;
import java.util.*;

import static org.gridgain.grid.cache.GridCacheDistributionMode.*;
import static org.gridgain.grid.cache.GridCacheMemoryMode.*;

/**
 * Standalone GridGain node.
 */
public class GridGainNode implements BenchmarkServer {
    /** Grid instance. */
    private Grid grid;

    /** Client mode. */
    private boolean clientMode;

    /** */
    public GridGainNode() {
        // No-op.
    }

    /** */
    public GridGainNode(boolean clientMode) {
        this.clientMode = clientMode;
    }

    /** */
    public GridGainNode(boolean clientMode, Grid grid) {
        this.clientMode = clientMode;
        this.grid = grid;
    }

    /** {@inheritDoc} */
    @Override public void start(BenchmarkConfiguration cfg) throws Exception {
        GridGainBenchmarkArguments args = new GridGainBenchmarkArguments();

        BenchmarkUtils.jcommander(cfg.commandLineArguments(), args, "<gridgain-node>");

        GridConfiguration c = loadConfiguration(args.configuration());

        assert c != null;

        for (GridCacheConfiguration cc : c.getCacheConfiguration()) {
            // GridGainNode can not run in CLIENT_ONLY mode,
            // except the case when it's used inside GridGainAbstractBenchmark.
            GridCacheDistributionMode distroMode = args.distributionMode() == CLIENT_ONLY && !clientMode ?
                PARTITIONED_ONLY : args.distributionMode();

            cc.setWriteSynchronizationMode(args.syncMode());
            cc.setDistributionMode(distroMode);

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

            cc.setDefaultTxConcurrency(args.txConcurrency());
            cc.setDefaultTxIsolation(args.txIsolation());
        }

        GridTcpCommunicationSpi commSpi = (GridTcpCommunicationSpi)c.getCommunicationSpi();

        if (commSpi == null)
            commSpi = new GridTcpCommunicationSpi();

        boolean asyncSnd = !args.isSyncSend();

        commSpi.setAsyncSend(asyncSnd);

        c.setCommunicationSpi(commSpi);

        grid = GridGain.start(c);
    }

    /**
     * @param springCfgPath Spring configuration file path.
     * @return Grid configuration.
     * @throws Exception If failed.
     */
    private static GridConfiguration loadConfiguration(String springCfgPath) throws Exception {
        URL url;

        try {
            url = new URL(springCfgPath);
        }
        catch (MalformedURLException e) {
            url = GridUtils.resolveGridGainUrl(springCfgPath);

            if (url == null)
                throw new GridException("Spring XML configuration path is invalid: " + springCfgPath +
                    ". Note that this path should be either absolute or a relative local file system path, " +
                    "relative to META-INF in classpath or valid URL to GRIDGAIN_HOME.", e);
        }

        GenericApplicationContext springCtx;

        try {
            springCtx = new GenericApplicationContext();

            new XmlBeanDefinitionReader(springCtx).loadBeanDefinitions(new UrlResource(url));

            springCtx.refresh();
        }
        catch (BeansException e) {
            throw new Exception("Failed to instantiate Spring XML application context [springUrl=" +
                url + ", err=" + e.getMessage() + ']', e);
        }

        Map<String, GridConfiguration> cfgMap;

        try {
            cfgMap = springCtx.getBeansOfType(GridConfiguration.class);
        }
        catch (BeansException e) {
            throw new Exception("Failed to instantiate bean [type=" + GridConfiguration.class + ", err=" +
                e.getMessage() + ']', e);
        }

        if (cfgMap == null || cfgMap.isEmpty())
            throw new Exception("Failed to find grid configuration in: " + url);

        return cfgMap.values().iterator().next();
    }

    /** {@inheritDoc} */
    @Override public void stop() throws Exception {
        GridGain.stopAll(true);
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
