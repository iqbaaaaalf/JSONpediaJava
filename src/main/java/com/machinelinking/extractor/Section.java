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

package com.machinelinking.extractor;

import com.machinelinking.pagestruct.PageStructConsts;
import com.machinelinking.serializer.Serializable;
import com.machinelinking.serializer.Serializer;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines a <i>Wikipedia section</i>.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class Section implements Serializable {

    private String title;
    private int level;
    private List<Integer> ancestors;

    public Section(String title, List<Integer> ancestors, int level) {
        this.title = title;
        this.level = level;
        this.ancestors = new ArrayList<>(ancestors);
    }

    public String getTitle() {
        return title;
    }

    public int getLevel() {
        return level;
    }

    @Override
    public void serialize(Serializer serializer) {
        serializer.openObject();
        serializer.fieldValue(PageStructConsts.TITLE_FIELD, title);
        serializer.fieldValue(PageStructConsts.LEVEL_FIELD, level);
        serializer.field(PageStructConsts.ANCESTORS_FIELD);
        serializer.openList();
        for(int i: ancestors){
            serializer.value(i);
        }
        serializer.closeList();
        serializer.closeObject();
    }

}
