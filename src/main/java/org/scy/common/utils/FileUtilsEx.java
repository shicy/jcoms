package org.scy.common.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 文件工具类，针对{@link org.apache.commons.io.FileUtils}的扩展
 * Created by shicy on 2017/6/14.
 */
public abstract class FileUtilsEx {

    private static Logger logger = LoggerFactory.getLogger(FileUtilsEx.class);

    /**
     * 获取资源文件
     * @param resourceName 资源文件名称
     * @return 返回 URL 对象，当资源不存在时返回 null
     */
    public static URL getResource(String resourceName) {
        if (StringUtils.isNotBlank(resourceName)) {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            return loader.getResource(resourceName);
        }
        return null;
    }

    /**
     * 批量获取资源文件
     * @param resourceNames 资源文件名称集
     * @return 返回资源 URL 对象集，不会为 null
     */
    public static URL[] getResources(String[] resourceNames) {
        List<URL> urls = new ArrayList<URL>();
        if (resourceNames != null && resourceNames.length > 0) {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            for (String resourceName: resourceNames) {
                URL resource = loader.getResource(resourceName);
                if (resource != null)
                    urls.add(resource);
            }
        }
        return urls.toArray(new URL[0]);
    }

    /**
     * 获取某个目录下的所有资源文件
     * @param resourceDir 文件目录
     * @return 返回资源 URL 对象集，不会为 null
     */
    public static URL[] getResources(String resourceDir) {
        return getResources(resourceDir, null, false);
    }

    /**
     * 获取某个目录下的所有资源文件
     * @param resourceDir 文件目录
     * @param ext 筛选文件的后缀名称（不含“.”）
     * @return 返回资源 URL 对象集，不会为 null
     */
    public static URL[] getResources(String resourceDir, String ext) {
        return getResources(resourceDir, ext, false);
    }

    /**
     * 获取某个目录下的所有资源文件
     * @param resourceDir 文件目录
     * @param deep 是否同时获取子目录下的资源文件
     * @return 返回资源 URL 对象集，不会为 null
     */
    public static URL[] getResources(String resourceDir, boolean deep) {
        return getResources(resourceDir, null, deep);
    }

    /**
     * 获取某个目录下的所有资源文件
     * @param resourceDir 文件目录
     * @param ext 筛选文件的后缀名称（不含“.”）
     * @param deep 是否同时获取子目录下的资源文件
     * @return 返回资源 URL 对象集，不会为 null
     */
    public static URL[] getResources(String resourceDir, String ext, boolean deep) {
        URL resource = getResource(resourceDir);
        if (resource != null) {
            if (isJarURL(resource)) {
                return getJarResources(resource, ext, deep);
            }
            else {
                return getFileResources(resource, ext, deep);
            }
        }
        return new URL[0];
    }

