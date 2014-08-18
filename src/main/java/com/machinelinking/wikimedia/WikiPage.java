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

package com.machinelinking.wikimedia;

/**
 * Models a single <i>Wikipage</i>
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class WikiPage {

    private final int id;
    private final int revId;
    private final String title;
    private final String content;

    public WikiPage(int id, int revid, String title, String content) {
        this.id      = id;
        this.revId = revid;
        this.title   = title;
        this.content = content;
    }

    public int getId() {
         return id;
    }

    public int getRevId() {
         return revId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public int getSize() {
        return title.length() + content.length() + 2;
    }

    @Override
    public String toString() {
        return String.format("id: %d title: %s\n\n content:%s\n", id, title, content);
    }

}
