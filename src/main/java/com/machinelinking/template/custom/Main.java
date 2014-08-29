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
import com.machinelinking.render.NodeRenderException;
import com.machinelinking.template.EvaluationContext;
import com.machinelinking.template.TemplateCall;
import com.machinelinking.template.TemplateCallHandler;
import com.machinelinking.template.TemplateCallHandlerException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class Main implements TemplateCallHandler {

    public static final String MAIN_TEMPLATE_NAME = "[Mm]ain";

    private static final Map<String,String> MAIN_DIV_ATTR = new HashMap<String,String>(){{
        put("class", "main");
    }};

    @Override
    public boolean process(EvaluationContext context, TemplateCall call, HTMLWriter writer)
    throws TemplateCallHandlerException {
        try {
            if(! context.evaluate(call.getName()).matches(MAIN_TEMPLATE_NAME)) return false;
        } catch (NodeRenderException nre) {
            throw new TemplateCallHandlerException("Error while evaluating context.", nre);
        }

        try {
            writer.openTag("span", MAIN_DIV_ATTR);
            writer.openTag("strong");
            writer.text("Main Article: ");
            writer.closeTag();
            for(String article : call.getParameterNames()) {
                final String domain = context.getJsonContext().getDocumentContext().getDocumentURL().getHost();
                writer.anchor(String.format("http://%s/wiki/%s", domain, article), article, true);
                writer.text(" ");
            }
            writer.closeTag();
            return true;
        } catch (IOException ioe) {
            throw new TemplateCallHandlerException("Error while invoking Main", ioe);
        }
    }
}
