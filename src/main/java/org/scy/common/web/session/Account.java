package org.scy.common.web.session;

/**
 * 帐户信息
 * Created by shicy on 2017/10/13.
 */
@SuppressWarnings("unused")
public class Account {

    private int id;
    private String name;
    private String code;
    private int state;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

}
