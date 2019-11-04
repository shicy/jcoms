package org.scy.common.web.model;

import com.alibaba.fastjson.annotation.JSONField;
import org.scy.common.Const;

import java.io.Serializable;
import java.util.Date;

/**
 * 模型基类，包含：id, createdDate, updatedDate, saasId
 * Created by shicy on 2017/5/11.
 */
@SuppressWarnings("unused")
public class BaseModel implements Serializable {

    private static final long serialVersionUID = 1002017051100000000L;

    // 模型编号，主键
    private int id;

    // 状态：0-无效 1-有效
    private short state = Const.ENABLED;

    // 创建用户编号
    private Integer creatorId;

    // 创建时间，时间戳：毫秒
    private Long createTime;

    // 最后更新用户编号
    private Integer updatorId;

    // 最后更新时间，时间戳：毫秒
    private Long updateTime;

    // 平台租户编号
    private int paasId;

    /**
     * 获取模型编号
     */
    public int getId() {
        return this.id;
    }

    /**
     * 设置模型编号
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * 获取状态
     */
    public short getState() {
        return this.state;
    }

    /**
     * 设置状态
     */
    public void setState(short state) {
        this.state = state;
    }

    /**
     * 获取创建用户编号
     */
    public Integer getCreatorId() {
        return this.creatorId;
    }

    /**
     * 设置创建用户编号
     */
    public void setCreatorId(Integer creatorId) {
        this.creatorId = creatorId;
    }

    /**
     * 获取更新用户编号
     */
    public Integer getUpdatorId() {
        return this.updatorId;
    }

    /**
     * 设置更新用户编号
     */
    public void setUpdatorId(Integer updatorId) {
        this.updatorId = updatorId;
    }

    /**
     * 获取创建时间，返回时间戳
     */
    public Long getCreateTime() {
        return this.createTime;
    }

    /**
     * 设置创建时间
     */
    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取创建时间，返回日期对象
     */
    @JSONField(serialize = false)
    public Date getCreateDate() {
        return this.createTime != null ? new Date(this.createTime) : null;
    }

    /**
     * 设置创建时间
     */
    public void setCreateDate(Date createDate) {
        this.createTime = createDate != null ? createDate.getTime() : null;
    }

    /**
     * 获取更新时间，返回时间戳
     */
    public Long getUpdateTime() {
        return this.updateTime;
    }

    /**
     * 设置更新时间
     */
    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 获取更新时间，返回日期对象
     */
    @JSONField(serialize = false)
    public Date getUpdateDate() {
        return this.updateTime != null ? new Date(this.updateTime) : null;
    }

    /**
     * 设置更新时间
     */
    public void setUpdateDate(Date updateDate) {
        this.updateTime = updateDate != null ? updateDate.getTime() : null;
    }

    /**
     * 获取平台租户编号
     */
    public int getPaasId() {
        return this.paasId;
    }

    /**
     * 设置平台租户编号
     */
    public void setPaasId(int paasId) {
        this.paasId = paasId;
    }

}
