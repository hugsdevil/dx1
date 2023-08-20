package com.career.dx1.util.image;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.spi.IIORegistry;
import javax.imageio.stream.ImageInputStream;

import org.dcm4che3.imageio.plugins.dcm.DicomImageReadParam;
import org.dcm4che3.imageio.plugins.dcm.DicomImageReader;
import org.dcm4che3.imageio.plugins.dcm.DicomImageReaderSpi;
import org.dcm4che3.opencv.NativeJ2kImageReaderSpi;
import org.dcm4che3.opencv.NativeJLSImageReaderSpi;
import org.dcm4che3.opencv.NativeJPEGImageReaderSpi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DicomImageConverter {
    static Logger logger = LoggerFactory.getLogger(DicomImageConverter.class);
    static boolean loaded = false;

    // 반드시 target/classes/lib/opencv 디렉토리 및 라이브러리가 존재해야 함
    // 다른 방법이 있으면 입맛대로 구현해도 됨
    static {
        try {
            if (loaded) {
                init();
                loaded = true;
            }
        } catch (Exception e) {
            logger.error("{}", e);
        }
    }

    public static void init() throws Exception {
        IIORegistry registry = IIORegistry.getDefaultInstance();
        registry.registerServiceProvider(new DicomImageReaderSpi());
        registry.registerServiceProvider(new NativeJ2kImageReaderSpi());
        registry.registerServiceProvider(new NativeJLSImageReaderSpi());
        registry.registerServiceProvider(new NativeJPEGImageReaderSpi());
    }

    // opencv library error 시 library path 추가
    // windows: C:\Temp
    // linux: /tmp
    public static BufferedImage convert(InputStream is) throws IOException {
        ImageInputStream iis = ImageIO.createImageInputStream(is); 
        Iterator<ImageReader> iter = ImageIO.getImageReadersByFormatName("dicom");
        if (!iter.hasNext()) {
            throw new UnsupportedOperationException("no supported image reader: dicom");
        }

        DicomImageReader ir = (DicomImageReader) iter.next();
        ir.setInput(iis, false);

        DicomImageReadParam irp = (DicomImageReadParam) ir.getDefaultReadParam();

        // only read first image (first image 0)
        BufferedImage bi = ir.read(0, irp);
        ir.close();

        return bi;
    }
}
