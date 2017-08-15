package org.scy.common.web.model;

import java.io.Serializable;
import java.util.Date;

/**
 * 模型基类，包含：id, createdDate, updatedDate, saasId
 * Created by shicy on 2017/5/11.
 */
public class BaseModel implements Serializable {

    private static final long serialVersionUID = 1002017051100000000L;

    /**
     * 模型编号，主键
     */
    private int id;

    /**
     * 创建时间，时间戳：毫秒
     */
    private Long tcreated;

    /**
     * 更新时间，时间戳：毫秒
     */
    private Long tupdated;

    /**
     * 创建时间，字符串格式：yyyy-MM-dd HH:mm:ss.SSS
     */
    private String dcreated;

    /**
     * 更新时间，字符串格式：yyyy-MM-dd HH:mm:ss.SSS
     */
    private String dupdated;

    /**
     * 平台租户编号
     */
    private int saasId;

    /**
     * 获取模型编号
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * 设置模型编号
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * 获取创建时间戳
     * @return
     */
    public Long getTcreated() {
        return this.tcreated;
    }

    /**
     * 设置创建时间戳
     * @param tcreated
     */
    public void setTcreated(Long tcreated) {
        this.tcreated = tcreated;
    }

    /**
     * 获取更新时间戳
     * @return
     */
    public Long getTupdated() {
        return this.tupdated;
    }

    /**
     * 设置更新时间戳
     * @param tupdated
     */
    public void setTupdated(Long tupdated) {
        this.tupdated = tupdated;
    }

    /**
     * 获取创建时间字符串，如：2017-05-11 16:50:00
     * @return
     */
    public String getDcreated() {
        return this.dcreated;
    }

    /**
     * 设置创建时间字符串，如：2017-05-11 16:50:00
     * @param dcreated
     */
    public void setDcreated(String dcreated) {
        this.dcreated = dcreated;
    }

    /**
     * 获取更新时间字符串，如：2017-05-11 17:00:00
     * @return
     */
    public String getDupdated() {
        return this.dupdated;
    }

    /**
     * 设置更新时间字符串，如：2017-05-11 17:00:00
     * @param dupdated
     */
    public void setDupdated(String dupdated) {
        this.dupdated = dupdated;
    }

    /**
     * 获取创建时间
     * @return
     */
    public Date getCreatedDate() {
        return null;
    }

    /**
     * 设置创建时间
     * @param createdDate
     */
    public void setCreatedDate(Date createdDate) {

    }

    /**
     * 获取更新时间
     * @return
     */
    public Date getUpdatedDate() {
        return null;
    }

    /**
     * 设置更新时间
     * @param updatedDate
     */
    public void setUpdatedDate(Date updatedDate) {

    }

    /**
     * 获取平台租户编号
     * @return
     */
    public int getSaasId() {
        return this.saasId;
    }

    /**
     * 设置平台租户编号
     * @param saasId
     */
    public void setSaasId(int saasId) {
        this.saasId = saasId;
    }

}
