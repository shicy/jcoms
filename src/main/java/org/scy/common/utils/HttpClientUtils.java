package org.scy.common.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.scy.common.web.controller.HttpRequest;
import org.scy.common.web.controller.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Map;

/**
 * Http客户端
 * Created by shicy on 2020/11/3
 */
public abstract class HttpClientUtils {

    private final static Logger logger = LoggerFactory.getLogger(HttpClientUtils.class);

    public static HttpResponse doGet(String url, Map<String, String> params) {
        HttpRequest request = new HttpRequest(url);
        request.setMethod(HttpRequest.Method.GET);
        request.setParams(params);
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
        return null;
    }

    public static HttpResponse doUpload(String url, MultipartFile file, Map<String, String> params) {
        return null;
    }

    public static HttpResponse doDownload(String url, Map<String, String> params) {
        return null;
    }

    public static HttpResponse doRequest(HttpRequest request) {
        logger.debug(request.getMethod().getValue() + ": " + request.getUrl());

        HttpUriRequest httpRequest = null;
        if (request.getMethod() == HttpRequest.Method.GET) {
            httpRequest = new HttpGet(request.urlForGet());
        }
        else if (request.getMethod() == HttpRequest.Method.POST) {
            httpRequest = new HttpPost(request.getUrl());
        }

        CloseableHttpResponse httpResponse = null;
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        try {
            httpResponse = httpClient.execute(httpRequest);
            return new HttpResponse(httpResponse);
        } catch (Exception e) {
            return new HttpResponse(e);
        } finally {
            IOUtils.closeQuietly(httpResponse);
            IOUtils.closeQuietly(httpClient);
        }
    }

}
