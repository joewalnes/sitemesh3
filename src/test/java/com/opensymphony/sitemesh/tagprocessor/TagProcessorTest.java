package com.opensymphony.sitemesh.tagprocessor;

import com.opensymphony.sitemesh.html.rules.TagReplaceRule;
import com.opensymphony.sitemesh.tagprocessor.util.CharArray;
import junit.framework.TestCase;

import java.io.*;

/**
 * @author Joe Walnes
 */
public class TagProcessorTest extends TestCase {

    public void testSupportsConventionalReaderAndWriter() throws IOException {
        Reader in = new StringReader("<hello><b id=\"something\">world</b></hello>");
        Writer out = new StringWriter();

        TagProcessor processor = new TagProcessor(in, out);
        processor.addRule(new TagReplaceRule("b", "strong"));

        processor.process();
        assertEquals("<hello><strong id=\"something\">world</strong></hello>", out.toString());
    }

    public void testAllowsRulesToModifyAttributes() throws IOException {
        Reader in = new StringReader("<hello><a href=\"modify-me\">world</a></hello>");
        Writer out = new StringWriter();

        TagProcessor processor = new TagProcessor(in, out);
        processor.addRule(new BasicRule("a") {
            @Override
            public void process(Tag tag) {
                CustomTag customTag = new CustomTag(tag);
                String href = customTag.getAttributeValue("href", false);
                if (href != null) {
                    href = href.toUpperCase();
                    customTag.setAttributeValue("href", true, href);
                }
                customTag.writeTo(currentBuffer());
            }
        });

        processor.process();
        assertEquals("<hello><a href=\"MODIFY-ME\">world</a></hello>", out.toString());
    }

    public void testSupportsChainedFilteringOfTextContent() throws IOException {
        Reader in = new StringReader("<hello>world</hello>");
        Writer out = new StringWriter();

        TagProcessor processor = new TagProcessor(in, out);
        processor.addTextFilter(new TextFilter() {
            @Override
            public String filter(String text) {
                return text.toUpperCase();
            }
        });
        processor.addTextFilter(new TextFilter() {
            @Override
            public String filter(String text) {
                return text.replaceAll("O", "o");
            }
        });

        processor.process();
        assertEquals("<HELLo>WoRLD</HELLo>", out.toString());
    }

    public void testSupportsTextFiltersForSpecificStates() throws IOException {
        Reader in = new StringReader("la la<br> la la <capitalism>laaaa<br> laaaa</capitalism> la la");
        Writer out = new StringWriter();

        TagProcessor processor = new TagProcessor(in, out);

        State capsState = new State();
        processor.addRule(new StateTransitionRule("capitalism", capsState, true));

        capsState.addTextFilter(new TextFilter() {
            @Override
            public String filter(String text) {
                return text.toUpperCase();
            }
        });

        processor.process();
        assertEquals("la la<br> la la <capitalism>LAAAA<BR> LAAAA</capitalism> la la", out.toString());
    }

    public void testCanAddAttributesToCustomTag() throws IOException {
        CharArray buffer = new CharArray(64);
        String html = "<h1>Headline</h1>";
        TagProcessor tagProcessor = new TagProcessor(html.toCharArray(), buffer);
        tagProcessor.addRule(new BasicRule() {
            @Override
            public boolean shouldProcess(String tag) {
                return tag.equalsIgnoreCase("h1");
            }

            @Override
            public void process(Tag tag) {
                if (tag.getType() == Tag.Type.OPEN) {
                    CustomTag ctag = new CustomTag(tag);
                    ctag.addAttribute("class", "y");
                    assertEquals(1, ctag.getAttributeCount());
                    tag = ctag;
                }
                tag.writeTo(currentBuffer());
            }
        });
        tagProcessor.process();
        assertEquals("<h1 class=\"y\">Headline</h1>", buffer.toString());
    }
}