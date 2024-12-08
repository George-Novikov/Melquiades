package com.georgen.melquiades.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import static java.nio.ByteBuffer.wrap;

public class BufferedReader implements AutoCloseable {
    private final byte[] buffer = new byte[1024];
    private final ByteBuffer bb = wrap(buffer);
    private final SeekableByteChannel channel;
    private final Charset charset = StandardCharsets.UTF_8;

    public BufferedReader(String path) throws IOException {
        this(Paths.get(path));
    }

    public BufferedReader(Path path) throws IOException {
        channel = Files.newByteChannel(path, StandardOpenOption.READ);
    }

    // This code buffers: First, our internal buffer is scanned
    // for a new line. If there is no full line in the buffer,
    // we read bytes from the file and check again until we find one.

    public String readLine(long position) throws IOException {
        channel.position(position);

        int len = 0;
        if (!channel.isOpen()) return null;

        int scanStart = 0;

        while (true) {
            // Scan through the bytes we have buffered for a newline.

            for (int i = scanStart; i < bb.position(); i++) {
                if (buffer[i] == '\n') {
                    // Found it. Take all bytes up to the new line, turn into a string.
                    String res = new String(buffer, 0, i, charset);

                    // Copy all bytes from _after_ the newline to the front.
                    System.arraycopy(buffer, i + 1, buffer, 0, bb.position() - i - 1);

                    // Adjust the position (which represents how many bytes are buffered).
                    bb.position(bb.position() - i - 1);
                    return res;
                }
            }
            scanStart = bb.position();

            // If we get here, the buffer is empty or contains no newline.

            if (scanStart == bb.limit()) {
                throw new IOException("Log line is too long");
            }

            int read = channel.read(bb); // let's fetch more bytes!

            if (read == -1) { // we've reached the end of the file.
                if (bb.position() == 0) return null;
                return new String(buffer, 0, bb.position(), charset);
            }
        }
    }

    @Override public void close() throws IOException {
        channel.close();
    }
}
