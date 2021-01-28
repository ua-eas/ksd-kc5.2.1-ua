package edu.arizona.kra.irb.pdf.sftp;

import net.schmizz.sshj.xfer.InMemorySourceFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;


public class ProtocolPdfFile extends InMemorySourceFile {
    private final String filename;
    private final long length;
    private final InputStream inputStream;


    public ProtocolPdfFile(String filename, byte[] bytes) {
        this.filename = filename;
        this.length = bytes.length;
        this.inputStream = new ByteArrayInputStream(bytes);
    }


    @Override
    public String getName() {
        return filename;
    }


    @Override
    public long getLength() {
        return length;
    }


    @Override
    public InputStream getInputStream() throws IOException {
        return inputStream;
    }
}
