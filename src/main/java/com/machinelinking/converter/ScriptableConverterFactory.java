package com.machinelinking.converter;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

/**
 * Factory for {@link com.machinelinking.converter.ScriptableConverter}.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ScriptableConverterFactory {

    private static ScriptableConverterFactory instance;

    private final ScriptEngineManager engineManager;
    private final Bindings bindings;

    public static final ScriptableConverterFactory getInstance() {
        if(instance == null) {
            instance = new ScriptableConverterFactory();
        }
        return instance;
    }

    private ScriptableConverterFactory() {
        engineManager = new ScriptEngineManager();
        bindings = new SimpleBindings();
        final ScriptContextFunctions functions = new ScriptContextFunctions();
        bindings.put("functions", functions);
    }

    ScriptableConverter createConverter(String script) throws ScriptableFactoryException {
        final ScriptEngine engine = engineManager.getEngineByName("python");
        engine.setBindings(bindings, ScriptContext.GLOBAL_SCOPE);
        try {
            engine.eval(script);
            return new ScriptableConverter((Invocable)engine);
        } catch (ScriptException se) {
            throw new ScriptableFactoryException("Error while instantiating converter.", se);
        }
    }

}
