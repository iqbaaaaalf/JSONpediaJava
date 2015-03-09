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

package com.machinelinking.template;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TemplateProcessorException extends Exception {

    private final TemplateCall call;

    public TemplateProcessorException(String message, TemplateCall call) {
        super(message);
        this.call = call;
    }

    public TemplateProcessorException(String message, Exception e, TemplateCall call) {
        super(message, e);
        this.call = call;
    }

    public TemplateCall getCall() {
        return call;
    }

    @Override
    public String toString() {
        return String.format("%s\n%s", super.toString(), call);
    }

}
