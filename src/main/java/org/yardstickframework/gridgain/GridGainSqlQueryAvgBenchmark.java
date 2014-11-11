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

    /** */
    public GridGainSqlQueryAvgBenchmark() {
        // Use cache "query" for this benchmark. Configuration for the cache can be found
        // in 'config/gridgain-config.xml' file.
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
                dataLdr.addData(i, new Person(i, "firstName" + i, "lastName" + i, i * 1000));

                if (i % 100000 == 0)
                    println(cfg, "Populated persons: " + i);
            }
        }

        println(cfg, "Finished populating query data in " + ((System.nanoTime() - start) / 1_000_000) + " ms.");

        qry = cache.queries().createSqlFieldsQuery("select avg(salary) from Person where salary >= ? and salary <= ?");
    }

    /** {@inheritDoc} */
    @Override public boolean test(Map<Object, Object> ctx) throws Exception {
        double salary = ThreadLocalRandom.current().nextDouble() * args.range() * 1000;

        double maxSalary = salary + 100 * 1000;

        double avgSalary = executeQuery(salary, maxSalary);

        if (avgSalary < salary || avgSalary > maxSalary)
            throw new Exception("Invalid avg salary calculated [min=" + salary + ", max=" + maxSalary +
                    ", avg=" + avgSalary + ']');

        return true;
    }

    /**
     * @param minSalary Min salary.
     * @param maxSalary Max salary.
     * @return Avg salary.
     * @throws Exception If failed.
     */
    private double executeQuery(double minSalary, double maxSalary) throws Exception {
        GridCacheQuery<List<?>> q = (GridCacheQuery<List<?>>)qry;

        return (Double) q.execute(minSalary, maxSalary).get().iterator().next().get(0);
    }
}
