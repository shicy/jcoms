package org.scy.common.ds;

import org.scy.common.utils.HttpUtilsEx;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * 查询分页信息
 * Created by shicy on 2017/10/7.
 */
@SuppressWarnings("unused")
public class PageInfo implements Serializable {

    private static final long serialVersionUID = 1002017100710580000L;

    public static int defaultSize = 20;

    // 当前页
    private int page;

    // 每页大小
    private int size;

    // 总记录数
    private int total;

    public PageInfo() {
        // do nothing
    }

    public PageInfo(int total) {
        this(1, total);
    }

    public PageInfo(int page, int total) {
        this(page, defaultSize, total);
    }

    public PageInfo(int page, int size, int total) {
        this.setPage(page);
        this.setSize(size);
        this.setTotal(total);
    }

    /**
     * 根据请求参数创建分页信息
     */
    public static PageInfo create(HttpServletRequest request) {
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPage(HttpUtilsEx.getIntValue(request, "page", 1));
        pageInfo.setSize(HttpUtilsEx.getIntValue(request, "limit", defaultSize));
        return pageInfo;
    }

    public int getPage() {
        return page > 0 ? page : 1;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size > 0 ? size : defaultSize;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTotal() {
        return total > 0 ? total : 0;
    }

    public void setTotal(int total) {
        this.total = total;
    }

//    public int getPages() {
//        int pageTotal = this.getTotal();
//        if (pageTotal > 0) {
//            int pageCount = pageTotal / this.getSize();
//            if (pageCount * this.getSize() < pageTotal)
//                pageCount += 1;
//            return pageCount;
//        }
//        return 0;
//    }

}
