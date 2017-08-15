package org.scy.common.utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import java.io.*;
import java.util.*;

/**
 * XML文档处理扩展工具
 * Created by hykj on 2017/8/15.
 */
public class XmlUtilsEx {

    /** 默认编码方式 */
    public static final String defEncoding = "GBK";

    /**
     * 创建一个新XML文档（DOM4J），如果输入参数不为空则创建的XML文档初始化为参数内容。
     * @param xmlText 一个XML形式的字符串，XML文档的初始内容。可空
     * @return 一个XML文档
     * @throws DocumentException
     */
    public static Document newDocument(String xmlText) throws DocumentException {
        Document doc = null;
        if (StringUtils.isBlank(xmlText))
            doc = DocumentHelper.createDocument();
        else
            doc = DocumentHelper.parseText(xmlText);
        return doc;
    }

    /**
     * 从一个输入流中构建XML文档（DOM4J）<br>
     * @param input 输入流，如一个XML文件输入流
     * @param encoding 编码集
     * @return 一个XML文档
     * @throws DocumentException
     */
    public static Document newDocument(InputStream input, String encoding)
            throws DocumentException {
        SAXReader sReader = new SAXReader();
        sReader.setEncoding(encoding);
        return sReader.read(input);
    }

    /**
     * 使用默认编码集，构造一个输入的XML文档（DOM4J）<br>
     * 在使用{@link XmlUtilsEx#newDocument(String)}创建一个空XML文档时可能会有冲突，请使用空
     * 字段串或<code>(String)null</code>强制类型转换。
     * @see #newDocument(InputStream, String)
     */
    public static Document newDocument(InputStream input) throws DocumentException{
        return newDocument(input, defEncoding);
    }

