package org.scy.common.utils;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * 验证码测试类
 */
public class ValidCodeUtilsTest {

//    @Test
    public void testSaveToFiel() {
        String code = ValidCodeUtils.getCode(4);
        BufferedImage image = ValidCodeUtils.getCodeImage(code);
        OutputStream stream = null;
        try {
            String fileName = "/mywork/temp/code.png";
            stream = new FileOutputStream(new File(fileName));
            ImageIO.write(image, "png", stream);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            IOUtils.closeQuietly(stream);
        }
        System.out.println(ValidCodeUtils.getBase64CodeImage(code));
    }

}
