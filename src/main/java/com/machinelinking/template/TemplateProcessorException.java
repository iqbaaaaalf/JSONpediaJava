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
