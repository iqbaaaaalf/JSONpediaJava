package com.machinelinking.render;

import com.machinelinking.pagestruct.PageStructConsts;
import org.codehaus.jackson.JsonNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link NodeRender} imeplementation for <i>Wikitext table</i>s.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TableNodeRender implements NodeRender {

    private static final Map<String,String> TABLE_ATTR = new HashMap<String,String>(){{
        put("class", "table");
    }};

    @Override
    public boolean acceptNode(JsonContext context, JsonNode node) {
        return true;
    }

    @Override
    public void render(JsonContext context, RootRender rootRender, JsonNode node, HTMLWriter writer)
    throws IOException {
        final JsonNode content = node.get(PageStructConsts.CONTENT_FIELD);
        writer.openTable("Table", TABLE_ATTR);

        writer.openTableRow();
        JsonNode cell;
        for(int i = 0; i < content.size(); i++) {
            cell = content.get(i);
            if( isHeadCell(cell) ) {
                writer.openTableCol();
                rootRender.render(context, rootRender, cell, writer);
                writer.closeTableCol();
            }
        }
        writer.closeTableRow();

        for(int i = 0; i < content.size(); i++) {
            cell = content.get(i);
            if( isBodyCell(cell) ) {
                writer.openTableRow();
                final JsonNode rowContent = cell.get(PageStructConsts.CONTENT_FIELD);
                for(int j = 0; j < rowContent.size(); j++) {
                    writer.openTableCol();
                    rootRender.render(context, rootRender, rowContent.get(j), writer);
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
        final JsonNode type = node.get(PageStructConsts.TYPE_FIELD);
        return type != null && t.equals(type.asText());
    }

}
