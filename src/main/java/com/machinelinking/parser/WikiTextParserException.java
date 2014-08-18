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
