package org.scy.common.web.model;

import java.io.Serializable;

/**
 * 验证码
 * Created by shicy 2019/10/28
 */
public class ValidInfo implements Serializable {

    private static final long serialVersionUID = 1002019102816500000L;

    // 验证码编号
    private String codeId;

    // 图片
    private String imageUrl;

    public String getCodeId() {
        return codeId;
    }

    public void setCodeId(String codeId) {
        this.codeId = codeId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
