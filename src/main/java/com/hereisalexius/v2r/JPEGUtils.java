package com.hereisalexius.v2r;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class JPEGUtils {

    public static void setDpi(File file, int value) throws IOException {

        BufferedImage bufferedImage = ImageIO.read(file);

        FileOutputStream fos = new FileOutputStream(file);

        JPEGImageEncoder jpegEncoder = JPEGCodec.createJPEGEncoder(fos);

        JPEGEncodeParam jpegEncodeParam = jpegEncoder.getDefaultJPEGEncodeParam(bufferedImage);
        jpegEncodeParam.setDensityUnit(JPEGEncodeParam.DENSITY_UNIT_DOTS_INCH);
        jpegEncodeParam.setXDensity(value);
        jpegEncodeParam.setYDensity(value);
        jpegEncoder.encode(bufferedImage, jpegEncodeParam);
        fos.close();

    }
}
