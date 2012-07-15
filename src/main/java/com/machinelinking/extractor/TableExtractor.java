package com.machinelinking.extractor;

import com.machinelinking.serializer.Serializer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
//TODO: the current output doens't produce nested table elements.
//TODO: this must become a transformer
public class TableExtractor extends Extractor {

    private Table table;

    private boolean beginTable;
    private boolean nextHead;
    private boolean nextBody;

    private List<String> rowData;
    private int currRow = 1;

    public TableExtractor() {
        super("tables");
    }

    @Override
    public void beginTable() {
        beginTable = true;
    }

    @Override
    public void text(String content) {
        if(beginTable) {
            beginTable = false;
            if(table != null) throw new IllegalStateException();
            table = new Table(content);
        } else if(nextHead) {
            nextHead = false;
            table.addHeader(content);
        } else if(nextBody) {
            nextBody = false;
            if(rowData == null) rowData = new ArrayList<String>();
            rowData.add(content);
        }
    }

    @Override
    public void headCell(int row, int col) {
        nextHead = true;
    }

    @Override
    public void bodyCell(int row, int col) {
        nextBody = true;
        if(currRow != row) {
            currRow = row;
            table.addRow(rowData);
            rowData = null;
        }
    }

    @Override
    public void endTable() {
        reset();
    }

    @Override
    public void flushContent(Serializer serializer) {
        // TODO: what to do with this.
        Table[] result = new Table[] {table};
        table = null;
    }

    @Override
    public void reset() {
        table = null;
        beginTable = false;
        nextHead = false;
        nextBody = false;
        currRow  = 1;
    }
}
