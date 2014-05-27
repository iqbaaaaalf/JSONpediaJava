package com.machinelinking.template;

import com.machinelinking.pagestruct.PageStructConsts;
import com.machinelinking.util.JSONUtils;
import org.codehaus.jackson.JsonNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TemplateCallBuilder {

    private static  final TemplateCallBuilder builder = new TemplateCallBuilder();

    public static TemplateCallBuilder getInstance() {
        return builder;
    }

    private TemplateCallBuilder() {}

    public TemplateCall buildCall(JsonNode node) {
        final JsonNode name = node.get(PageStructConsts.NAME_FIELD);
        final JsonNode content = node.get(PageStructConsts.CONTENT_FIELD);
        final List<TemplateCall.Parameter> parameters = new ArrayList<>();
        String paramName;
        for(Map.Entry<String,JsonNode> entry : JSONUtils.toIterable(content.getFields())) {
            paramName = entry.getKey().startsWith(PageStructConsts.ANON_NAME_PREFIX) ? null : entry.getKey();
            parameters.add(new TemplateCall.Parameter(paramName, simplifySingleElemArray(entry.getValue())));
        }
        return new DefaultTemplateCall(
                name,
                parameters.toArray( new TemplateCall.Parameter[parameters.size()])
        );
    }

    //TODO: remove when introduced serialization with single array elem avoidance.
    private JsonNode simplifySingleElemArray(JsonNode e) {
        if(e.isArray() && e.size() == 1) {
            return e.get(0);
        } else {
            return e;
        }
    }

}
