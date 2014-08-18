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
