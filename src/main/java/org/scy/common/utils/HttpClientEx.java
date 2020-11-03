package org.scy.common.utils;

import com.alibaba.druid.support.json.JSONUtils;
import org.scy.common.web.model.RequestModel;
import org.scy.common.web.model.ResponseModel;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Map;

/**
 * Http客户端
 * Created by shicy on 2020/11/3
 */
public abstract class HttpClientEx {

    public static ResponseModel doGet(String url, Map<String, String> params) {
        RequestModel requestModel = new RequestModel(url);
        requestModel.setMethod(RequestModel.Method.GET);
        requestModel.setParams(params);
        return doRequest(requestModel);
    }

    public static ResponseModel doPost(String url, Map<String, String> params) {
        RequestModel requestModel = new RequestModel(url);
        requestModel.setMethod(RequestModel.Method.POST);
        requestModel.setParams(params);
        return doRequest(requestModel);
    }

    public static ResponseModel doJson(String url, Object params) {
        RequestModel requestModel = new RequestModel(url);
        if (params != null) {
            requestModel.setMethod(RequestModel.Method.JSON);
            requestModel.setBody(JSONUtils.toJSONString(params));
        }
        else {
            requestModel.setMethod(RequestModel.Method.POST);
        }
        return doRequest(requestModel);
    }

    public static ResponseModel doUpload(String url, File file, Map<String, String> params) {
        return null;
    }

    public static ResponseModel doUpload(String url, MultipartFile file, Map<String, String> params) {
        return null;
    }

    public static ResponseModel doDownload(String url, Map<String, String> params) {
        return null;
    }

    public static ResponseModel doRequest(RequestModel requestModel) {
        return null;
    }

}
