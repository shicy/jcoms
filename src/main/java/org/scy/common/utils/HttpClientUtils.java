package org.scy.common.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.scy.common.web.controller.HttpRequest;
import org.scy.common.web.controller.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * Http客户端
 * Created by shicy on 2020/11/3
 */
public abstract class HttpClientUtils {

    private final static Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);

    public static HttpResponse doGet(String url) {
        return doGet(url, null);
    }

    public static HttpResponse doGet(String url, Map<String, String> params) {
        HttpRequest request = new HttpRequest(url);
        request.setMethod(HttpRequest.Method.GET);
        request.setParams(params);
        return doRequest(request);
    }

    public static HttpResponse doPost(String url) {
        HttpRequest request = new HttpRequest(url);
        request.setMethod(HttpRequest.Method.POST);
        return doRequest(request);
    }

    public static HttpResponse doPost(String url, String data) {
        HttpRequest request = new HttpRequest(url);
        request.setMethod(HttpRequest.Method.POST);
        request.setBody(data);
        return doRequest(request);
    }

    public static HttpResponse doPost(String url, Map<String, String> params) {
        HttpRequest request = new HttpRequest(url);
        request.setMethod(HttpRequest.Method.POST);
        request.setParams(params);
        return doRequest(request);
    }

    public static HttpResponse doJson(String url, Object params) {
        HttpRequest request = new HttpRequest(url);
        if (params != null) {
            request.setMethod(HttpRequest.Method.JSON);
            request.setBody(JSONObject.toJSONString(params));
        }
        else {
            request.setMethod(HttpRequest.Method.POST);
        }
        return doRequest(request);
    }

    public static HttpResponse doUpload(String url, File file, Map<String, String> params) {
        return doUpload(url, file, null, params);
    }

    public static HttpResponse doUpload(String url, File file, String fileName, Map<String, String> params) {
        HttpRequest request = new HttpRequest(url);
        request.setMethod(HttpRequest.Method.UPLOAD);
        request.setFile(file);
        request.setFileName(fileName);
        request.setParams(params);
        return doRequest(request);
    }

    public static HttpResponse doUpload(String url, MultipartFile file, Map<String, String> params) {
        return doUpload(url, file, null, params);
    }

    public static HttpResponse doUpload(String url, MultipartFile file, String fileName, Map<String, String> params) {
        HttpRequest request = new HttpRequest(url);
        request.setMethod(HttpRequest.Method.UPLOAD);
        request.setMultipartFile(file);
        request.setFileName(fileName);
        request.setParams(params);
        return doRequest(request);
    }

    public static HttpResponse doUpload(String url, InputStream inputStream, String fileName, Map<String, String> params) {
        HttpRequest request = new HttpRequest(url);
        request.setMethod(HttpRequest.Method.UPLOAD);
        request.setInputStream(inputStream);
        request.setFileName(fileName);
        request.setParams(params);
        return doRequest(request);
    }

    public static HttpResponse doDownload(String url, Map<String, String> params, OutputStream output) {
        HttpRequest request = new HttpRequest(url);
        request.setMethod(HttpRequest.Method.DOWNLOAD);
        request.setParams(params);
        return doRequest(request, output);
    }

    public static HttpResponse doRequest(HttpRequest request) {
        return doRequest(request, null);
    }

    public static HttpResponse doRequest(HttpRequest request, OutputStream output) {
        logger.debug(request.getMethod().getValue() + ": " + request.getUrl());

        HttpResponse response = new HttpResponse();

        HttpRequestBase httpRequest;
        if (request.getMethod() == HttpRequest.Method.GET ||
                request.getMethod() == HttpRequest.Method.DOWNLOAD) {
            httpRequest = new HttpGet(request.urlForGet());
        }
        else if (request.getMethod() == HttpRequest.Method.POST ||
                request.getMethod() == HttpRequest.Method.JSON ||
                request.getMethod() == HttpRequest.Method.UPLOAD) {
            httpRequest = new HttpPost(request.getUrl());
            try {
                ((HttpPost)httpRequest).setEntity(request.getSendData());
            } catch (IOException e) {
                e.printStackTrace();
                request.clean();

                response.setError(e);
                return response;
            }
        }
        else {
            throw new RuntimeException("暂不支持请求方式：" + request.getMethod().getValue());
        }

        httpRequest.setConfig(request.getConfigs());

        CloseableHttpResponse httpResponse = null;
        CloseableHttpClient httpClient = null;
        try {
            httpClient = HttpClientBuilder.create().build();
            httpResponse = httpClient.execute(httpRequest);
            response.setOutput(output);
            response.setResponse(httpResponse);
        } catch (Exception e) {
            response.setError(e);
        } finally {
            request.clean();
            IOUtils.closeQuietly(httpResponse);
            IOUtils.closeQuietly(httpClient);
        }
        return response;
    }

}
