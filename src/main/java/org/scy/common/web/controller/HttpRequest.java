package org.scy.common.web.controller;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 发起Http请求的参数信息
 * Created by shicy 2020/11/1
 */
public class HttpRequest {

    private final static Logger logger = LoggerFactory.getLogger(HttpRequest.class);

    private Method method;
    private String url;
    private Map<String, String> params;
    private String body;
    private File file;
    private MultipartFile multipartFile;
    private InputStream inputStream;
    private String uploadName = "file";
    private String fileName = "";

    private InputStream fileInputStream;

    public enum Method {
        GET("GET"),
        POST("POST"),
        JSON("JSON"),
        UPLOAD("UPLOAD"),
        DOWNLOAD("DOWNLOAD");

        private final String value;
        Method(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public HttpRequest(String url) {
        this.url = url;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getUrl() {
        return StringUtils.trimToEmpty(url);
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setFile(File file) {
        this.file = file;
        if (file != null) {
            this.multipartFile = null;
            this.inputStream = null;
        }
    }

    public void setMultipartFile(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
        if (multipartFile != null) {
            this.file = null;
            this.inputStream = null;
        }
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
        if (inputStream != null) {
            this.file = null;
            this.multipartFile = null;
        }
    }

    public String getUploadName() {
        return uploadName;
    }

    public void setUploadName(String uploadName) {
        this.uploadName = uploadName;
    }

    public String getFileName() {
        if (file != null)
            return file.getName();
        if (multipartFile != null)
            return multipartFile.getOriginalFilename();
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String urlForGet() {
        String _url = getUrl();
        if (_url.length() > 0) {
            String params = getUrlParams();
            if (params.length() > 0)
                _url += "?" + params;
        }
        return _url;
    }

    public String getUrlParams() {
        if (params == null)
            return "";
        StringBuilder builder = new StringBuilder();
        for (String key: params.keySet()) {
            if (builder.length() > 0)
                builder.append("&");
            String value = StringUtils.trimToEmpty(params.get(key));
            if (value.length() > 0) {
                try {
                    value = URLEncoder.encode(value, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            builder.append(key).append("=").append(value);
        }
        return builder.toString();
    }

    public HttpEntity getSendData() throws IOException {
        if (method == Method.POST) {
            if (StringUtils.isNotBlank(body))
                return getDataAsText();
            if (params != null && params.size() > 0)
                return getDataAsForm();
        }
        else if (method == Method.JSON)
            return getDataAsJson();
        else if (method == Method.UPLOAD)
            return getDataAsFile();
        return null;
    }

    public RequestConfig getConfigs() {
        return null;
    }

    public void clean() {
        IOUtils.closeQuietly(fileInputStream);
        IOUtils.closeQuietly(inputStream);
    }

    private HttpEntity getDataAsFile() throws IOException {
        if (file != null || multipartFile != null || inputStream != null) {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setCharset(Charset.forName("utf-8"));
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            String fileName = getFileName();
            String uploadName = getUploadName();
            if (file != null) {
                builder.addPart(uploadName, new FileBody(file, ContentType.DEFAULT_BINARY, fileName));
            }
            else if (multipartFile != null) {
                fileInputStream = multipartFile.getInputStream();
                builder.addBinaryBody(uploadName, fileInputStream,
                        ContentType.MULTIPART_FORM_DATA, fileName);
            }
            else if (inputStream != null) {
                builder.addBinaryBody(uploadName, inputStream, ContentType.MULTIPART_FORM_DATA, fileName);
            }
            if (params != null) {
                for (String key: params.keySet()) {
                    builder.addTextBody(key, params.get(key), ContentType.TEXT_PLAIN);
                }
            }
            return builder.build();
        }
        return null;
    }

    private HttpEntity getDataAsJson() {
        if (StringUtils.isNotBlank(body)) {
            StringEntity entity = new StringEntity(body, "utf-8");
            entity.setContentEncoding("utf-8");
            entity.setContentType(ContentType.APPLICATION_JSON.getMimeType());
            return entity;
        }
        return null;
    }

    private HttpEntity getDataAsForm() {
        if (params != null && params.size() > 0) {
            List<NameValuePair> values = new ArrayList<NameValuePair>();
            for (String key: params.keySet()) {
                values.add(new BasicNameValuePair(key, params.get(key)));
            }
            try {
                return new UrlEncodedFormEntity(values, "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage());
            }
        }
        return null;
    }

    private HttpEntity getDataAsText() {
        if (StringUtils.isNotBlank(body)) {
            StringEntity entity = new StringEntity(body, "utf-8");
            entity.setContentEncoding("utf-8");
            entity.setContentType(ContentType.TEXT_XML.getMimeType());
        }
        return null;
    }

}
