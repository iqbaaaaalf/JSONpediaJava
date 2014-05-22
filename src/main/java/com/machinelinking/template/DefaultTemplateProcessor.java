package com.machinelinking.template;

import com.machinelinking.render.HTMLWriter;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Wikimedia template processor based on documentation available at
 * <a href="http://en.wikipedia.org/wiki/Help:Template">Help:Template</a>
 * <a href="http://en.wikipedia.org/wiki/Help:Magic_words">Help:Magic_words</a>
 * Test with <a href="http://en.wikipedia.org/w/index.php?title=Wikipedia:Sandbox&action=submit">Wikipedia:Sandbox</a>
 *
 * @author Michele Mostarda (mostarda@fbk.eu)
 */
public class DefaultTemplateProcessor implements TemplateProcessor {

    public static  final Set<String> VARIABLES = Collections.unmodifiableSortedSet(new TreeSet<>(Arrays.asList(
            "ARTICLEPAGENAME",
            "ARTICLESPACE",
            "BASEPAGENAME",
            "FULLPAGENAME",
            "NAMESPACE",
            "PAGENAME",
            "SUBJECTPAGENAME",
            "SUBJECTSPACE",
            "SUBPAGENAME",
            "TALKPAGENAME",
            "TALKSPACE",
            "FULLPAGENAMEE ",
            "NAMESPACEE",
            "SITENAME",
            "SERVER",
            "SERVERNAME",
            "SCRIPTPATH",
            "CURRENTVERSION",
            "REVISIONID",
            "REVISIONDAY",
            "REVISIONDAY2",
            "REVISIONMONTH",
            "REVISIONYEAR",
            "REVISIONTIMESTAMP",
            "REVISIONUSER",
            "CURRENTYEAR",
            "CURRENTMONTH",
            "CURRENTMONTHNAME",
            "CURRENTMONTHABBREV",
            "CURRENTDAY",
            "CURRENTDAY2",
            "CURRENTDOW",
            "CURRENTDAYNAME",
            "CURRENTTIME",
            "CURRENTHOUR",
            "CURRENTWEEK",
            "CURRENTTIMESTAMP",
            "LOCALYEAR",
            "NUMBEROFPAGES",
            "NUMBEROFARTICLES",
            "NUMBEROFFILES",
            "NUMBEROFEDITS",
            "NUMBEROFVIEWS",
            "NUMBEROFUSERS",
            "NUMBEROFADMINS",
            "NUMBEROFACTIVEUSERS")));

    public static final Set<String> METADATA = Collections.unmodifiableSortedSet(new TreeSet<>(Arrays.asList(
            "PAGEID",
            "PAGESIZE:",
            "PROTECTIONLEVEL:",
            "PENDINGCHANGELEVEL",
            "PAGESINCATEGORY:",
            "NUMBERINGROUP:"
    )));

    public static final List<String> NAMESPACES = Collections.unmodifiableList(Arrays.asList(
            "Talk",
            "User",
            "User talk",
            "Wikipedia",
            "Wikipedia talk",
            "File",
            "File talk",
            "MediaWiki",
            "MediaWiki talk",
            "Template",
            "Template talk",
            "Help",
            "Help talk",
            "Category",
            "Category talk"
    ));

    private final List<TemplateCallHandler> handlers = new ArrayList<>();

    public DefaultTemplateProcessor() {}

    public void process(EvaluationContext context, TemplateCall call, HTMLWriter writer)
    throws TemplateProcessorException {
        try {
            final String name = call.getName().evaluate(context);
            final int splitIndex = name.indexOf(':');
            final String[] nameParts = splitIndex == -1 ?
                    new String[]{name}
                    :
                    new String[] {name.substring(0, splitIndex), name.substring(splitIndex + 1)};
            if (processVariable(name, writer)) {
            } else if (processMetadata(name, writer)) {
            } else if (processFormatting(name, nameParts, context, call, writer)) {
            } else if (processPath(name, nameParts, context, call, writer)) {
            } else if (processConditionalExp(name, nameParts, context, call, writer)) {
            } else if (processHandlers(context, call, writer)) {
            } else {
                throw new TemplateProcessorException(
                        "Cannot find handler for template call.",
                        new Fragment.TemplateFragment(call)
                );
            }
        } catch (Exception e) {
            throw new TemplateProcessorException(
                    "Error while processing template call.", e, new Fragment.TemplateFragment(call)
            );
        }
    }

