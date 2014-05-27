package com.machinelinking.template;

import com.machinelinking.template.custom.Citation;
import com.machinelinking.template.custom.CiteWeb;
import com.machinelinking.template.custom.Main;

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
        processor.addTemplateCallHandler( new Citation() );
        processor.addTemplateCallHandler( new CiteWeb() );
        processor.addTemplateCallHandler( new Main() );
        return processor;
    }

}
