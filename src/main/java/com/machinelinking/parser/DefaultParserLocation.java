package com.machinelinking.parser;

/**
 * Default implementation of {@link ParserLocation}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultParserLocation implements ParserLocation {

    private int row;
    private int col;

    public DefaultParserLocation(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public DefaultParserLocation(ParserLocation l) {
        this(l.getRow(), l.getCol());
    }

    @Override
    public int getRow() {
        return row;
    }

    @Override
    public int getCol() {
        return col;
    }

}
