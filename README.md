## Yardstick GridGain
Yardstick GridGain is the set of GridGain benchmarks written by using Yardstick framework.
For more information about Yardstick framework, how to run it, build graphs and etc.
go to its own [repository](https://github.com/gridgain/yardstick).
For more information about GridGain In-Memory Data Grid visit [www.gridgain.org](http://www.gridgain.org).

## How to write your own GridGain benchmark
All benchmarks extend `GridGainAbstractBenchmark` class. A new benchmark should also extend this
abstract class and implement `test` method. This is the method that is actually benchmarked.

## How to run GridGain benchmarks
Before the run the project should be compiled and the jar file is built. This is done by command `mvn package`.
Also this command unpacks benchmark scripts from `yardstick-resources.zip` file to `bin` directory.
The procedure of benchmarks run is the same as described in Yardstick
[documentation](https://github.com/gridgain/yardstick).

### Properties and command line arguments

The following GridGain benchmark properties can be defined in the benchmark configuration:

* `-nn <num>` or `--nodeNumber <num>` - number of nodes, it is used by the benchmark driver to wait for the specified number of nodes are started,
    to not start the driver while not all nodes are ready
* `-b <num>` or `--backups <num>` - number of backups
* `-ggcfg <path>` or `--ggConfig <path>` - GridGain configuration file
* `-sm <mode>` or `-syncMode <mode>` - GridGain synchronization mode
* `-dm <mode>` or `--distroMode <mode>` - GridGain distribution mode
* `-wom <mode>` or `--writeOrderMode <mode>` - GridGain write order mode
* `-txc <value>` or `--txConcurrency <value>` - GridGain transaction concurrency control
* `-txi <value>` or `--txIsolation <value>` - GridGain transaction isolation
* `-ot` or `--offheapTiered` - flag indicating whether offheap mode is on
* `-ov` or `--offheapValuesOnly` - flag indicating whether offheap mode is on and only cache values are stored offheap
* `-rtp <num>`  or `--restPort <num>` - REST TCP port, 
if this property is defined it indicates that a GridGain node is ready to process GridGain Clients
* `-rth <host>` or `--restHost <host>` - REST TCP host
* `-ss` or `--syncSend` - flag indicating whether synchronous send is used in `GridTcpCommunicationSpi`
* `-r <num>` or `--range` - range of keys that are randomly generated for cache operations

For example if we need to run GridGainNode server on localhost and GridGainPutBenchmark benchmark on localhost, 
number of backups is 1, synchronization mode is PRIMARY_SYNC 
then the following configuration should be specified in run properties file:

* `HOSTS=localhost` 
* `CONFIGS="-b 1 -sm PRIMARY_SYNC -dn GridGainPutBenchmark -sn GridGainNode"`

## Maven Install
The easiest way to get started with Yardstick GridGain in your project is to use Maven dependency management:

```xml
<dependency>
    <groupId>org.yardstick.gridgain</groupId>
    <artifactId>yardstick-gridgain</artifactId>
    <version>${yardstick-gridgain.version}</version>
</dependency>
```

You can copy and paste this snippet into your Maven POM file. Make sure to replace version with the one you need.

## Issues
Use GitHub [issues](https://github.com/gridgain/yardstick-gridgain/issues) to file bugs.

## License
Yardstick GridGain is available under [Apache 2.0](http://www.apache.org/licenses/LICENSE-2.0.html) license.
