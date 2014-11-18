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
import org.yardstickframework.*;
import org.yardstickframework.gridgain.querymodel.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * GridGain benchmark that performs put and query operations.
 */
public class GridGainSqlQueryPutBenchmark extends GridGainAbstractBenchmark {
    /** */
    private GridCacheQuery qry;

    /** */
    public GridGainSqlQueryPutBenchmark() {
        // Use cache "query" for this benchmark. Configuration for the cache can be found
        // in 'config/gridgain-config.xml' file.
        super("query");
    }

    /** */
    protected GridGainSqlQueryPutBenchmark(String cacheName) {
        super(cacheName);
    }

    /** {@inheritDoc} */
    @Override public void setUp(BenchmarkConfiguration cfg) throws Exception {
        super.setUp(cfg);

        qry = cache.queries().createSqlQuery(Person.class, "salary >= ? and salary <= ?");
    }

    /** {@inheritDoc} */
    @Override public boolean test(Map<Object, Object> ctx) throws Exception {
        ThreadLocalRandom rnd = ThreadLocalRandom.current();

        if (rnd.nextBoolean()) {
            double salary = rnd.nextDouble() * args.range() * 1000;

            double maxSalary = salary + 1000;

            Collection<Map.Entry<Integer, Person>> entries = executeQuery(salary, maxSalary);

            for (Map.Entry<Integer, Person> entry : entries) {
                Person p = entry.getValue();

                if (p.getSalary() < salary || p.getSalary() > maxSalary)
                    throw new Exception("Invalid person retrieved [min=" + salary + ", max=" + maxSalary +
                            ", person=" + p + ']');
            }
        }
        else {
            int i = rnd.nextInt(args.range());

            cache.putx(i, new Person(i, "firstName" + i, "lastName" + i, i * 1000));
        }

        return true;
    }

    /**
     * @param minSalary Min salary.
     * @param maxSalary Max salary.
     * @return Query result.
     * @throws Exception If failed.
     */
    private Collection<Map.Entry<Integer, Person>> executeQuery(double minSalary, double maxSalary) throws Exception {
        GridCacheQuery<Map.Entry<Integer, Person>> q = (GridCacheQuery<Map.Entry<Integer, Person>>)qry;

        return q.execute(minSalary, maxSalary).get();
    }
}
