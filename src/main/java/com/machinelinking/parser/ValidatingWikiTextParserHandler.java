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

package com.machinelinking.parser;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Stack;

/**
 * {@link WikiTextParserHandler} able to validate the event stream.
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class ValidatingWikiTextParserHandler {

    private final String name;
    private final WikiTextParserHandler wrapped;
    private final WikiTextParserHandler proxyHandler;

    private final Stack<NodeElement> stack = new Stack<NodeElement>();

    public ValidatingWikiTextParserHandler(String name, WikiTextParserHandler w) {
        if(name == null) throw new NullPointerException();
        if(w == null) throw new NullPointerException();
        this.name = name;
        this.wrapped = w;

        proxyHandler = (WikiTextParserHandler) Proxy.newProxyInstance(
                this.getClass().getClassLoader(),
                new Class[]{WikiTextParserHandler.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        validateCallStack(method, args);
                        method.invoke(wrapped, args);
                        return null;
                    }
                }
        );
    }

    public WikiTextParserHandler getProxy() {
        return proxyHandler;
    }

    private void validateCallStack(Method method, Object[] args) {
        final WikiTextParserHandler.Push push = method.getAnnotation(WikiTextParserHandler.Push.class);
        if(push != null) {
            stack.push( new NodeElement(push.node(), getId(push.id(), args)) );
        } else {
            final WikiTextParserHandler.Pop pop = method.getAnnotation(WikiTextParserHandler.Pop.class);
            if(pop != null) {
                final NodeElement node = new NodeElement(pop.node(), getId(pop.id(), args));
                if(stack.empty()) throw new IllegalStateException(
                        getErrorName() + " Expected element in stack for node " + node
                );
                final NodeElement peek = stack.pop();
                peek.checkMatch(node);
            }
        }
        if(method.getAnnotation(WikiTextParserHandler.ValidateStack.class) != null) {
            if( ! stack.isEmpty() ) {
                throw new IllegalStateException(getErrorName() + " Stack must be empty. Found " + stack.toString() );
            }
        }
    }

    private String getId(int idIndex, Object[] args) {
        if(idIndex == -1) return null;
        final Object arg = args[idIndex];
        return arg == null ? null : arg.toString();
    }

    private String getErrorName() {
        return "Error in validator [" + name + "]";
    }

    class NodeElement {
        final String node;
        final String id;

        NodeElement(String node, String id) {
            if(node == null) throw new IllegalArgumentException();
            this.node = node;
            this.id   = id;
        }

        private void checkMatch(NodeElement other) {
            if(! node.equals(other.node)) throw new IllegalArgumentException(
                    String.format(getErrorName() + " Invalid node, expected: [%s] found [%s]", node, other.node)
            );
            if(id != null && ! id.equals(other.id)) throw new IllegalArgumentException(
                    String.format(getErrorName() + " Invalid node id, expected: [%s] found [%s]", id, other.id)
            );
        }

        @Override
        public String toString() {
            return String.format("{node: [%s], id: [%s]}", node, id);
        }
    }
}
