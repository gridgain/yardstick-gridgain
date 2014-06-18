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

import com.beust.jcommander.*;
import org.gridgain.grid.cache.*;
import org.gridgain.grid.util.typedef.internal.*;

/**
 * Input arguments for GridGain benchmarks.
 */
@SuppressWarnings({"UnusedDeclaration", "FieldCanBeLocal"})
public class GridGainBenchmarkArguments {
    /** */
    @Parameter(names = {"-nn", "--nodeNumber"}, description = "Node number")
    private int nodes = 1;

    /** */
    @Parameter(names = {"-b", "--backups"}, description = "Backups")
    private int backups;

    @Parameter(names = {"-ggcfg", "--ggConfig"}, description = "Configuration file")
    private String ggcfg = "config/gridgain-localhost-config.xml";

    /** */
    @Parameter(names = {"-sm", "--syncMode"}, description = "Synchronization mode")
    private GridCacheWriteSynchronizationMode syncMode = GridCacheWriteSynchronizationMode.PRIMARY_SYNC;

    /** */
    @Parameter(names = {"-dm", "--distroMode"}, description = "Distribution mode")
    private GridCacheDistributionMode distroMode = GridCacheDistributionMode.PARTITIONED_ONLY;

    /** */
    @Parameter(names = {"-wom", "--writeOrderMode"}, description = "Write ordering mode")
    private GridCacheAtomicWriteOrderMode orderMode;

    /** */
    @Parameter(names = {"-txc", "--txConcurrency"}, description = "Transaction concurrency")
    private GridCacheTxConcurrency txConcurrency = GridCacheTxConcurrency.OPTIMISTIC;

    /** */
    @Parameter(names = {"-txi", "--txIsolation"}, description = "Transaction isolation")
    private GridCacheTxIsolation txIsolation = GridCacheTxIsolation.REPEATABLE_READ;

    /** */
    @Parameter(names = {"-ot", "--offheapTiered"}, description = "Tiered offheap")
    private boolean offheapTiered;

    /** */
    @Parameter(names = {"-ov", "--offheapValuesOnly"}, description = "Offheap values only")
    private boolean offheapVals;

    /** */
    @Parameter(names = {"-rtp", "--restPort"}, description = "REST TCP port")
    private int restTcpPort;

    /** */
    @Parameter(names = {"-rth", "--restHost"}, description = "REST TCP host")
    private String restTcpHost;

    /** */
    @Parameter(names = {"-ss", "--syncSend"}, description = "Synchronous send")
    private boolean syncSnd;

    /** */
    @Parameter(names = {"-r", "--range"}, description = "Key range")
    private int range = 1_000_000;

    /**
     * @return Transaction concurrency.
     */
    public GridCacheTxConcurrency txConcurrency() {
        return txConcurrency;
    }

    /**
     * @return Transaction isolation.
     */
    public GridCacheTxIsolation txIsolation() {
        return txIsolation;
    }

    /**
     * @return REST TCP port.
     */
    public int restTcpPort() {
        return restTcpPort;
    }

    /**
     * @return REST TCP host.
     */
    public String restTcpHost() {
        return restTcpHost;
    }

    /**
     * @return Distribution.
     */
    public GridCacheDistributionMode distributionMode() {
        return distroMode;
    }

    /**
     * @return Synchronization.
     */
    public GridCacheWriteSynchronizationMode syncMode() {
        return syncMode;
    }

    /**
     * @return Cache write ordering mode.
     */
    public GridCacheAtomicWriteOrderMode orderMode() {
        return orderMode;
    }

    /**
     * @return Backups.
     */
    public int backups() {
        return backups;
    }

    /**
     * @return Offheap tiered.
     */
    public boolean isOffheapTiered() {
        return offheapTiered;
    }

    /**
     * @return Offheap values.
     */
    public boolean isOffheapValues() {
        return offheapVals;
    }

    /**
     * @return {@code True} if any offheap is enabled.
     */
    public boolean isOffHeap() {
        return offheapTiered || offheapVals;
    }

    /**
     * @return Nodes.
     */
    public int nodes() {
        return nodes;
    }

    /**
     * @return {@code True} if sending is synchronous.
     */
    public boolean isSyncSend() {
        return syncSnd;
    }

    /**
     * @return Key range, from {@code 0} to this number.
     */
    public int range() {
        return range;
    }

    /**
     * @return Configuration file.
     */
    public String configuration() {
        return ggcfg;
    }

    /**
     * @return Description.
     */
    public String description() {
        return "-nn=" + nodes + "-b=" + backups + "-sm=" + syncMode + "-dm=" + distroMode +
            (orderMode == null ? "" : "-wom=" + orderMode) + "-txc=" + txConcurrency;
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return S.toString(GridGainBenchmarkArguments.class, this);
    }
}
