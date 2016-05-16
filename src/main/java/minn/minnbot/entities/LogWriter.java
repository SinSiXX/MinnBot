package minn.minnbot.entities;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LogWriter extends Writer {

    private BufferedOutputStream bufferedOutputStream;
    private File f;

    public LogWriter() throws IOException {
        File folder = new File("Logs/Session");
        if (!folder.exists()) folder.mkdirs();
        f = new File(String.format("Logs/Session/SessionLog-%s.log", 22 << System.currentTimeMillis()));
        if (f.exists())
            f.delete();
        f.createNewFile();
        bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(f));
    }

    public LogWriter(File f) throws IOException {
        if (!f.exists())
            f.createNewFile();
        this.f = f;
        bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(f));
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        writeLn(String.copyValueOf(cbuf, off, len));
    }

    @Override
    public void flush() throws IOException {
        if (f.exists()) f.delete();
        f.createNewFile();
    }

    public void writeLn(String input) {
        try {
            bufferedOutputStream.write((input + "\n").getBytes());
        } catch (IOException ignore) {
        }
    }

    public void close() throws IOException {
        bufferedOutputStream.close();
        if (new String(Files.readAllBytes(Paths.get(f.getCanonicalPath()))).isEmpty()) f.delete();
    }

}
