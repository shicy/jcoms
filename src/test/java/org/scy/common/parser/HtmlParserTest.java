package org.scy.common.parser;

import java.util.List;
import java.util.Map;

public class HtmlParserTest {

    public static void main(String[] args) {
        try {
            String url = "https://hanyu.baidu.com/s?wd=%E6%98%AF&ptype=zici";
            HtmlParser htmlParser = new HtmlParser(url);

            // 获取属性
            String word = htmlParser.attr("body", "data-name");
            System.out.println("===> word: " + word);

            // 获取文本
            String pinyin = htmlParser.text("#pinyin b");
            System.out.println("===> pinyin: " + pinyin);

            // 获取节点属性
            Map<String, String> attributes = htmlParser.attrs("#word_bishun");
            for (String name: attributes.keySet()) {
                System.out.println("===>" + name + ": " + attributes.get(name));
            }

            // 列表
            List<String> values = htmlParser.listText("#zuci-wrapper a");
            for (String value: values) {
                System.out.println("===> words: " + value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
