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
import java.util.ArrayList;
import java.util.List;

public class BufferReader implements AutoCloseable {

    public static int DEFAULT_BUFFER_SIZE = 1024;

    private final byte[] bytes;
    private final ByteBuffer buffer;
    private final SeekableByteChannel channel;
    private final Charset charset = StandardCharsets.UTF_8;

    public BufferReader(String path) throws IOException {
        this(path, DEFAULT_BUFFER_SIZE);
    }

    public BufferReader(String path, int bufferSize) throws IOException {
        this(Paths.get(path), bufferSize);
    }

    public BufferReader(Path path) throws IOException {
        this(path, DEFAULT_BUFFER_SIZE);
    }

    public BufferReader(Path path, int bufferSize) throws IOException {
        this.bytes = new byte[bufferSize];
        this.buffer = ByteBuffer.wrap(bytes);
        this.channel = Files.newByteChannel(path, StandardOpenOption.READ);
    }

    public long position() throws IOException {
        return channel.position();
    }

    public String readLine(long position) throws IOException {
        channel.position(position);
        if (!channel.isOpen()) return null;

        buffer.clear();
        int bytesRead = channel.read(buffer);
        if (bytesRead == -1) return null;
        buffer.flip();

        StringBuilder line = new StringBuilder();

        while (true) {
            int lineStart = buffer.position();
            boolean isNewlineFound = false;

            for (int i = buffer.position(); i < buffer.limit(); i++) {
                if (bytes[i] == '\n') {
                    isNewlineFound = true;
                    line.append(new String(bytes, lineStart, i - lineStart, charset));
                    channel.position(position + i + 1);
                    return line.toString();
                }
            }

            if (!isNewlineFound) {
                line.append(new String(bytes, lineStart, buffer.limit() - lineStart, charset));

                buffer.clear();
                bytesRead = channel.read(buffer);

                if (bytesRead == -1) {
                    return line.length() > 0 ? line.toString() : null;
                }
                buffer.flip();
            }
        }
    }

    public List<String> linesBetween(long start, long end) throws IOException {
        if (start >= end || !channel.isOpen()) return new ArrayList<>();

        List<String> lines = new ArrayList<>();
        channel.position(start);
        long currentPosition = start;
        StringBuilder currentLine = new StringBuilder();

        while (true) {
            buffer.clear();
            int bytesRead = channel.read(buffer);
            if (bytesRead == -1) break;
            buffer.flip();

            int lineStart = 0;
            for (int i = 0; i < buffer.limit(); i++) {
                if (bytes[i] == '\n') {
                    int lineLength = i - lineStart;
                    if (lineLength > 0) {
                        String line = new String(bytes, lineStart, lineLength, charset);
                        if (!line.trim().isEmpty()) {
                            currentLine.append(line);
                        }
                    }

                    if (currentLine.length() > 0) {
                        lines.add(currentLine.toString());
                        currentLine.setLength(0);
                    }

                    lineStart = i + 1;

                    // If we've passed the end position and reached a newline, we're done
                    if (currentPosition + i >= end) {
                        return lines;
                    }
                }
            }

            // Handle any remaining content in buffer
            if (lineStart < buffer.limit()) {
                String line = new String(bytes, lineStart, buffer.limit() - lineStart, charset);
                if (!line.trim().isEmpty()) {
                    currentLine.append(line);
                }
            }

            currentPosition += buffer.limit();
        }

        // Add any remaining content
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }

        return lines;
    }

    public long firstPosition(String condition) throws IOException {
        return firstPosition(0, condition);
    }

    public long firstPosition(long position, String condition) throws IOException {
        if (condition == null || condition.isEmpty()) return -1;

        byte[] pattern = condition.getBytes(charset);
        channel.position(position);
        if (!channel.isOpen()) return -1;

        long currentPosition = position;

        while (true) {
            buffer.clear();
            int bytesRead = channel.read(buffer);
            if (bytesRead == -1) return -1;
            buffer.flip();

            int lineStart = 0;
            for (int i = 0; i < buffer.limit(); i++) {
                if (bytes[i] == '\n') {
                    if (matchPattern(bytes, lineStart, i, pattern)) {
                        return currentPosition + lineStart;
                    }
                    lineStart = i + 1;
                }
            }

            // Check last line if it doesn't end with newline
            if (lineStart < buffer.limit() && matchPattern(bytes, lineStart, buffer.limit(), pattern)) {
                return currentPosition + lineStart;
            }

            currentPosition += buffer.limit();
        }
    }

    public long lastPosition(String condition) throws IOException {
        return lastPosition(0, condition);
    }

    public long lastPosition(long position, String condition) throws IOException {
        if (condition == null || condition.isEmpty()) return -1;

        byte[] pattern = condition.getBytes(charset);
        channel.position(position);
        if (!channel.isOpen()) return -1;

        long lastLineEndPosition = -1;
        long currentPosition = position;

        while (true) {
            buffer.clear();
            int bytesRead = channel.read(buffer);
            if (bytesRead == -1) break;
            buffer.flip();

            int lineStart = 0;
            for (int i = 0; i < buffer.limit(); i++) {
                if (bytes[i] == '\n') {
                    if (matchPattern(bytes, lineStart, i, pattern)) {
                        lastLineEndPosition = currentPosition + i + 1;
                    }
                    lineStart = i + 1;
                }
            }

            // Check last line if it doesn't end with newline
            if (lineStart < buffer.limit() && matchPattern(bytes, lineStart, buffer.limit(), pattern)) {
                lastLineEndPosition = currentPosition + buffer.limit();
            }

            currentPosition += buffer.limit();
        }

        return lastLineEndPosition;
    }

    private boolean matchPattern(byte[] data, int start, int end, byte[] pattern) {
        outer:
        for (int i = start; i <= end - pattern.length; i++) {
            for (int j = 0; j < pattern.length; j++) {
                if (data[i + j] != pattern[j]) {
                    continue outer;
                }
            }
            return true;
        }
        return false;
    }

    @Override public void close() throws IOException {
        if (channel.isOpen()) channel.close();
    }
}
