package org.scy.common.utils;

import org.apache.commons.io.IOUtils;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 验证码生成
 * Created by shicy on 2017/9/5.
 */
public abstract class ValidCodeUtils {

    private final static char[] VALIDATE_CODES = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static BASE64Encoder base64Encoder = new BASE64Encoder();

    /**
     * 获取一个4位的验证码
     * @return
     */
    public static String getCode() {
        return getCode(4);
    }

    /**
     * 获取一个随机验证码号，最多10位码
     * @param length
     * @return
     */
    public static String getCode(int length) {
        if (length < 1)
            length = 1;
        if (length > 10)
            length = 10;

        String code = "";
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(VALIDATE_CODES.length);
            code += VALIDATE_CODES[index];
        }
        return code;
    }

    /**
     * 获取验证码图片的 Base64 编码，默认宽度100，高度40
     * @param code 验证码号
     * @return
     */
    public static String getBase64CodeImage(String code) {
        return getBase64CodeImage(code, 100, 40);
    }

    /**
     * 获取验证码图片的 Base64 编码
     * @param code 验证码号
     * @param width 图片的宽度
     * @param height 图片的高度
     * @return
     */
    public static String getBase64CodeImage(String code, int width, int height) {
        BufferedImage image = getCodeImage(code, width, height);
        if (image != null) {
            ByteArrayOutputStream stream = null;
            try {
                stream = new ByteArrayOutputStream();
                ImageIO.write(image, "png", stream);
                String base64Code = base64Encoder.encode(stream.toByteArray());
                base64Code = base64Code.replaceAll("\n", "").replaceAll("\r", "");
                base64Code = URLEncoder.encode(base64Code, "utf-8");
                return "data:image/png;base64," + base64Code;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                IOUtils.closeQuietly(stream);
            }
        }
        return null;
    }

    /**
     * 生成验证码图片，默认宽度100，高度40
     * @param code 验证码号
     * @return
     */
    public static BufferedImage getCodeImage(String code) {
        return getCodeImage(code, 100, 40);
    }

    /**
     * 生成验证码图片
     * @param code 验证码号
     * @param width 图片宽度
     * @param height 图片高度
     * @return
     */
    public static BufferedImage getCodeImage(String code, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();

        // 白色背景
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, height);

        Random random = new Random();
        List<Color> colors = new ArrayList<Color>();
        colors.add(Color.WHITE);

        // 绘制编码
        drawCodes(graphics, width, height, code, colors, random);

        // 绘制干扰线
        drawLines(graphics, width, height, colors, random);

        // 绘制噪点
        drawNoise(image, width, height, colors, random);

        return image;
    }

    private static void drawCodes(Graphics2D graphics, int width, int height, String code,
            List<Color> exceptColors, Random random) {
        int fontSize = (int)Math.round(height * 0.7);
        graphics.setFont(new Font("Times New Roman", Font.ROMAN_BASELINE, fontSize));

        int fontWidth = (int)Math.round(width * 0.8 / code.length());
        int offsetX = (int)Math.ceil((width - fontWidth * code.length()) / 2);
        int offsetY = height - (int)Math.floor(height * 0.25);

        char[] codeChars = code.toCharArray();
        for (int i = 0; i < codeChars.length; i++) {
            graphics.setColor(getRandomColor(exceptColors, random));
            graphics.drawChars(codeChars, i, 1, offsetX, offsetY);
            offsetX += fontWidth;
        }
    }

    /**
     * 绘制干扰线
     * @param graphics
     * @param width
     * @param height
     * @param exceptColors
     * @param random
     */
    private static void drawLines(Graphics2D graphics, int width, int height, List<Color> exceptColors, Random random) {
        int lineCount = random.nextInt(5) + 4;
        int[] rect = new int[]{width/2, height/2, width*2, height*2};
        for (int i = 0; i < lineCount; i++) {
            int x1 = random.nextInt(rect[2]) - rect[0];
            int y1 = random.nextInt(rect[3]) - rect[1];
            int x2 = random.nextInt(rect[2]) - rect[0];
            int y2 = random.nextInt(rect[3]) - rect[1];
            x1 = Math.min(Math.max(x1, 0), width);
            y1 = Math.min(Math.max(y1, 0), height);
            x2 = Math.min(Math.max(x2, 0), width);
            y2 = Math.min(Math.max(y2, 0), height);
            graphics.setColor(getRandomColor(exceptColors, random));
            graphics.drawLine(x1, y1, x2, y2);
        }
    }

    /**
     * 绘制噪点
     * @param image
     * @param width
     * @param height
     * @param exceptColors
     * @param random
     */
    private static void drawNoise(BufferedImage image, int width, int height, List<Color> exceptColors, Random random) {
        int noiseCount = (int)(width * height * 0.05);
        for (int i = 0; i < noiseCount; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            image.setRGB(x, y, getRandomColor(exceptColors, random).getRGB());
        }
    }

    /**
     * 随机生成一个颜色
     * @param random
     * @return
     */
    private static Color getRandomColor(List<Color> exceptColors, Random random) {
        if (random == null)
            random = new Random();
        while (true) {
            int red = random.nextInt(255);
            int green = random.nextInt(255);
            int blue = random.nextInt(255);
            Color color = new Color(red, green, blue);
            if (exceptColors != null) {
                for (Color color1: exceptColors) {
                    if (color.equals(color1))
                        continue;
                }
                exceptColors.add(color);
            }
            return color;
        }
    }

}
