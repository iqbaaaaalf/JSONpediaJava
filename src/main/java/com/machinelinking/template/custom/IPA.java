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

import java.io.IOException;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class IPA implements TemplateCallHandler {

    public static final String TEMPLATE_PATTERN = "IPA[c]?[-]?.{2,3}";

    @Override
    public boolean process(EvaluationContext context, TemplateCall call, HTMLWriter writer) throws TemplateCallHandlerException {
        if(!context.evaluate(call.getName()).matches(TEMPLATE_PATTERN)) return false;
        try {
            writer.openTag("i");
            writer.text("[");
            String value;
            for (TemplateCall.Parameter p : call.getParameters()) {
                value = context.evaluate(p.value);
                if("lang".equals(value) || value.endsWith(".ogg")) continue;
                writer.text(value);
            }
            writer.text("]");
            writer.closeTag();
        } catch (IOException ioe) {
            throw new TemplateCallHandlerException("Error while evaluating IPA", ioe);
        }
        return true;
    }

}
