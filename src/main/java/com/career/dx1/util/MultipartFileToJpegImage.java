package com.career.dx1.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.career.dx1.domain.Image;
import com.career.dx1.util.image.BypassBufferedImage;
import com.career.dx1.util.image.DicomImageConverter;

public class MultipartFileToJpegImage {
    static Logger logger = LoggerFactory.getLogger(MultipartFileToJpegImage.class);

    public static Image convert(MultipartFile file) {
        try {
            String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
            if (ext == null) {
                return Image.exception(new UnsupportedOperationException("unknown extension"), multipartFileTransferToOutputStream(file));
            }

            BufferedImage bi = null;
            switch (ext.toLowerCase()) {
                case "dcm":
                    bi = DicomImageConverter.convert(file.getInputStream());
                    break;
                case "jpg":
                    bi = BypassBufferedImage.convert(file.getInputStream());
                    break;
                default:
                    bi = BypassBufferedImage.convert(file.getInputStream());
                    break;
            }

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(bi, "jpg", os);
            return Image.valueOf(bi, os);
        } catch (Exception e) {
            return Image.exception(e, multipartFileTransferToOutputStream(file));
        }
    }

    private static final int DEFAULT_BUFFER_SIZE = 8192;

    public static OutputStream multipartFileTransferToOutputStream(MultipartFile file) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            InputStream in = file.getInputStream();
            Objects.requireNonNull(out, "out");
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            long transferred = 0;
            int read;
            while ((read = in.read(buffer, 0, DEFAULT_BUFFER_SIZE)) >= 0) {
                out.write(buffer, 0, read);
                transferred += read;
            }
            if (read < 0) {
                throw new IOException(String.format(
                    "InputStream read fail: transferred %d", transferred)
                );
            }
            return out;
        } catch (Exception e) {
            return out;
        }
    }
}
