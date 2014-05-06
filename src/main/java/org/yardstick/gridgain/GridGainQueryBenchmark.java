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

import org.gridgain.grid.cache.query.*;
import org.gridgain.grid.dataload.*;
import org.gridgain.grid.lang.*;
import org.gridgain.grid.util.typedef.*;
import org.yardstick.*;
import org.yardstick.gridgain.querymodel.*;

import java.util.*;

/**
 * GridGain benchmark that performs query operations.
 */
public class GridGainQueryBenchmark extends GridGainAbstractBenchmark {
    /** */
    private GridCacheQuery qry;

    /** */
    public GridGainQueryBenchmark() {
        // Use cache "query" for this benchmark. Configuration for the cache can be found
        // in 'config/gridgain-benchmark-config.xml' file.
        super("query");
    }

    /** {@inheritDoc} */
    @Override public void setUp(BenchmarkConfiguration cfg) throws Exception {
        super.setUp(cfg);

        cfg.output().println("Populating query data...");

        long start = System.nanoTime();

        try (GridDataLoader<Integer, GridGainBenchmarkPerson> dataLdr = grid().dataLoader(cache.name())) {
            for (int i = 0; i < args.range() && !Thread.currentThread().isInterrupted(); i++) {
                dataLdr.addData(i, new GridGainBenchmarkPerson(i, "firstName" + i, "lastName" + i, i * 1000));

                if (i % 100000 == 0)
                    cfg.output().println("Populated persons: " + i);
            }
        }

        cfg.output().println("Finished populating query data in " + ((System.nanoTime() - start) / 1_000_000) + " ms.");

        qry = cache.queries().createSqlQuery(GridGainBenchmarkPerson.class, "salary >= ? and salary <= ?");
    }

    /** {@inheritDoc} */
    @Override public void test() throws Exception {
        double salary = RAND.nextDouble() * args.range() * 1000;

        double maxSalary = salary + 1000;

        Collection<GridGainBenchmarkPerson> persons = executeQuery(salary, maxSalary);

        for (GridGainBenchmarkPerson p : persons) {
            if (p.getSalary() < salary || p.getSalary() > maxSalary)
                throw new Exception("Invalid person retrieved [min=" + salary + ", max=" + maxSalary +
                    ", person=" + p + ']');
        }
    }

    /**
     * @param minSalary Min salary.
     * @param maxSalary Max salary.
     * @return Query result.
     * @throws Exception If failed.
     */
    private Collection<GridGainBenchmarkPerson> executeQuery(double minSalary, double maxSalary) throws Exception {
        GridCacheQuery<Map.Entry<Integer, GridGainBenchmarkPerson>> q = (GridCacheQuery<Map.Entry<Integer, GridGainBenchmarkPerson>>)qry;

        Collection<Map.Entry<Integer, GridGainBenchmarkPerson>> res = q.execute(minSalary, maxSalary).get();

        return F.viewReadOnly(res,
            new GridClosure<Map.Entry<Integer, GridGainBenchmarkPerson>, GridGainBenchmarkPerson>() {
                @Override public GridGainBenchmarkPerson apply(Map.Entry<Integer, GridGainBenchmarkPerson> e) {
                    return e.getValue();
                }
            }
        );
    }
}
