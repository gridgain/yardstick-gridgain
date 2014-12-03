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

package org.yardstickframework.gridgain.compute.model;

import org.gridgain.grid.portables.GridPortableException;
import org.gridgain.grid.portables.GridPortableMarshalAware;
import org.gridgain.grid.portables.GridPortableReader;
import org.gridgain.grid.portables.GridPortableWriter;

import java.io.*;
import java.util.concurrent.*;

/**
 *
 */
public class NoopCallable implements Callable<Object>, Externalizable, GridPortableMarshalAware {
    /** {@inheritDoc} */
    @Override public void writePortable(GridPortableWriter writer) throws GridPortableException {
        //No-op
    }

    /** {@inheritDoc} */
    @Override public void readPortable(GridPortableReader reader) throws GridPortableException {
        //No-op
    }

    /** {@inheritDoc} */
    @Override public Object call() {
        return null;
    }

    /** {@inheritDoc} */
    @Override public void writeExternal(ObjectOutput out) throws IOException {
        //No-op
    }

    /** {@inheritDoc} */
    @Override public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        //No-op
    }
}
