package org.scy.common.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

/**
 * HTML文档解析器
 * Created by shicy on 2020-11-23
 */
public class HtmlParser implements Iterable<HtmlParser> {

    private Document htmlDocument;
    private Elements htmlElements;

    public HtmlParser(String url) throws IOException {
        htmlDocument = Jsoup.connect(url).get();
//        System.out.println(htmlDocument.outerHtml());
    }

    public HtmlParser(Elements elements) {
        htmlElements = elements;
    }

    public HtmlParser mark(String selector) {
        if (htmlDocument != null) {
            return new HtmlParser(htmlDocument.select(selector));
        }
        return new HtmlParser(htmlElements.select(selector));
    }

    public String text(String selector) {
        Element element = selectFirst(selector);
        return element != null ? element.text() : null;
    }

    public String attr(String selector, String attrName) {
        Element element = selectFirst(selector);
        return element != null ? element.attr(attrName) : null;
    }

    public String html(String selector) {
        Element element = selectFirst(selector);
        return element != null ? element.html() : null;
    }

    public Map<String, String> attrs(String selector) {
        Element element = selectFirst(selector);
        return attrs(element, null);
    }

    public Map<String, String> attrs(String selector, String[] attrNames) {
        Element element = selectFirst(selector);
        return attrs(element, attrNames);
    }

    public List<String> listText(String selector) {
        Elements elements = select(selector);
        List<String> values = new ArrayList<String>();
        if (elements != null) {
            for (Element element: elements) {
                values.add(element.text());
            }
        }
        return values;
    }

    public List<String> listAttr(String selector, String attrName) {
        Elements elements = select(selector);
        List<String> values = new ArrayList<String>();
        if (elements != null) {
            for (Element element: elements) {
                values.add(element.attr(attrName));
            }
        }
        return values;
    }

    public List<Map<String, String>> listAttrs(String selector) {
        List<Map<String, String>> results = new ArrayList<Map<String, String>>();
        Elements elements = select(selector);
        if (elements != null) {
            for (Element element: elements) {
                results.add(attrs(element, null));
            }
        }
        return results;
    }

    public List<Map<String, String>> listAttrs(String selector, String[] attrNames) {
        List<Map<String, String>> results = new ArrayList<Map<String, String>>();
        Elements elements = select(selector);
        if (elements != null) {
            for (Element element: elements) {
                results.add(attrs(element, attrNames));
            }
        }
        return results;
    }

    private Elements select(String selector) {
        if (htmlDocument != null)
            return htmlDocument.select(selector);
        return htmlElements.select(selector);
    }

    private Element selectFirst(String selector) {
        if (htmlDocument != null)
            return htmlDocument.selectFirst(selector);
        Elements elements = htmlElements.select(selector);
        return elements != null ? elements.first() : null;
    }

    private Map<String, String> attrs(Element element, String[] attrNames) {
        Map<String, String> values = new HashMap<String, String>();
        if (attrNames != null) {
            for (String attrName: attrNames) {
                values.put(attrName, element.attr(attrName));
            }
        }
        else {
            Attributes attributes = element.attributes();
            if (attributes != null) {
                for (Attribute attribute: attributes) {
                    String name = attribute.getKey();
                    String value = attribute.getValue();
                    values.put(name, value);
                }
            }
        }
        return values;
    }

    public Iterator<HtmlParser> iterator() {
        return new Iterator<HtmlParser>() {
            private int index = 0;

            public boolean hasNext() {
                return htmlElements != null && index < htmlElements.size();
            }

            public HtmlParser next() {
                Elements elements = htmlElements.eq(index++);
                return new HtmlParser(elements);
            }

            public void remove() {
                // .
            }
        };
    }
}