    @Override
    public void process(EvaluationContext context, Fragment value, HTMLWriter writer)
    throws TemplateProcessorException {
        try {
            writer.text(value.evaluate(context));
        } catch (IOException ioe) {
            throw new TemplateProcessorException("Error while evaluating fragment.", ioe, value);
        }
    }

    @Override
    public void addTemplateCallHandler(TemplateCallHandler handler) {
        handlers.add(handler);
    }

    @Override
    public void removeTemplateCallHandler(TemplateCallHandler handler) {
        handlers.remove(handler);
    }

    private boolean processVariable(String candidate, HTMLWriter writer) throws IOException {
        if(VARIABLES.contains(candidate)) {
            writer.text(String.format("(variable %s)", candidate));
            return true;
        }
        return false;
    }

    private boolean processMetadata(String candidate, HTMLWriter writer) throws IOException {
        for(String metadata : METADATA) {
            if(candidate.startsWith(metadata)) {
                writer.text(String.format("(metadata %s)", candidate));
                return true;
            }
        }
        return false;
    }

    private boolean processFormatting(
            String name, String[] nameParts, EvaluationContext context, TemplateCall call, HTMLWriter writer
    ) throws IOException, TemplateProcessorException {
        if (nameParts.length != 2) return false;
        if (name.startsWith("lc:")) {
            writer.text(nameParts[1].toLowerCase());
            return true;
        }
        if (name.startsWith("lcfirst:")) {
            writer.text(nameParts[1].substring(0, 1).toLowerCase());
            writer.text(nameParts[1].substring(1));
            return true;
        }
        if (name.startsWith("uc:")) {
            writer.text(nameParts[1].toUpperCase());
            return true;
        }
        if (name.startsWith("ucfirst:")) {
            writer.text(nameParts[1].substring(0, 1).toUpperCase());
            writer.text(nameParts[1].substring(1));
            return true;
        }
        if (name.startsWith("formatnum:")) {
            writer.text(nameParts[1]);
            return true;
        }
        if (name.startsWith("#formatdate:")) {
            writer.text(nameParts[1]);
            return true;
        }
        if (name.startsWith("padleft:")) {
            final int pad = Integer.parseInt(call.getProcessedParameter(0, context)) - nameParts[1].length();
            writer.text(nameParts[1]);
            for(int i = 0; i < pad; i++) writer.text("0");
            return true;
        }
        if (name.startsWith("padright:")) {
            final int pad = Integer.parseInt(call.getProcessedParameter(0, context)) - nameParts[1].length();
            for(int i = 0; i < pad; i++) writer.text("0");
            writer.text(nameParts[1]);
            return true;
        }
        if (name.startsWith("plural:")) {
            final int count = Integer.parseInt(nameParts[1]);
            writer.text( call.getParameter(count == 1 ? 0 : 1).evaluate(context) );
            return true;
        }
        if (name.startsWith("#time:")) {
            writer.text( call.getParameter(0).evaluate(context) );
            return true;
        }
        if (name.startsWith("gender:")) {
            writer.text(String.format(
                    "%s/%s/%s",
                    call.getParameter(0).evaluate(context),
                    call.getParameter(1).evaluate(context),
                    call.getParameter(2).evaluate(context)
            ));
            return true;
        }
        if (name.startsWith("#tag:")) {
            writer.openTag(nameParts[1], call.getParameters(1, context, false));
            writer.text(call.getParameter(0).evaluate(context));
            writer.closeTag();
            return true;
        }
        return false;
    }

