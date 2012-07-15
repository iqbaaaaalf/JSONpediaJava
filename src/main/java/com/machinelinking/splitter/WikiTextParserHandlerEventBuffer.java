package com.machinelinking.splitter;

import com.machinelinking.pagestruct.WikiTextHRDumperHandler;
import com.machinelinking.pagestruct.WikiTextSerializerHandler;
import com.machinelinking.pagestruct.WikiTextSerializerHandlerFactory;
import com.machinelinking.parser.WikiTextParserHandler;
import com.machinelinking.serializer.Serializer;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class WikiTextParserHandlerEventBuffer {

    private final WikiTextParserHandler proxyHandler;

    private final List<Invocation> buffer = new ArrayList<>();

    public WikiTextParserHandlerEventBuffer() {
        proxyHandler = (WikiTextParserHandler) Proxy.newProxyInstance(
                this.getClass().getClassLoader(),
                new Class[]{WikiTextParserHandler.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        buffer.add( new Invocation(method, args) );
                        return null;
                    }
                }
        );
    }

    public WikiTextParserHandler getProxy() {
        return proxyHandler;
    }

    public void flush(WikiTextParserHandler out) {
        try {
            for (Invocation invocation : buffer) {
                invocation.apply(out);
            }
        } catch (Exception e) {
            // TODO: add proper log.
            final StringBuilder errorSB = new StringBuilder();
            errorSB.append("Error while flushing buffer.\n");
            errorSB.append( e.getMessage() ).append('\n');
            errorSB.append("Content:\n============{\n");
            final WikiTextHRDumperHandler dumper = new WikiTextHRDumperHandler(false);
            flush(dumper);
            errorSB.append( dumper.getContent() );
            errorSB.append("\n}============\n");
            System.err.println( errorSB.toString() );
            throw new RuntimeException("Error while flushing buffer into handler.", e);
        }
        clear();
    }

    public void flush(Serializer serializer) {
        final WikiTextSerializerHandler handler =
                WikiTextSerializerHandlerFactory.getInstance().createSerializerHandler(serializer);
        flush(handler);
        handler.flush();
    }

    public int size() {
        return buffer.size();
    }

    public void clear() {
        buffer.clear();
    }

    class Invocation {
        private final Method method;
        private final Object[] args;

        Invocation(Method method, Object[] args) {
            this.method = method;
            this.args = args;
        }

        void apply(WikiTextParserHandler out) {
            try {
                method.invoke(out, args);
            } catch (Exception e) {
                throw new RuntimeException(
                        String.format("Error while applying method %s with args [%s]", method, args), e
                );
            }
        }
    }

}
