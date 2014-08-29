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

package com.machinelinking.render;

import com.machinelinking.template.DefaultTemplateProcessorFactory;
import com.machinelinking.template.EvaluationContext;
import com.machinelinking.template.TemplateCall;
import com.machinelinking.template.TemplateCallBuilder;
import com.machinelinking.template.TemplateProcessor;
import com.machinelinking.template.TemplateProcessorException;
import org.codehaus.jackson.JsonNode;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TemplateNodeRender implements NodeRender {

    private final TemplateProcessor processor;

    public TemplateNodeRender(TemplateProcessor processor) {
        this.processor = processor;
    }

    public TemplateNodeRender() {
        this( DefaultTemplateProcessorFactory.getInstance().createProcessor() );
    }

    @Override
    public boolean acceptNode(JsonContext context, JsonNode node) {
        return true;
    }

    @Override
    public void render(JsonContext context, RootRender rootRender, JsonNode node, HTMLWriter writer) {
        final TemplateCall call = TemplateCallBuilder.getInstance().buildCall(node);
        final EvaluationContext evaluationContext = new EvaluationContext(rootRender, context);
        try {
            processor.process(evaluationContext, call, writer);
        } catch (TemplateProcessorException tpe) {
            throw new RuntimeException(tpe);
        }
    }
}
