package com.machinelinking.parser;

/**
 * Any exception raised by {@link WikiTextParser}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class WikiTextParserException extends Exception {

    private int row, col;

    public WikiTextParserException(int row, int col, String msg) {
        super(msg);
        this.row = row;
        this.col = col;
    }

    public WikiTextParserException(int row, int col, Throwable e) {
        super(e);
        this.row = row;
        this.col = col;
    }

    public WikiTextParserException(int row, int col, String msg, Throwable e) {
        super(msg, e);
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    @Override
    public String toString() {
        return String.format("%s (row,col: %d,%d)", super.toString(), getRow(), getCol());
    }

}
