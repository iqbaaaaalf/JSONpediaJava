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

package com.machinelinking.service;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Used to define an unresolvable entity in <i>Wikipedia</i>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class UnresolvableEntityException extends BaseServiceException {

    public UnresolvableEntityException(Exception e) {
        super(
                Response
                        .status(Response.Status.NOT_FOUND)
                        .entity(new ExceptionWrapper(e))
                        .type(MediaType.APPLICATION_JSON)
                        .build()
        );
    }

}

