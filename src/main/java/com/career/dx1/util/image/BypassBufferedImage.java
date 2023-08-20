package com.career.dx1.util.image;

import java.io.IOException;
import java.io.InputStream;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BypassBufferedImage {
    static Logger logger = LoggerFactory.getLogger(BypassBufferedImage.class);

    public static BufferedImage convert(InputStream is) throws IOException {
        return ImageIO.read(is);
    }
}
