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

import org.gridgain.grid.cache.query.*;
import org.gridgain.grid.dataload.*;
import org.yardstickframework.*;
import org.yardstickframework.gridgain.querymodel.*;

import java.util.*;
import java.util.concurrent.*;

import static org.yardstickframework.BenchmarkUtils.*;

/**
 * GridGain benchmark that performs aggregation query operations.
 */
public class GridGainSqlQueryAvgBenchmark extends GridGainAbstractBenchmark {
    /** */
    private GridCacheQuery qry;

    /**
     * Use cache "query" for this benchmark. Configuration for the cache can be found
     * in 'config/gridgain-config.xml' file.
     */
    public GridGainSqlQueryAvgBenchmark() {
        super("query");
    }

    /** */
    protected GridGainSqlQueryAvgBenchmark(String cacheName) {
        super(cacheName);
    }

    /** {@inheritDoc} */
    @Override public void setUp(BenchmarkConfiguration cfg) throws Exception {
        super.setUp(cfg);

        println(cfg, "Populating query data...");

        long start = System.nanoTime();

        try (GridDataLoader<Integer, Person> dataLdr = grid().dataLoader(cache.name())) {
            for (int i = 0; i < args.range() && !Thread.currentThread().isInterrupted(); i++) {
                //salary will be firmly clamped in a range (0, 1000]
                dataLdr.addData(i, new Person(i, "firstName" + i, "lastName" + i,
                        ThreadLocalRandom.current().nextDouble() * 1000));

                if (i % 100000 == 0)
                    println(cfg, "Populated persons: " + i);
            }
        }

        println(cfg, "Finished populating query data in " + ((System.nanoTime() - start) / 1_000_000) + " ms.");

        qry = cache.queries().createSqlFieldsQuery("select avg(salary) from Person where salary >= ?");
    }

    /** {@inheritDoc} */
    @Override public boolean test(Map<Object, Object> ctx) throws Exception {
        //salary range (0, 1000]. Aggregation query will work with near half values.
        double salary = ThreadLocalRandom.current().nextDouble(0.45, 0.55) * 1000;

        double avgSalary = executeQuery(salary);

        if (avgSalary < salary)
            throw new Exception("Invalid avg salary calculated [min=" + salary + ", avg=" + avgSalary + ']');

        return true;
    }

    /**
     * @param minSalary Min salary.
     * @return Avg salary.
     * @throws Exception If failed.
     */
    private double executeQuery(double minSalary) throws Exception {
        GridCacheQuery<List<?>> q = (GridCacheQuery<List<?>>)qry;

        return (Double) q.execute(minSalary).get().iterator().next().get(0);
    }
}
