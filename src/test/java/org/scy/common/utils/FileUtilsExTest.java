package org.scy.common.utils;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.*;

/**
 * 文件工具类测试
 * Created by shicy on 2017/6/14.
 */
public class FileUtilsExTest {

    @Test
    public void testGetResourceFile() {
        String fileName = "classpath:org/scy/common/test/[1.0.0]base_v1.0.sql";
        File file = FileUtilsEx.getResourceFile(fileName);

        if (file != null)
            this.showFiles(new File[]{file});

        Assert.assertNotNull("获取文件失败", file);
    }

    private void showFiles(File[] files) {
        if (files != null && files.length > 0) {
            for (File file: files) {
                System.out.println(file.getAbsoluteFile());
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                    String text = null;
                    while ((text = reader.readLine()) != null) {
                        System.out.println(text);
                    }
                }
                catch (FileNotFoundException e) {
                    //
                }
                catch (IOException e) {
                    //
                }
                finally {
                    IOUtils.closeQuietly(reader);
                }
            }
        }
    }

}
