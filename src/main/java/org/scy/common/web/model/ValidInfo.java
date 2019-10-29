package org.scy.common.web.model;

import java.io.Serializable;

/**
 * 验证码
 * Created by shicy 2019/10/28
 */
public class ValidInfo implements Serializable {

    private static final long serialVersionUID = 1002019102816500000L;

    // 验证码
    private String code;

    // 图片
    private String imageUrl;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
