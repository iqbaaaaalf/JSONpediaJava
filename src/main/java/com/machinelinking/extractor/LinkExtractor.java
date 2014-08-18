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
import com.machinelinking.serializer.Serializer;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Specific {@link Extractor} for <i>Wikipedia link</i>s.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class LinkExtractor extends SectionAwareExtractor {

    private List<Link> links;

    private URL url;
    private StringBuilder linkContent = new StringBuilder();
    private boolean foundParam;

    public LinkExtractor() {
        super(PageStructConsts.LINKS_FIELD);
    }

    @Override
    public void beginLink(URL url) {
        this.url = url;
        linkContent.delete(0, linkContent.length());
        foundParam = false;
    }

    @Override
    public void parameter(String param) {
        if(foundParam) linkContent.append("|");
        foundParam = true;
        if(param != null) {
            linkContent.append(param).append("=");
        }
    }

    @Override
    public void text(String content) {
        linkContent.append(content);
    }

    @Override
    public void endLink(URL url) {
        // TODO: handle links exploded with templates, ex: [{{Allmusic|class=explore|id=style/d2693|pure_url=yes}} "Hair metal"]
        if(this.url == null) return;
        if(links == null) links = new ArrayList<>();
        links.add(new Link(this.url, linkContent.toString(), super.getSectionIndex()));
    }

    @Override
    public void flushContent(Serializer serializer) {
        if(links == null) {
            serializer.value(null);
            return;
        }
        serializer.openList();
        for(Link link : links) {
            link.serialize(serializer);
        }
        serializer.closeList();
        links.clear();
    }

    @Override
    public void reset() {
        if(links != null)links.clear();
    }

}
