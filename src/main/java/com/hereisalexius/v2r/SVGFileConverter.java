package com.hereisalexius.v2r;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.JPEGTranscoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;

public class SVGFileConverter extends VectorFileConverter {

    public SVGFileConverter(File file) {
        super(file);
    }

    public Image getImage(int page) {
        Image result = null;
        File tempFile = new File("tempOriginal.jpg");
        try {
            String svgURL = file.toURI().toURL().toString();
            TranscoderInput inputT = new TranscoderInput(svgURL);
            OutputStream ostream = new FileOutputStream(tempFile);
            TranscoderOutput output = new TranscoderOutput(ostream);
            JPEGTranscoder converter = new JPEGTranscoder();
            converter.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, new Float(.9));
            converter.transcode(inputT, output);
            ostream.flush();
            ostream.close();
            BufferedImage bufferedImage = ImageIO.read(tempFile);
            result = SwingFXUtils.toFXImage(bufferedImage, null);
            Files.delete(tempFile.toPath());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return result;
    }

    public Image getImage(int page, double scale, double dpi) {
        Image result = null;
        File tempFile = new File("temp.jpg");
        try {
            export(tempFile,page,scale,dpi);
            BufferedImage bufferedImage = ImageIO.read(tempFile);
            result = SwingFXUtils.toFXImage(bufferedImage, null);
            Files.delete(tempFile.toPath());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return result;
    }

    @Override
    public void export(File f, int page, double scale, double dpi){
        try {
            Image original = getImage(1);
            String svgURL = file.toURI().toURL().toString();
            TranscoderInput inputT = new TranscoderInput(svgURL);
            OutputStream ostream = new FileOutputStream(f);
            TranscoderOutput output = new TranscoderOutput(ostream);
            JPEGTranscoder converter = new JPEGTranscoder();
            converter.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, new Float(.9));
            converter.addTranscodingHint(JPEGTranscoder.KEY_WIDTH, new Float(original.getWidth() * scale));
            converter.addTranscodingHint(JPEGTranscoder.KEY_HEIGHT, new Float(original.getHeight() * scale));
            converter.transcode(inputT, output);
            ostream.flush();
            ostream.close();
            JPEGUtils.setDpi(f, (int) dpi);
        } catch (TranscoderException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
