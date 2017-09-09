package org.scy.common.ds.druid;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

/**
 * Druid 监控配置信息
 * 访问：/druid/index.html
 * Created by shicy on 2017/5/16.
 */
//@Configuration
public class DruidConfiguration {

    @Bean
    public ServletRegistrationBean DruidStatViewServle() {
        ServletRegistrationBean bean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
        bean.addInitParameter("allow", "127.0.0.1");
        bean.addInitParameter("loginUsername", "shicy");
        bean.addInitParameter("loginPassword", "u");
        bean.addInitParameter("resetEnable", "false");
        return bean;
    }

    @Bean
    public FilterRegistrationBean druidStatFilter() {
        FilterRegistrationBean bean = new FilterRegistrationBean(new WebStatFilter());
        bean.addUrlPatterns("/*");
        bean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        return  bean;
    }

}
