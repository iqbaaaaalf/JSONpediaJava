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

package com.machinelinking.pagestruct;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface Ontology {

    static final String TYPE_FIELD = "__type";

    static final String TYPE_ENRICHED_ENTITY = "entity";
    static final String TYPE_PAGE = "page";
    static final String TYPE_SECTION = "section";
    static final String TYPE_PARAGRAPH = "paragraph";
    static final String TYPE_REFERENCE = "reference";
    static final String TYPE_LINK = "link";
    static final String TYPE_LIST = "list";
    static final String TYPE_LIST_ITEM = "list_item";
    static final String TYPE_TEMPLATE = "template";
    static final String TYPE_VAR = "var";
    static final String TYPE_TABLE = "table";
    static final String TYPE_TABLE_HEAD_CELL = "head_cell";
    static final String TYPE_TABLE_BODY_CELL = "body_cell";
    static final String TYPE_OPEN_TAG = "open_tag";
    static final String TYPE_CLOSE_TAG = "close_tag";
    static final String TYPE_INLINE_TAG = "inline_tag";
    static final String TYPE_COMMENT_TAG = "comment_tag";
    static final String TYPE_ENTITY = "entity";
    static final String TYPE_MAPPING = "mapping";

    static final String PAGE_DOM_FIELD = "wikitext-dom";
    static final String STRUCTURE_FIELD = "structure";
    static final String ISSUES_FIELD = "issues";
    static final String ABSTRACT_FIELD = "abstract";
    static final String CATEGORIES_FIELD = "categories";
    static final String SECTIONS_FIELD = "sections";
    static final String REFERENCES_FIELD = "references";
    static final String LINKS_FIELD = "links";
    static final String FREEBASE_FIELD = "freebase";
    static final String TEMPLATES_FIELD = "templates";
    static final String TEMPLATE_MAPPING_FIELD = "template-mapping";

    static final String ID_FIELD = "id";
    static final String REVID_FIELD = "revid";
    static final String NAME_FIELD = "name";
    static final String TITLE_FIELD = "title";
    static final String URL_FIELD = "url";
    static final String SIZE_FIELD = "size";
    static final String LABEL_FIELD = "label";
    static final String LEVEL_FIELD = "level";
    static final String ANCESTORS_FIELD = "ancestors";
    static final String CONTENT_FIELD = "content";
    static final String DEFAULT_FIELD = "default";

    static final String ANON_NAME_PREFIX = "__an";

}
