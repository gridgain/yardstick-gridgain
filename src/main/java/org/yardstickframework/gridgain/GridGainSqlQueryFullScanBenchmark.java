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
 * GridGain benchmark that performs query operations.
 */
public class GridGainSqlQueryFullScanBenchmark extends GridGainAbstractBenchmark {
    /** */
    private GridCacheQuery qry;

    /**
     * Use cache "query" for this benchmark. Configuration for the cache can be found
     * in 'config/gridgain-config.xml' file.
     */
    public GridGainSqlQueryFullScanBenchmark() {
        super("query");
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

        qry = cache.queries().createSqlQuery(Person.class, "firstName like '%' || ? and  lastName like '%' || ?");
    }

    /** {@inheritDoc} */
    @Override public boolean test(Map<Object, Object> ctx) throws Exception {
        String lastChar = Integer.toString(ThreadLocalRandom.current().nextInt(args.range()));

        Collection<Map.Entry<Integer, Person>> entries = executeQuery(lastChar);

        for (Map.Entry<Integer, Person> entry : entries) {
            Person p = entry.getValue();

            if (!p.getLastName().endsWith(lastChar) || !p.getFirstName().endsWith(lastChar))
                throw new Exception("Invalid person retrieved [lastChar=" + lastChar + ", person=" + p + ']');
        }

        return true;
    }

    /**
     * @param lastChar Last char into first name and last name.
     * @return Query result.
     * @throws Exception If failed.
     */
    private Collection<Map.Entry<Integer, Person>> executeQuery(String lastChar) throws Exception {
        GridCacheQuery<Map.Entry<Integer, Person>> q = (GridCacheQuery<Map.Entry<Integer, Person>>)qry;

        return q.execute(lastChar, lastChar).get();
    }
}
