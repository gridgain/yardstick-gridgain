# Yardstick GridGain Benchmarks
Yardstick GridGain is a set of <a href="http://www.gridgain.org/platform/data-grid" target="_blank">GridGain Data Grid</a> benchmarks written on top of Yardstick framework.

## Yardstick Framework
Visit <a href="https://github.com/gridgain/yardstick" target="_blank">Yardstick Repository</a> for detailed information on how to run Yardstick benchmarks and how to generate graphs.

The documentation below describes configuration parameters in addition to standard Yardstick parameters.

## Screenshots
![Generated Graph](http://www.gridgain.org/images/yardstick/gridgain/yardstick-gg-compound.png)
### Click on individual graphs to Zoom In
![Generated Graph](http://www.gridgain.org/images/yardstick/gridgain/yardstick-gg-compound-magnified.png)

## Installation
1. Create a local clone of Yardstick GridGain repository
2. Import Yardstick GridGain POM file into your project
3. Run `mvn package` command

## Provided Benchmarks
The following benchmarks are provided:

1. Put
2. Put And Get
3. Transactional Put
4. Transactional Put And Get
5. SQL Query
6. SQL Query With Put

## Writing GridGain Benchmarks
All benchmarks extend `GridGainAbstractBenchmark` class. A new benchmark should also extend this abstract class and implement `test` method. This is the method that is actually benchmarked.

## Running GridGain Benchmarks
Before running GridGain benchmarks, run `mvn package` command. This command will compile the project and also will unpack scripts from `yardstick-resources.zip` file to `bin` directory.

### Properties And Command Line Arguments
> Note that this section only describes configuration parameters specific to GridGain benchmarks, and not for Yardstick framework. To run GridGain benchmarks and generate graphs, you will need to run them using Yardstick framework scripts in `bin` folder.

> Refer to [Yardstick Documentation](https://github.com/gridgain/yardstick) for common Yardstick properties and command line arguments for running Yardstick scripts.

The following GridGain benchmark properties can be defined in the benchmark configuration:

* `-nn <num>` or `--nodeNumber <num>` - Number of nodes (automatically set in `benchmark.properties`), used to wait for the specified number of nodes to start
* `-b <num>` or `--backups <num>` - Number of backups for every key
* `-ggcfg <path>` or `--ggConfig <path>` - Path to GridGain configuration file
* `-sm <mode>` or `-syncMode <mode>` - GridGain synchronization mode (defined in `GridCacheWriteSynchronizationMode`)
* `-dm <mode>` or `--distroMode <mode>` - GridGain distribution mode (defined in `GridCacheDistributionMode`)
* `-wom <mode>` or `--writeOrderMode <mode>` - GridGain write order mode for ATOMIC caches (defined in `GridCacheAtomicWriteOrderMode`)
* `-txc <value>` or `--txConcurrency <value>` - GridGain cache transaction concurrency control, either `OPTIMISTIC` or `PESSIMISTIC` (defined in `GridCacheTxConcurrency`)
* `-txi <value>` or `--txIsolation <value>` - GridGain cache transaction isolation (defined in `GridCacheTxIsolation`)
* `-ot` or `--offheapTiered` - Flag indicating whether tiered off-heap mode is on
* `-ov` or `--offheapValuesOnly` - Flag indicating whether off-heap mode is on and only cache values are stored off-heap
* `-rtp <num>`  or `--restPort <num>` - REST TCP port, indicates that a GridGain node is ready to process GridGain Clients
* `-rth <host>` or `--restHost <host>` - REST TCP host
* `-ss` or `--syncSend` - Flag indicating whether synchronous send is used in `GridTcpCommunicationSpi`
* `-r <num>` or `--range` - Range of keys that are randomly generated for cache operations

For example if we need to run 2 `GridGainNode` servers on localhost with `GridGainPutBenchmark` benchmark on localhost, with number of backups set to 1, synchronization mode set to `PRIMARY_SYNC`, then the following configuration should be specified in `benchmark.properties` file:

```
HOSTS=localhost,localhost
    
# Note that -dn and -sn, which stand for data node and server node, are 
# native Yardstick parameters and are documented in Yardstick framework.
CONFIGS="-b 1 -sm PRIMARY_SYNC -dn GridGainPutBenchmark -sn GridGainNode"
```

## Issues
Use GitHub [issues](https://github.com/gridgain/yardstick-gridgain/issues) to file bugs.

## License
Yardstick GridGain is available under [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0.html) Open Source license.
