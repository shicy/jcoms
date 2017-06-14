package org.scy.common.utils;

import org.apache.commons.io.FileUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件工具类，针对{@link org.apache.commons.io.FileUtils}的扩展
 * Created by shicy on 2017/6/14.
 */
public abstract class FileUtilsEx {

    /**
     * 获取资源文件
     * @param resourceFileName 资源文件名称，包路径下需要“classpath:”前缀
     * @return 返回文件的 File 对象，如果文件不存在则返回 null
     */
    public static File getResourceFile(String resourceFileName) {
        try {
            File file = ResourceUtils.getFile((resourceFileName));
            if (file != null && file.exists())
                return file;
        }
        catch (FileNotFoundException e) {
            // do nothing
        }
        return null;
    }

    /**
     * 批量获取资源文件，包路径下需要“classpath:”前缀
     * @param resourceFileNames 多个资源文件名称数组
     * @return 返回存在的文件数组，不会为 null
     */
    public static File[] getResourceFiles(String[] resourceFileNames) {
        List<File> files = new ArrayList<File>();
        if (resourceFileNames != null) {
            for (String fileName: resourceFileNames) {
                File file = getResourceFile(fileName);
                if (file != null)
                    files.add(file);
            }
        }
        return files.toArray(new File[0]);
    }

    /**
     * 获取某个目录下的所有资源文件，不会遍历子目录，包路径下需要“classpath:”前缀
     * @param resourceFilePath 某个资源文件目录，如果是一个具体文件将只返回该文件，同{@link FileUtilsEx#getResourceFile(String)}
     * @return 返回存在的文件数组，不会为 null
     */
    public static File[] getResourceFiles(String resourceFilePath) {
        return getResourceFiles(resourceFilePath, null, false);
    }

    /**
     * 获取某个目录下的所有资源文件，不会遍历子目录，包路径下需要“classpath:”前缀
     * @param resourceFilePath 某个资源文件目录，如果是一个具体文件将只返回该文件，同{@link FileUtilsEx#getResourceFile(String)}
     * @param ext 过滤筛选文件的后缀名称（不带“.”）
     * @return 返回存在的文件数组，不会为 null
     */
    public static File[] getResourceFiles(String resourceFilePath, String ext) {
        return getResourceFiles(resourceFilePath, ext, false);
    }

    /**
     * 获取某个目录下的所有资源文件，包路径下需要“classpath:”前缀
     * @param resourceFilePath 某个资源文件目录，如果是一个具体文件将只返回该文件，同{@link FileUtilsEx#getResourceFile(String)}
     * @param deep 是否遍历子目录
     * @return 返回存在的文件数组，不会为 null
     */
    public static File[] getResourceFiles(String resourceFilePath, boolean deep) {
        return getResourceFiles(resourceFilePath, null, deep);
    }

    /**
     * 获取某个目录下的所有资源文件，包路径下需要“classpath:”前缀
     * @param resourceFilePath 某个资源文件目录，如果是一个具体文件将只返回该文件，同{@link FileUtilsEx#getResourceFile(String)}
     * @param ext 过滤筛选文件的后缀名称（不带“.”）
     * @param deep 是否遍历子目录
     * @return 返回存在的文件数组，不会为 null
     */
    public static File[] getResourceFiles(String resourceFilePath, String ext, boolean deep) {
        List<File> files = new ArrayList<File>();

        try {
            URL fileUrl = ResourceUtils.getURL(resourceFilePath);
            if (fileUrl != null) {
                File dir = new File(fileUrl.toURI());
                if (dir.isDirectory()) {

                }
                else {

                }
            }
        }
        catch (FileNotFoundException e) {
            // do nothing
        }
        catch (URISyntaxException e) {
            // do nothing
        }

//        if (fileUrl != null) {
//            try {
//                File dir = new File(fileUrl.toURI());
//                if (dir.isDirectory()) {
//
//                }
//                else {
////                    File file = getResourceFile()
//                }
//            }
//            catch (URISyntaxException e) {
//
//            }
//        }
//        if (resourceFilePath != null) {

//            int dot = resourceFilePath.lastIndexOf('.');
//            if (dot >= 0) {
//                File file = getResourceFile(resourceFilePath);
//                if (file != null)
//                    files.add(file);
//            }
//            else {
//            }
//        }

//        URL url = null;
//        try {
//            url = ResourceUtils.getURL(resourceFilePath);
//        }
//        catch (FileNotFoundException e) {
//            // do nothing
//        }
//
//        if (url != null) {
//
//        }

        return files.toArray(new File[0]);
    }

}
