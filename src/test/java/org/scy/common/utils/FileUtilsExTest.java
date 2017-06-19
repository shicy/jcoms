package org.scy.common.utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * 文件工具类测试
 * Created by shicy on 2017/6/14.
 */
public class FileUtilsExTest {

    /**
     * 外部资源文件
     */
    @Test
    public void testGetResourceFile() {
        String fileName = "org/scy/common/test/[1.0.0]base_v1.0.sql";
        File file = FileUtilsEx.getResourceFile(fileName);
        Assert.assertNotNull("获取文件失败", file);
        FileUtilsEx.print(file, 10);
    }

    /**
     * jar 包内资源文件（不能获取）
     */
    @Test
    public void testGetResourceFile2() {
        String fileName = "LICENSE-junit.txt";
        File file = FileUtilsEx.getResourceFile(fileName);
        Assert.assertNull("获取文件失败", file);
    }

    /**
     * 外部资源文件流
     */
    @Test
    public void testGetResourceInputStream() {
        String fileName = "org/scy/common/test/[1.0.0]base_v1.0.sql";
        InputStream stream = FileUtilsEx.getResourceStream(fileName);
        Assert.assertNotNull("获取文件流失败", stream);
        try {
            FileUtilsEx.print(stream, 10);
        }
        finally {
            IOUtils.closeQuietly(stream);
        }
    }

    /**
     * jar 包内资源文件流（可以获取）
     */
    @Test
    public void testGetResourceInputStream2() {
        String fileName = "LICENSE-junit.txt";
        InputStream stream = FileUtilsEx.getResourceStream(fileName);
        Assert.assertNotNull("获取文件流失败", stream);
        try {
            FileUtilsEx.print(stream, 10);
        }
        finally {
            IOUtils.closeQuietly(stream);
        }
    }

    /**
     * 批量获取资源文件（不能获取 jar 包内资源文件, 忽略不存在的文件）
     */
    @Test
    public void testGetResourceFiles() {
        String[] fileNames = new String [5];
        fileNames[0] = "org/scy/common/test/[1.0.0]base_v1.0.sql";
        fileNames[1] = "org/scy/common/test/readme.txt";
        fileNames[2] = "org/scy/common/logback-base.xml";
        fileNames[3] = "org/scy/common/nofile.txt";
        fileNames[4] = "LICENSE-junit.txt";

        File[] files = FileUtilsEx.getResourceFiles(fileNames);
        Assert.assertTrue("批量获取文件数不正确", files.length == 3);
        this.showFiles(files, 0);
    }

    /**
     * 批量获取资源文件（可以获取 jar 包内资源文件, 忽略不存在的文件）
     */
    @Test
    public void testGetResourceFiles2() {
        String[] fileNames = new String [5];
        fileNames[0] = "org/scy/common/test/[1.0.0]base_v1.0.sql";
        fileNames[1] = "org/scy/common/test/readme.txt";
        fileNames[2] = "org/scy/common/logback-base.xml";
        fileNames[3] = "org/scy/common/nofile.txt";
        fileNames[4] = "LICENSE-junit.txt";

        URL[] resources = FileUtilsEx.getResources(fileNames);
        Assert.assertTrue("批量获取资源数不正确", resources.length == 4);
        this.showFiles(resources, 2);
    }

    /**
     * 按目录获取的资源文件
     */
    @Test
    public void testGetResourceFilesWithDir() {
        String fileDir = "org/scy/common/test";
        URL[] resources = FileUtilsEx.getResources(fileDir);
        this.showFiles(resources, 2);
    }

    /**
     * 按目录获取 jar 包内文件
     */
    @Test
    public void testGetResourceFilesWithDir2() {
        String fileDir = "org/junit/rules";
        fileDir = "META-INF/maven/com.alibaba";
        URL[] resources = FileUtilsEx.getResources(fileDir, true);
        this.showFiles(resources, 3);
    }

    private void showFiles(File[] files, int rows) {
        if (files != null && files.length > 0) {
            for (File file: files) {
                FileUtilsEx.print(file, rows);
            }
        }
    }

    private void showFiles(URL[] files, int rows) {
        if (files != null && files.length > 0) {
            for (URL file: files) {
                System.out.println("资源：" + file.getFile());
                InputStream stream = null;
                try {
                    stream = file.openStream();
                    FileUtilsEx.print(stream, rows);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    IOUtils.closeQuietly(stream);
                }
            }
        }
    }

    @Test
    public void myTest() {
        String fileName = "a/b/c/d";
        String[] names = fileName.split(File.separator);
        for (String n: names) {
            System.out.println(n);
        }
        System.out.println(names.length);
    }

}