    /**
     * 获取包内资源文件zjzw65656256 i5442530
     * @param resource 资源根目录信息
     * @param ext 筛选文件后缀名称（不含“.”）
     * @param deep 是否获取子目录
     * @return
     */
    private static URL[] getJarResources(URL resource, String ext, boolean deep) {
        String resourcePath = resource.getFile();
        String resourceName = StringUtils.substringAfter(resourcePath, "!/");
        String jarFileName = StringUtils.substringBetween(resource.getFile(), "file:", "!/");

        List<URL> results = new ArrayList<URL>();
        try {
            JarFile jarFile = new JarFile(jarFileName);
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                if (!jarEntry.isDirectory()) {
                    String fileName = jarEntry.getName();

                    if (ext != null) {
                        String extName = getExtensionName(fileName);
                        if ((ext.length() == 0 && extName != null) || !ext.equals(extName))
                            continue;
                    }

                    if (fileName.equals(resourceName)) {
                        results.add(resource);
                    }
                    else if (StringUtils.startsWith(fileName, resourceName)) {
                        fileName = StringUtils.substring(fileName, resourceName.length());
                        if (!StringUtils.endsWith(resourceName, File.separator)) {
                            if (!StringUtils.startsWith(fileName, File.separator))
                                continue;
                            fileName = StringUtils.substring(fileName, 1);
                        }
                        if (deep || !fileName.contains(File.separator)) {
                            results.add(new URL("jar:file:" + jarFileName + "!/" + jarEntry.getName()));
                        }
                    }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return results.toArray(new URL[0]);
    }

    /**
     * 获取外部的资源文件（不在 jar 包内的资源）
     * @param resource 资源根目录信息
     * @param ext 筛选文件后缀名称（不含“.”）
     * @param deep 是否获取子目录文件
     * @return 返回资源文件集
     */
    private static URL[] getFileResources(URL resource, String ext, boolean deep) {
        File[] files = getFiles(toFile(resource), ext, deep);
        if (files.length > 0) {
            try {
                return FileUtils.toURLs(files);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new URL[0];
    }

    /**
     * 获取资源文件，将资源 URL 转换成文件对象 File，仅本地独立的文件可以转换
     * @param resource 资源信息
     * @return 返回改资源的文件对象，如果转换失败则返回 null
     */
    public static File getResourceFile(URL resource) {
        if (resource != null) {
            if (isFileUrl(resource)) {
                File file = toFile(resource);
                if (file != null && file.exists() && file.isFile())
                    return file;
            }
            else {
                logger.warn("无法将资源转换成文件对象：" + resource.getFile());
            }
        }
        return null;
    }

    /**
     * 获取资源文件，无法获取 jar 包中的资源文件
     * @param resourceFileName 资源文件路径名称
     * @return 返回文件的 File 对象，如果文件不存在则返回 null
     */
    public static File getResourceFile(String resourceFileName) {
        return getResourceFile(getResource(resourceFileName));
    }

    /**
     * 批量获取资源文件，无法获取 jar 包中的资源文件
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
     * 获取某个目录下的所有资源文件，不会遍历子目录
     * @param resourceFilePath 某个资源文件目录，如果是一个具体文件将只返回该文件，同{@link FileUtilsEx#getResourceFile(String)}
     * @return 返回存在的文件数组，不会为 null
     */
    public static File[] getResourceFiles(String resourceFilePath) {
        return getResourceFiles(resourceFilePath, null, false);
    }

    /**
     * 获取某个目录下的所有资源文件，不会遍历子目录
     * @param resourceFilePath 某个资源文件目录，如果是一个具体文件将只返回该文件，同{@link FileUtilsEx#getResourceFile(String)}
     * @param ext 过滤筛选文件的后缀名称（不含“.”）
     * @return 返回存在的文件数组，不会为 null
     */
    public static File[] getResourceFiles(String resourceFilePath, String ext) {
        return getResourceFiles(resourceFilePath, ext, false);
    }

    /**
     * 获取某个目录下的所有资源文件
     * @param resourceFilePath 某个资源文件目录，如果是一个具体文件将只返回该文件，同{@link FileUtilsEx#getResourceFile(String)}
     * @param deep 是否遍历子目录
     * @return 返回存在的文件数组，不会为 null
     */
    public static File[] getResourceFiles(String resourceFilePath, boolean deep) {
        return getResourceFiles(resourceFilePath, null, deep);
    }

    /**
     * 获取某个目录下的所有资源文件
     * @param resourceFilePath 某个资源文件目录，如果是一个具体文件将只返回该文件，同{@link FileUtilsEx#getResourceFile(String)}
     * @param ext 过滤筛选文件的后缀名称（不带“.”）
     * @param deep 是否遍历子目录
     * @return 返回存在的文件数组，不会为 null
     */
    public static File[] getResourceFiles(String resourceFilePath, String ext, boolean deep) {
        URL resource = getResource(resourceFilePath);
        if (resource != null) {
            if (isFileUrl(resource)) {
                return getFiles(getResourceFile(resourceFilePath), ext, deep);
            }
            else {
                logger.warn("无法获取包内资源文件：" + resource.getFile());
            }
        }
        return new File[0];
    }

    /**
     * 根据目录获取本地文件信息
     * @param dir 文件根目录（不包含），如果这是一个文件并且符合后缀名称将作为结果返回
     * @param ext 筛选文件后缀名称（不含“.”）
     * @param deep 是否获取子目录文件
     * @return 返回结果文件集，不会为 null
     */
    public static File[] getFiles(File dir, String ext, boolean deep) {
        List<File> results = new ArrayList<File>();
        if (dir != null) {
            if (dir.isDirectory()) {
                File[] subFiles = dir.listFiles();
                if (subFiles != null && subFiles.length > 0) {
                    for (File file: subFiles) {
                        if (file.isFile() || deep) {
                            File[] files = getFiles(file, ext, deep);
                            if (files.length > 0)
                                results.addAll(Arrays.asList(files));
                        }
                    }
                }
            }
            else if (dir.exists()) {
                if (ext != null) {
                    if (ext.length() == 0) {
                        if (getExtensionName(dir) == null)
                            results.add(dir);
                    }
                    else if (ext.equals(getExtensionName(dir))) {
                        results.add(dir);
                    }
                }
                else {
                    results.add(dir);
                }
            }
        }
        return results.toArray(new File[0]);
    }

    /**
     * 获取资源文件输入流
     * @param resourceFileName 资源文件路径名称
     * @return 返回该资源的输入流，如果资源不存在则返回 null
     */
    public static InputStream getResourceStream(String resourceFileName) {
        return getResourceStream(getResource(resourceFileName));
    }

    /**
     * 获取资源文件输入流
     * @param resource 资源文件
     * @return 返回该资源的输入流, 异常时返回 null
     */
    public static InputStream getResourceStream(URL resource) {
        if (resource != null) {
            try {
                return resource.openStream();
            }
            catch (IOException e) {
                logger.warn("获取资源文件流失败：" + resource.getFile());
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取文件后缀名称
     * @param fileName 文件名称
     * @return 返回文件后缀名称
     */
    public static String getExtensionName(String fileName) {
        if (StringUtils.isNoneBlank(fileName) && fileName.indexOf('.') >= 0) {
            String[] names = fileName.split("\\.");
            return names[names.length - 1];
        }
        return null;
    }

    /**
     * 获取文件后缀名称
     * @param file 文件对象
     * @return 返回文件后缀名称
     */
    public static String getExtensionName(File file) {
        return file != null ? getExtensionName(file.getName()) : null;
    }

    /**
     * 打印文件内容
     * @param file 文件信息
     * @param rows 打印的行数，小于 0 时打印所有行
     */
    public static void print(File file, int rows) {
        if (file != null) {
            System.out.println("文件：" + file.getAbsoluteFile());
            if (file.exists() && rows != 0) {
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                    while (true) {
                        String text = reader.readLine();
                        if (text == null)
                            break;
                        System.out.println(text);
                        if (rows > 0) {
                            if (--rows <= 0)
                                break;
                        }
                    }
                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    IOUtils.closeQuietly(reader);
                }
            }
            System.out.println();
        }
    }

    /**
     * 打印文件流，不关闭
     * @param inputStream 文件输入流
     * @param rows 打印的行数，小于等于 0 时打印所有行
     */
    public static void print(InputStream inputStream, int rows) {
        if (inputStream != null) {
            if (rows == 0)
                rows = -1;
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(inputStream));
                while (true) {
                    String text = reader.readLine();
                    if (text == null)
                        break;
                    System.out.println(text);
                    if (rows >= 0) {
                        if (--rows <= 0)
                            break;
                    }
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                IOUtils.closeQuietly(reader);
            }
            System.out.println();
        }
    }

    /**
     * 判断是不是文件 URL，指独立文件，而非包内或网络等资源
     * @param url 资源文件信息
     * @return 如果是文件资源返回 true，否则返回 false
     */
    public static boolean isFileUrl(URL url) {
        if (url != null) {
            String protocol = url.getProtocol();
            return "file".equals(protocol) || "vfsfile".equals(protocol) || "vfs".equals(protocol);
        }
        return false;
    }

    /**
     * 判断是不是 jar 包内文件
     * @param url 资源文件信息
     * @return 如果是 jar 包内的资源文件返回 true，否则返回 false
     */
    public static boolean isJarURL(URL url) {
        if (url != null) {
            String protocol = url.getProtocol();
            return "jar".equals(protocol) || "war".equals(protocol) || "zip".equals(protocol) || "vfszip".equals(protocol) || "wsjar".equals(protocol);
        }
        return false;
    }

    /**
     * 资源转换成文件对象
     * @param resource 资源文件信息
     * @return 返回文件对象
     */
    public static File toFile(URL resource) {
        if (resource != null) {
            try {
                return new File(resource.toURI().getSchemeSpecificPart());
            }
            catch (URISyntaxException e) {
                return new File(resource.getFile());
            }
        }
        return null;
    }

}
