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

package org.yardstickframework.gridgain.cache.model;

import org.gridgain.grid.portables.*;

/**
 * Portable entity class for benchmark.
 */
public class PortableSampleValue implements GridPortableMarshalAware {
    /** */
    private int id;

    /** */
    public PortableSampleValue() {
        // No-op.
    }

    /**
     * Constructor.
     *
     * @param id Id.
     */
    public PortableSampleValue(int id) {
        this.id = id;
    }

    /**
     * @return Id.
     */
    public int id() {
        return id;
    }

    /** {@inheritDoc} */
    @Override public void writePortable(GridPortableWriter writer) throws GridPortableException {
        writer.writeInt("id", id);
    }

    /** {@inheritDoc} */
    @Override public void readPortable(GridPortableReader reader) throws GridPortableException {
        id = reader.readInt("id");
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return "Value [id=" + id + ']';
    }
}
