package com.machinelinking.render;

import org.codehaus.jackson.JsonNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TableNodeRender implements NodeRender {

    private static final Map<String,String> TABLE_ATTR = new HashMap<String,String>(){{
        put("style", "background-color: #DC5858");
    }};

    @Override
    public boolean acceptNode(JsonNode node) {
        return true;
    }

    @Override
    public void render(RootRender rootRender, JsonNode node, HTMLWriter writer) throws IOException {
        final JsonNode content = node.get(TemplateConstants.TEMPLATE_CONTENT);
        writer.openTable("Table", TABLE_ATTR);

        writer.openTableRow();
        JsonNode cell;
        for(int i = 0; i < content.size(); i++) {
            cell = content.get(i);
            if( isHeadCell(cell) ) {
                writer.openTableCol();
                rootRender.render(rootRender, cell, writer);
                writer.closeTableCol();
            }
        }
        writer.closeTableRow();

        for(int i = 0; i < content.size(); i++) {
            cell = content.get(i);
            if( isBodyCell(cell) ) {
                writer.openTableRow();
                final JsonNode rowContent = cell.get(TemplateConstants.TEMPLATE_CONTENT);
                for(int j = 0; j < rowContent.size(); j++) {
                    writer.openTableCol();
                    rootRender.render(rootRender, rowContent.get(j), writer);
                    writer.closeTableCol();
                }
                writer.closeTableRow();
            }
        }


        writer.closeTable();
    }

    private boolean isHeadCell(JsonNode node) {
        return checkCellType(node, "head_cell");
    }

    private boolean isBodyCell(JsonNode node) {
        return checkCellType(node, "body_cell");
    }

    private boolean checkCellType(JsonNode node, String t) {
        final JsonNode type = node.get(TemplateConstants.TYPE_ATTR);
        return type != null && t.equals(type.asText());
    }

}
