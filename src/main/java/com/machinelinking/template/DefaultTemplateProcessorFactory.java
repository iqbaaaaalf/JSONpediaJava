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

package com.machinelinking.template;

import com.machinelinking.template.custom.CiteBook;
import com.machinelinking.template.custom.CiteJournal;
import com.machinelinking.template.custom.CiteWeb;
import com.machinelinking.template.custom.IPA;
import com.machinelinking.template.custom.Main;
import com.machinelinking.template.custom.NoWrap;
import com.machinelinking.template.custom.TemplateSuppressor;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultTemplateProcessorFactory implements TemplateProcessorFactory {

    private static final DefaultTemplateProcessorFactory instance = new DefaultTemplateProcessorFactory();

    public static final DefaultTemplateProcessorFactory getInstance() {
        return instance;
    }

    @Override
    public TemplateProcessor createProcessor() {
        final DefaultTemplateProcessor processor = new DefaultTemplateProcessor();
        processor.addTemplateCallHandler(null, new Main() );
        processor.addTemplateCallHandler(null, new IPA() );
        processor.addTemplateCallHandler(null, new NoWrap() );

        processor.addTemplateCallHandler(RenderScope.TEXT_RENDERING, new TemplateSuppressor(".*")); // Suppress all.

        processor.addTemplateCallHandler(RenderScope.FULL_RENDERING, new CiteWeb());
        processor.addTemplateCallHandler(RenderScope.FULL_RENDERING, new CiteBook());
        processor.addTemplateCallHandler(RenderScope.FULL_RENDERING, new CiteJournal());
        return processor;
    }

}
