package com.machinelinking.splitter;

/**
 * {@link Splitter} implementation for <i>table</i>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class TableSplitter extends Splitter {

    public TableSplitter() {
        super("table-splitter");
    }

    @Override
    public void beginTable() {
        super.split();
    }

}
