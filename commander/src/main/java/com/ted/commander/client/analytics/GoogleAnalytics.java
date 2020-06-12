package com.ted.commander.client.analytics;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ScriptElement;

/**
 * Created by pete on 11/13/2016.
 */
public class GoogleAnalytics {

    public static native void setGaVars() /*-{
        $wnd._gaq = $wnd._gaq || [];
        $wnd._gaq.push(['_setAccount', ' UA-87346279-1']);
    }-*/;


    public GoogleAnalytics(){
        setGaVars();
        Document doc = Document.get();
        ScriptElement script = doc.createScriptElement();
        script.setSrc("https://ssl.google-analytics.com/ga.js");
        script.setType("text/javascript");
        script.setLang("javascript");
        doc.getBody().appendChild(script);
    }


    public static native void trackPageview(String url) /*-{
        $wnd._gaq.push(['_trackPageview', url]);
    }-*/;

}
