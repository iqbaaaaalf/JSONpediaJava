/*
 * Copyright 2012-2015 Michele Mostarda (me@michelemostarda.it)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.machinelinking.filter;

/**
 * Defines a filter applicable over a <i>JSON</i> object.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface JSONFilter {

    /**
     * @return <code>true</code> if filter is empty, <code>false</code> otherwise.
     */
    boolean isEmpty();

    /**
     * Sets the nested filter if any.
     *
     * @param nested
     */
    void setNested(JSONFilter nested);

    /**
     *
     * @return the nested filter if any or <code>null</code>.
     */
    JSONFilter getNested();

    /**
     * @return human readable version of filter.
     */
    String humanReadable();

}