    private boolean processPath(
            String name, String[] nameParts, EvaluationContext context, TemplateCall call, HTMLWriter writer
    ) throws IOException, TemplateProcessorException {
        if (nameParts.length != 2) return false;
        if (name.startsWith("localurl:")) {
            final String query = call.getProcessedParameter(0, context);
            writer.text(String.format("/w/index.php?title=%s&%s", nameParts[1], query == null ? "" : query));
            return true;
        }
        if (name.startsWith("fullurl:")) {
            final String query = call.getProcessedParameter(0, context);
            writer.text(getURL(null, null, nameParts[1], query));
            return true;
        }
        if (name.startsWith("canonicalurl:")) {
            final String query = call.getProcessedParameter(0, context);
            writer.text(getURL("http:", context.getDomain(), nameParts[1], query));
            return true;
        }
        if (name.startsWith("filepath:")) {
            writer.text(nameParts[1]);
            return true;
        }
        if (name.startsWith("urlencode:")) {
            final String format = call.getProcessedParameter(0, context);
            switch (format) {
                case "WIKI":
                    writer.text(URLEncoder.encode(nameParts[1], "UTF-8"));
                    break;
                case "PATH":
                    writer.text(nameParts[1].replaceAll(" ", "_"));
                    break;
                default:
                    throw new IllegalArgumentException("Invalid format: " + format);
            }
            return true;
        }
        if (name.startsWith("anchorencode:")) {
            writer.text(URLEncoder.encode(nameParts[1], "UTF-8"));
            return true;
        }
        if (name.startsWith("ns:")) {
            final int index = Integer.parseInt(nameParts[1]) - 1;
            writer.text(NAMESPACES.get(index));
            return true;
        }
        if (name.startsWith("#reltoabs:")) {
            writer.text(nameParts[1]);
            return true;
        }
        if (name.startsWith("#titleparts:")) {
            writer.text(nameParts[1]);
            return true;
        }
        return false;
    }

    // TODO: some magic words have been not implemented.
    private boolean processConditionalExp(
            String name, String[] nameParts, EvaluationContext context, TemplateCall call, HTMLWriter writer
    ) throws IOException, TemplateProcessorException {
        if (name.startsWith("#expr:")) {
            writer.text(nameParts[1]);
            return true;
        }
        if (name.startsWith("#if:")) {
            final String testString = nameParts.length == 1 ? "" : nameParts[1];
            writer.text(call.getProcessedParameter(testString.length() > 0 ? 0 : 1, context));
            return true;
        }
        if (name.startsWith("#ifeq:")) {
            final String testString1 = nameParts[1];
            final String testString2 = call.getProcessedParameter(0, context);
            writer.text(call.getProcessedParameter(testString1.equals(testString2) ? 1 : 2, context));
            return true;
        }
        if (name.startsWith("#iferror:")) {
            return true;
        }
        if (name.startsWith("#ifexpr:")) {
            return true;
        }
        if (name.startsWith("#ifexist:")) {
            return true;
        }
        if (name.startsWith("#switch:")) {
            final String testString = nameParts[1];
            final DefaultTemplateCall.Parameter matchedCase = call.getParameter(testString);
            if(matchedCase == null) {
                writer.text(call.getProcessedParameter(call.getParametersCount() - 1, context));
            } else {
                writer.text(matchedCase.value.evaluate(context));
            }
            return true;
        }
        return false;
    }

    private boolean processHandlers(EvaluationContext context, TemplateCall call, HTMLWriter writer) {
        for(TemplateCallHandler handler : handlers) {
            if(handler.process(context, call, writer))
                return true;
        }
        return false;
    }

    private String getURL(String protocol, String host, String title, String query) {
        return String.format(
                "%s//%s/w/index.php?title=%s&%s",
                protocol == null ? "" : protocol,
                host == null ? "" : host,
                title == null ? "" : title,
                query == null ? "" : query
        );
    }

}
