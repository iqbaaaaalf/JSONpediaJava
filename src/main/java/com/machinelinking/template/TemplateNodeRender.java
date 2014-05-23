package com.machinelinking.template;

import com.machinelinking.render.HTMLWriter;
import com.machinelinking.render.JsonContext;
import com.machinelinking.render.NodeRender;
import com.machinelinking.render.RootRender;
import org.codehaus.jackson.JsonNode;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TemplateNodeRender implements NodeRender {

    private final TemplateProcessor processor = new DefaultTemplateProcessor();

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
