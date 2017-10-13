package org.scy.common;

/**
 * 常量定义
 * Created by shicy on 2017/8/31.
 */
@SuppressWarnings("unused")
public final class Const {

    // 有效
    public final static short ENABLED = 1;

    // 无效
    public final static short DISABLED = 0;

    // 平台帐号
    public final static String PLATFORM_CODE = "1000000000000000";

    // 参数错误
    public final static short MSG_CODE_PARAMINVALID  = 1000;

    // 参数缺失
    public final static short MSG_CODE_PARAMMISSING = 1001;

    // 参数格式错误
    public final static short MSG_CODE_PARAMFORMATERROR = 1002;

    // 存在重复
    public final static short MSG_CODE_DUPLICATED = 1003;

    // 对象不存在
    public final static short MSG_CODE_NOTEXIST = 1004;

    // 数据为空
    public final static short MSG_CODE_DATAEMPTY = 1005;

    // 服务拒绝访问或连接失败
    public final static short MSG_CODE_SERVERREFUSED = 1010;

    // 当前帐户错误，不存在或密码错误
    public final static short MSG_CODE_ACCOUNTERROR = 1100;

    // 没有操作权限
    public final static short MSG_CODE_NOPERMISSION = 1101;

    // 验证码错误
    public final static short MSG_CODE_VALIDFAILED = 1102;

}
