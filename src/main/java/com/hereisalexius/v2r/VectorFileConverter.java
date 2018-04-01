package com.hereisalexius.v2r;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import org.apache.batik.transcoder.TranscoderException;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class VectorFileConverter {

    protected File file;

    public VectorFileConverter(File file) {
        this.file = file;
    }

    public abstract Image getImage(int page) throws IOException;

    public abstract Image getImage(int page,double scale, double dpi) throws IOException;

    public abstract void export(File f, int page,double scale, double dpi);

    public int getPageCount(){
        return 1;
    }
}
