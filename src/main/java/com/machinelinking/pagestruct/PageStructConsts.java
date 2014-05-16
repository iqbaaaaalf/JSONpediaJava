package com.machinelinking.pagestruct;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public interface PageStructConsts {

    static final String TYPE_FIELD = "__type";
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

    static final String NAME_FIELD = "name";
    static final String URL_FIELD = "url";
    static final String LABEL_FIELD = "label";
    static final String LEVEL_FIELD = "level";
    static final String CONTENT_FIELD = "content";

}
