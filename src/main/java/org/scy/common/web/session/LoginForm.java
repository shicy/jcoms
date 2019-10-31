package org.scy.common.web.session;

/**
 * 用户登录提交表单
 * Created by shicy 2019/10/31
 */
public class LoginForm {

    // 登录用户名
    private String username;

    // 登录密码
    private String password;

    // 登录类型，默认所有登录方式
    private int loginType;

    // 过期时间（秒），默认无限期
    private int expires;

    // 验证码编号，使用“/login/code”获取登录验证码
    private String validCodeId;

    // 验证码
    private String validCode;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getLoginType() {
        return Math.max(0, loginType);
    }

    public void setLoginType(int loginType) {
        this.loginType = loginType;
    }

    public int getExpires() {
        return Math.max(0, expires);
    }

    public void setExpires(int expires) {
        this.expires = expires;
    }

    public String getValidCodeId() {
        return validCodeId;
    }

    public void setValidCodeId(String validCodeId) {
        this.validCodeId = validCodeId;
    }

    public String getValidCode() {
        return validCode;
    }

    public void setValidCode(String validCode) {
        this.validCode = validCode;
    }
}
