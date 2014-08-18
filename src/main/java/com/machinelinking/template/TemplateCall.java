/*
 * JSONpedia - Convert any MediaWiki document to JSON.
 *
 * Written in 2014 by Michele Mostarda <mostarda@fbk.eu>.
 *
 * To the extent possible under law, the author has dedicated all copyright and related and
 * neighboring rights to this software to the public domain worldwide.
 * This software is distributed without any warranty.
 *
 * You should have received a copy of the CC BY Creative Commons Attribution 4.0 Internationa Public License.
 * If not, see <https://creativecommons.org/licenses/by/4.0/legalcode>.
 */

package com.machinelinking.template;

import org.codehaus.jackson.JsonNode;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface TemplateCall {

    JsonNode getName();

    Parameter[] getParameters();

    String[] getParameterNames();

    JsonNode getParameter(String param);

    JsonNode getParameter(int index);

    int getParametersCount();

    public class Parameter {
        public final String name;
        public final JsonNode value;
        public Parameter(String name, JsonNode value) {
            this.name = name;
            this.value = value;
        }
        @Override
        public String toString() {
            return String.format("%s:%s", name, value);
        }
    }

}
