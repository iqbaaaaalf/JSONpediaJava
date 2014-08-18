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

package com.machinelinking.template.custom;

import com.machinelinking.render.HTMLWriter;
import com.machinelinking.template.EvaluationContext;
import com.machinelinking.template.TemplateCall;
import com.machinelinking.template.TemplateCallHandler;
import com.machinelinking.template.TemplateCallHandlerException;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TemplateSuppressor implements TemplateCallHandler {

    private final String[] namePatterns;

    public TemplateSuppressor(String... namePatterns) {
        this.namePatterns = namePatterns;
    }

    @Override
    public boolean process(EvaluationContext context, TemplateCall call, HTMLWriter writer)
    throws TemplateCallHandlerException {
        final String name = context.evaluate(call.getName());
        for(String namePattern : namePatterns) {
            if(name.matches(namePattern)) return true;
        }
        return false;
    }

}
