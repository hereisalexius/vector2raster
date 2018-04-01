package com.hereisalexius.v2r;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class PDFFileConverter extends VectorFileConverter {

    public PDFFileConverter(File file) {
        super(file);
    }

    public Image getImage(int page) {
        Image result = null;
        try {
            PDDocument document = PDDocument.load(file.getAbsolutePath());
            List<PDPage> list = document.getDocumentCatalog().getAllPages();
            BufferedImage bufferedImage = list.get(page - 1).convertToImage(BufferedImage.TYPE_INT_RGB, 144);
            result = SwingFXUtils.toFXImage(bufferedImage, null);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Image getImage(int page, double scale, double dpi) {
        Image result = null;
        try {
            PDDocument document = PDDocument.load(file.getAbsolutePath());
            List<PDPage> list = document.getDocumentCatalog().getAllPages();
            BufferedImage bufferedImage = list.get(page - 1).convertToImage(BufferedImage.TYPE_INT_RGB, (int) (144 * scale));
            result = SwingFXUtils.toFXImage(bufferedImage, null);
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void export(File f, int page, double scale, double dpi) {
        try {
            PDDocument document = PDDocument.load(file.getAbsolutePath());
            List<PDPage> list = document.getDocumentCatalog().getAllPages();
            BufferedImage bufferedImage = list.get(page - 1).convertToImage(BufferedImage.TYPE_INT_RGB, (int) (144 * scale));
            document.close();
            ImageIO.write(bufferedImage, "jpg", f);
            JPEGUtils.setDpi(f,(int)dpi);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getPageCount() {
        int result = 1;
        PDDocument document = null;
        try {
            document = PDDocument.load(file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<PDPage> list = document.getDocumentCatalog().getAllPages();
        result = list.size();
        try {
            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}