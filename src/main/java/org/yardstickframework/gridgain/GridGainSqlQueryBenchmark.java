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
import org.gridgain.grid.lang.*;
import org.gridgain.grid.util.typedef.*;
import org.yardstickframework.*;
import org.yardstickframework.gridgain.querymodel.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * GridGain benchmark that performs query operations.
 */
public class GridGainSqlQueryBenchmark extends GridGainAbstractBenchmark {
    /** */
    private GridCacheQuery qry;

    /** */
    public GridGainSqlQueryBenchmark() {
        // Use cache "query" for this benchmark. Configuration for the cache can be found
        // in 'config/gridgain-config.xml' file.
        super("query");
    }

    /** {@inheritDoc} */
    @Override public void setUp(BenchmarkConfiguration cfg) throws Exception {
        super.setUp(cfg);

        cfg.output().println("Populating query data...");

        long start = System.nanoTime();

        try (GridDataLoader<Integer, Person> dataLdr = grid().dataLoader(cache.name())) {
            for (int i = 0; i < args.range() && !Thread.currentThread().isInterrupted(); i++) {
                dataLdr.addData(i, new Person(i, "firstName" + i, "lastName" + i, i * 1000));

                if (i % 100000 == 0)
                    cfg.output().println("Populated persons: " + i);
            }
        }

        cfg.output().println("Finished populating query data in " + ((System.nanoTime() - start) / 1_000_000) + " ms.");

        qry = cache.queries().createSqlQuery(Person.class, "salary >= ? and salary <= ?");
    }

    /** {@inheritDoc} */
    @Override public void test() throws Exception {
        double salary = ThreadLocalRandom.current().nextDouble() * args.range() * 1000;

        double maxSalary = salary + 1000;

        Collection<Person> persons = executeQuery(salary, maxSalary);

        for (Person p : persons) {
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
    private Collection<Person> executeQuery(double minSalary, double maxSalary) throws Exception {
        GridCacheQuery<Map.Entry<Integer, Person>> q = (GridCacheQuery<Map.Entry<Integer, Person>>)qry;

        Collection<Map.Entry<Integer, Person>> res = q.execute(minSalary, maxSalary).get();

        return F.viewReadOnly(res,
            new GridClosure<Map.Entry<Integer, Person>, Person>() {
                @Override public Person apply(Map.Entry<Integer, Person> e) {
                    return e.getValue();
                }
            }
        );
    }
}
