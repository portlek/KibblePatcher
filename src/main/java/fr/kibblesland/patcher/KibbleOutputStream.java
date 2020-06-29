package fr.kibblesland.patcher;

import java.io.IOException;
import java.io.OutputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.Deflater;
import java.util.zip.ZipOutputStream;

public class KibbleOutputStream extends JarOutputStream {

    public KibbleOutputStream(OutputStream out, Manifest man) throws IOException {
        super(out, man);
        super.setMethod(ZipOutputStream.DEFLATED);
        super.setLevel(9); // Max compression
        super.def.setStrategy(Deflater.NO_FLUSH);
    }

    public Deflater getDeflater() {
        return super.def;
    }
}
