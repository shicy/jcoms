package org.scy.common.ds;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 数据库更新测试
 * Created by shicy on 2017/6/9.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Import({TestDataSourceBeanDefinition.class})
public class DbUpgradeTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void doTest() {
        DbUpgrade upgrade = new DbUpgrade(jdbcTemplate, "org/scy/common/test/scripts");
        upgrade.setListener(new DbUpgrade.DbUpgradeListener() {

        });
        upgrade.run();
    }
}