    /**
     * 通过文件创建Document对象
     * @param file 传入一个文件对象
     * @return
     * @throws DocumentException
     * @throws IOException
     */
    public static Document newDocument(File file) throws DocumentException, IOException {
        InputStream inputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(file));
            return newDocument(inputStream);
        }
        finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    /**
     * 解析一个XML文档，并获得它的根元素
     * @param fileName XML文档的文件路径
     * @return 返回根元素，如果没有根元素则抛出异常
     * @throws DocumentException
     * @throws IOException
     */
    public static Element getFileRoot(String fileName) throws DocumentException, IOException{
        File file = new File(fileName);
        if (!file.exists())
            throw new FileNotFoundException(fileName + " 文件不存在! ");
        Document doc = newDocument(file);
        return doc.getRootElement(); // 如果没有根元素将不能顺利解析，会抛出异常
    }

    /**
     * 将XML文档写到一个输出流中。
     * @param doc 想要输出的XML文档（DOM4J）
     * @param out 输出目的流
     * @param encoding 编码集
     * @throws IOException
     */
    public static void writeDomToStream(Document doc, OutputStream out, String encoding)
            throws IOException {
        OutputFormat format = OutputFormat.createPrettyPrint(); // XML文档格式化
        format.setEncoding(encoding); // 设置编码方式，默认是UTF-8
        //format.setTrimText(false); // 保存元素值的空符号，如空格
        format.setIndentSize(4); // 缩进4个字符

        XMLWriter xmlWriter = null;
        try {
            xmlWriter = new XMLWriter(out, format);
            xmlWriter.write(doc);
            xmlWriter.flush();
        }
        finally {
            if (xmlWriter != null)
                xmlWriter.close();
        }
    }

    /**
     * @see #writeDomToStream(Document, OutputStream, String)
     */
    public static void writeDomToStream(Document doc, OutputStream out) throws IOException {
        writeDomToStream(doc, out, defEncoding);
    }

    /**
     * 将XML文档写入一个文件
     * @param doc 文档对象
     * @param file 保存到具体文件
     * @param encoding 字符编码
     * @throws IOException
     */
    public static void writeDomToFile(Document doc, File file, String encoding) throws IOException {
        OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        try {
            writeDomToStream(doc, out, encoding);
        }
        finally {
            IOUtils.closeQuietly(out);
        }
    }

    /**
     * @see #writeDomToStream(Document, OutputStream, String)
     */
    public static void writeDomToFile(Document doc, File file) throws IOException {
        writeDomToFile(doc, file, defEncoding);
    }

    /**
     * 将XML节点转换成字符串(静态方法)
     * @param node
     * @return
     */
    public static String toString(org.w3c.dom.Node node){
        return (new XmlUtilsEx()).toXmlString(node);
    }

    /**
     * 将XML节点转换成字符串(内部方法递归调用)
     * @param node
     * @return
     */
    private String toXmlString(org.w3c.dom.Node node){
        StringBuffer buf = new StringBuffer();
        if (node == null)
            return "";
        if (node.getNodeType() == org.w3c.dom.Node.TEXT_NODE) //文本节点
            buf.append(node.getNodeValue());
        else if (node.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE){ //元素节点
            buf.append("<" + node.getNodeName());
            NamedNodeMap nnm = node.getAttributes();
            for (int i = 0; i < nnm.getLength(); i++)
                buf.append(toXmlString(nnm.item(i)));
            buf.append(">");
            //以下遍历子节点
            NodeList nl = node.getChildNodes(); //获取子节点列表
            for (int i = 0; i < nl.getLength(); i++)
                buf.append(toXmlString(nl.item(i)));
            buf.append("</" + node.getNodeName() + ">");
        }
        else if (node.getNodeType() == org.w3c.dom.Node.ATTRIBUTE_NODE){ //属性节点
            buf.append(" " + node.getNodeName())
                    .append("=\"" + node.getNodeValue() + "\"");
        }
        return buf.toString();
    }

    /**
     * 获取某路径下相对简单（一对一）的文档信息，比较适合于简单的键-值对关系，没有子元素的情况。<br/>
     * 返回的是一个以属性名和属性值作为键-值对的映射表。<br/>
     * 注意：该方法默认为文档中只有一个这样的节点，如果有多个只返回第一个节点的值。
     * 如果节点不存在时，返回一个空(0个元素)的映射表。
     * 想要同时返回多个同类节点值时，请用{@link #getSimpleValueList(String)}
     * @param doc XML文档信息
     * @param xpath 节点元素路径，如：/root/param1/param2
     * @return
     */
    public static Map<String, Object> parseAsSimpleMap(Document doc, String xpath) {
        Node node = doc.selectSingleNode(xpath);
        return parseAsSimpleMap(node);
    }

    /**
     * 获取某路径下相对简单（一对一）的文档信息，
     * 详细请参见 {@link #getSimpleValues(Document, String)}
     * @param node
     * @return
     */
    public static Map<String, Object> parseAsSimpleMap(Node node) {
        Map<String, Object> params = new HashMap<String, Object>();
        if (node != null) {
            if (node instanceof Element) {
                // 获取节点属性值
                Iterator<?> attrs = ((Element)node).attributeIterator();
                while (attrs.hasNext()) {
                    Attribute attr = (Attribute)attrs.next();
                    params.put(attr.getName(), attr.getText());
                }
                // 获取子节点属性值
                Iterator<?> elems = ((Element)node).elementIterator();
                while (elems.hasNext()) {
                    Element elem = (Element)elems.next();
                    if (elem.isTextOnly()) { // 只有简单节点才返回
                        params.put(elem.getName(), elem.getText());
                    }
                }
            }
            else {
                params.put(node.getName(), node.getText());
            }
        }
        return params;
    }

    /**
     * 这里获取的是存在多个同类节点情况下的配置信息
     * @param doc
     * @param xpath 节点元素路径，如：/root/param1/param2
     * @return
     * @see #getSimpleValues(Document, String)
     */
    public static List<Map<String, Object>> parseAsSimpleMaps(Document doc, String xpath) {
        List<?> nodes = doc.selectNodes(xpath);
        List<Map<String, Object>> paramList = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < nodes.size(); i++) {
            Node node = (Node)nodes.get(i);
            Map<String, Object> params = parseAsSimpleMap(node);
            paramList.add(params);
        }
        return paramList;
    }

    /**
     * 获取一个较复杂节点的属性值，如果节点包含子节点，则子节点的属性也组装为一个Map返回
     * @param node
     * @return
     */
    public static Map<String, Object> parseAsComplexMap(Node node) {
        Map<String, Object> params = parseAsSimpleMap(node);
        if (node != null && (node instanceof Element)) {
            Iterator<?> elems = ((Element)node).elementIterator();
            while (elems.hasNext()) {
                Element elem = (Element)elems.next();
                if (elem.isTextOnly() == false) {
                    params.put(elem.getName(), parseAsComplexMap(elem));
                }
            }
        }
        return params;
    }

    /**
     * 获取某单一节点的属性值
     * @param doc xml文档
     * @param xpath 节点元素路径，如：/root/param1/param2
     * @return
     */
    public static String getSingleText(Document doc, String xpath) {
        Node node = doc.selectSingleNode(xpath);
        return getNodeText(node);
    }

    /**
     * 获取某个节点中的配置属性值
     * @param node 节点
     * @param paramName 属性名
     * @return
     */
    public static String getSingleText(Element elem, String paramName) {
        if (elem == null)
            return null;

        Element temp = (Element)elem.selectSingleNode(paramName);
        if (temp != null && temp.isTextOnly())
            return temp.getText();

        return elem.attributeValue(paramName);
    }

    /**
     * 获取某一组同类节点值数据集
     * @param doc XML文档
     * @param xpath 节点路径
     * @return
     */
    public static String[] getSingleTexts(Document doc, String xpath) {
        List<?> nodes = doc.selectNodes(xpath);
        List<String> values = new ArrayList<String>();
        for (int i = 0; i < nodes.size(); i++) {
            Node node = (Node)nodes.get(i);
            String value = getNodeText(node);
            if (value != null)
                values.add(value);
        }
        return values.toArray(new String[0]);
    }

    /**
     * 获取节点内容字符串，只有简单节点有效，否则返回null
     * @param node
     * @return
     */
    private static String getNodeText(Node node) {
        if (node == null)
            return null;

        if (node instanceof Element) {
            Element elem = (Element)node;
            return elem.isTextOnly() ? elem.getText() : null;
        }

        return node.getText();
    }

    /**
     * 设置XML文档某个节点值，根据xpath遍历节点，如果有多个节点将逐个设置，
     * 如果节点还不存在则创建节点。
     * @param doc XML文档
     * @param xpath 节点路径
     * @param value 新节点内容
     */
    public static void setNodeText(Document doc, String xpath, String value) {
        List<?> nodes = doc.selectNodes(xpath);
        if (nodes.size() == 0) {
            Element elem = DocumentHelper.makeElement(doc, xpath);
            if (xpath.indexOf("/@") < 0) // 是一个节点
                elem.setText(value);
            else {
                String attrName = StringUtils.substringAfterLast(xpath, "/@");
                elem.addAttribute(attrName, value);
            }
        }
        else {
            for (int i = 0; i < nodes.size(); i++) {
                Node node = (Node)nodes.get(i);
                node.setText(value);
            }
        }
    }

    /**
     * 设置元素的属性值或子节点列表，输入一个节点元素用于设置属性，该方法可以自动清除该元素中
     * 的其它属性及子节点，同时输入一个子元素映射表和属性映射表，可以为空，映射表的键将作为
     * 属性，映射表的值将作为属性值
     * @param elem 将被设置的元素
     * @param params 将要设置的节点映射表，如果属性名以@符号开头，则设置为节点属性
     * @param clear 是否清除原子节点和属性，如果不清除，新设置的属性将覆盖原属性，如果原属性
     * 	不存在，则创建属性或子节点
     * @return
     * @see #setNodeValue(Document, String, String)
     */
    public static Element setElement(Element elem, Map<String, Object> params, boolean clear) {
        if (elem == null)
            return null;

        if (clear) { // 清空节点
            elem = clearElement(elem);
        }

        // 设置子节点
        if (params != null && params.size() > 0) {
            setSubElements(elem, params);
        }

        return elem;
    }

    /**
     * 设置元素的子元素，如果某子元素已经存在则覆盖为新元素值，否则添加新元素。
     * 支持复杂元素设置，即如果elemMap中的值也是一个映射表，则作为一个子子元素设置。
     * @param parentElem
     * @param elemMap
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void setSubElements(Element parentElem, Map<String, Object> elemMap) {
        Iterator<String> paramNames = elemMap.keySet().iterator();
        while (paramNames.hasNext()) {
            String paramName = paramNames.next();
            Object paramValue = elemMap.get(paramName);
            if (StringUtils.startsWith(paramName, "@")) {
                parentElem.attributeValue(paramName.substring(1), "" + paramValue);
            }
            else {
                Element elem = (Element)parentElem.selectSingleNode(paramName);
                if (elem == null)
                    elem = parentElem.addElement(paramName);

                if (paramValue instanceof Map) {
                    if (elem.isTextOnly())
                        elem.setText(""); // 先清除文本
                    setSubElements(elem, (Map<String, Object>)paramValue);
                }
                else if (paramValue instanceof Collection) {
                    removeSubNodes(parentElem, paramName);
                    Iterator<?> values = ((Collection)paramValue).iterator();
                    while (values.hasNext()) {
                        parentElem.addElement(paramName).setText("" + values.next());
                    }
                }
                else {
                    elem.setText("" + paramValue);
                }
            }
        }
    }

    /**
     * 设置元素的属性值
     * @param elem
     * @param attrMap
     */
    public static void setNodeAttributes(Element elem, Map<String, String> attrMap) {
        Iterator<String> paramNames = attrMap.keySet().iterator();
        while (paramNames.hasNext()) {
            String paramName = paramNames.next();
            String paramValue = attrMap.get(paramName);
            elem.attributeValue(paramName, paramValue);
        }
    }

    /**
     * 清空某个节点元素，即删除所有子节点和属性
     * @param elem
     * @return
     */
    public static Element clearElement(Element elem) {
        elem.clearContent();
        List<?> attributes = elem.attributes();
        for (int i = attributes.size() - 1; i >= 0; i--) {
            elem.remove((Attribute)attributes.get(i));
        }
        return elem;
    }

    /**
     * 删除节点
     * @param node 将被删除的节点
     */
    public static void removeNode(Node node) {
        Element elem = node.getParent();
        if (elem != null)
            elem.remove(node);
        else {
            Document doc = node.getDocument();
            doc.remove(node);
        }
    }

    /**
     * 删除某个类型的子节点
     * @param elem 父节点
     * @param subNodeName 将被删除的节点名称
     */
    public static void removeSubNodes(Element elem, String subNodeName){
        List<?> nodes = elem.selectNodes(subNodeName);
        for (int i = 0; i < nodes.size(); i++) {
            elem.remove((Node)nodes.get(i));
        }
    }

    /**
     * 设置元素文本内容
     * @param parent
     * @param name
     * @param text
     * @return
     */
    public static Element setElement(Element parent, String name, String text) {
        Element elem = parent.element(name);
        if (elem == null)
            elem = parent.addElement(name);
        elem.setText(text);
        return elem;
    }

}
