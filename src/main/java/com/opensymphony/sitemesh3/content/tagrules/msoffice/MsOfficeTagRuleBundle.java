package com.opensymphony.sitemesh3.content.tagrules.msoffice;

import com.opensymphony.sitemesh3.tagprocessor.State;
import com.opensymphony.sitemesh3.tagprocessor.StateTransitionRule;
import com.opensymphony.sitemesh3.content.ContentProperty;
import com.opensymphony.sitemesh3.content.tagrules.TagRuleBundle;
import com.opensymphony.sitemesh3.content.tagrules.html.ExportTagToContentRule;
import com.opensymphony.sitemesh3.SiteMeshContext;

/**
 * {@link com.opensymphony.sitemesh3.content.tagrules.TagRuleBundle} that adds document properties from MS Office Word and Excel
 * documents that have been saved as HTML.
 *
 * <p>These are:</p>
 * <ul>
 * <li><b><code>office.DocumentProperties.XXX</code></b>: The document properties, where <code>XXX</code> is
 * <code>Author</code>, <code>Company</code>, <code>Version</code>, etc.</li>
 * </ul>
 *
 * @author Joe Walnes
 */
public class MsOfficeTagRuleBundle implements TagRuleBundle {

    @Override
    public void install(State defaultState, ContentProperty contentProperty, SiteMeshContext siteMeshContext) {
        // When inside <xml><o:documentproperties>...</o:documentproperties></xml>,
        // capture every tag that has an o: prefix.
        State xmlState = new State();
        defaultState.addRule("xml", new StateTransitionRule(xmlState));

        State documentPropertiesState = new State();
        ContentProperty docProperties = contentProperty.getChild("office").getChild("DocumentProperties");
        for (String documentPropertyName : getOfficePropertyNames()) {
            documentPropertiesState.addRule("o:" + documentPropertyName,
                    new ExportTagToContentRule(docProperties.getChild(documentPropertyName)));
        }
        xmlState.addRule("o:documentproperties", new StateTransitionRule(documentPropertiesState));

    }

    protected String[] getOfficePropertyNames() {
        return new String[]{
            "Author", "Characters", "CharactersWithSpaces", "Company", "Created", "LastAuthor", "LastSaved",
                "Lines", "Pages", "Paragraphs", "Revision", "TotalTime", "Version", "Words"
        };
    }

}