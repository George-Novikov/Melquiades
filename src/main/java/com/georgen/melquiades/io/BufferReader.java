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

import static java.nio.ByteBuffer.wrap;

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
        this.buffer = wrap(bytes);
        this.channel = Files.newByteChannel(path, StandardOpenOption.READ);
    }

    public long position() throws IOException {
        return channel.position();
    }

    public String readLine(long position) throws IOException {
        channel.position(position);
        if (!channel.isOpen()) return null;

        while (true) {
            for (int i = 0; i < buffer.position(); i++) {
                if (bytes[i] == '\n') {
                    return constructString(bytes, i);
                }
            }
            if (buffer.position() == buffer.limit()) throw new IOException("Line is too long");

            if (channel.read(buffer) == -1) {
                if (buffer.position() == 0) return null;
                return new String(bytes, 0, buffer.position(), charset);
            }
        }
    }

    public String constructString(byte[] bytes, int i) {
        // Check if there's anything to copy first
        if (buffer.position() > i + 1) {
            System.arraycopy(bytes, i + 1, bytes, 0, buffer.position() - i - 1);
            buffer.position(buffer.position() - i - 1);
        } else {
            buffer.position(0); // Reset position if no data to copy
        }
        return new String(bytes, 0, i, charset);
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

    public long findFirstPosition(long position, String condition) throws IOException {
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

    public long findLastPosition(long position, String condition) throws IOException {
        if (condition == null || condition.isEmpty()) return -1;

        byte[] pattern = condition.getBytes(charset);
        channel.position(position);
        if (!channel.isOpen()) return -1;

        long lastMatchPosition = -1;
        long lastLineEndPosition = -1;  // Add this to track the end of the last matching line
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
                        lastMatchPosition = currentPosition + lineStart;
                        lastLineEndPosition = currentPosition + i + 1;
                    }
                    lineStart = i + 1;
                }
            }

            // Check last line if it doesn't end with newline
            if (lineStart < buffer.limit() && matchPattern(bytes, lineStart, buffer.limit(), pattern)) {
                lastMatchPosition = currentPosition + lineStart;
                lastLineEndPosition = currentPosition + buffer.limit();
            }

            currentPosition += buffer.limit();
        }

        return lastLineEndPosition;  // Return the position after the end of the last matching line
    }


    public String findFirst(long position, String condition) throws IOException {
        if (condition == null || condition.isEmpty()) return readLine(position);

        byte[] pattern = condition.getBytes(charset);
        channel.position(position);
        if (!channel.isOpen()) return null;

        buffer.clear();
        int bytesRead = channel.read(buffer);
        if (bytesRead == -1) return null;
        buffer.flip();

        while (true) {
            int lineStart = buffer.position();
            boolean foundLine = false;

            // Look for newline in current buffer
            for (int i = buffer.position(); i < buffer.limit(); i++) {
                if (bytes[i] == '\n') {
                    foundLine = true;
                    // Check if current line matches pattern
                    if (matchPattern(bytes, lineStart, i, pattern)) {
                        String result = new String(bytes, lineStart, i - lineStart, charset);
                        channel.position(position + i + 1);
                        return result;
                    }
                    lineStart = i + 1;
                    buffer.position(lineStart);
                }
            }

            // If we haven't found any newlines and buffer is full, we need more data
            if (!foundLine) {
                if (buffer.hasRemaining()) {
                    buffer.compact();
                    bytesRead = channel.read(buffer);
                    buffer.flip();
                    if (bytesRead == -1) {
                        // Check final line if any
                        if (buffer.hasRemaining() &&
                                matchPattern(bytes, buffer.position(), buffer.limit(), pattern)) {
                            return new String(bytes, buffer.position(), buffer.limit() - buffer.position(), charset);
                        }
                        return null;
                    }
                } else {
                    // Buffer is full but no newline found
                    buffer.clear();
                    bytesRead = channel.read(buffer);
                    if (bytesRead == -1) return null;
                    buffer.flip();
                }
            }
        }
    }

    public String findLast(long position, String condition) throws IOException {
        if (condition == null || condition.isEmpty()) return readLine(position);

        byte[] pattern = condition.getBytes(charset);
        channel.position(position);
        if (!channel.isOpen()) return null;

        // Store the byte range and buffer content of the last match
        int matchStart = -1;
        int matchEnd = -1;
        byte[] matchBuffer = null;
        long matchPosition = -1;

        buffer.clear();
        int bytesRead = channel.read(buffer);
        if (bytesRead == -1) return null;
        buffer.flip();

        long currentPosition = position;

        while (true) {
            int lineStart = buffer.position();
            boolean foundLine = false;

            // Look for newline in current buffer
            for (int i = buffer.position(); i < buffer.limit(); i++) {
                if (bytes[i] == '\n') {
                    foundLine = true;
                    // Check if current line matches pattern
                    if (matchPattern(bytes, lineStart, i, pattern)) {
                        // Save match information without creating String
                        matchStart = lineStart;
                        matchEnd = i;
                        matchBuffer = new byte[i - lineStart];
                        System.arraycopy(bytes, lineStart, matchBuffer, 0, i - lineStart);
                        matchPosition = currentPosition + i + 1;
                    }
                    lineStart = i + 1;
                    buffer.position(lineStart);
                }
            }

            currentPosition += buffer.position();

            if (!foundLine) {
                if (buffer.hasRemaining()) {
                    buffer.compact();
                    bytesRead = channel.read(buffer);
                    buffer.flip();
                    if (bytesRead == -1) {
                        // Check final line if any
                        if (buffer.hasRemaining() &&
                                matchPattern(bytes, buffer.position(), buffer.limit(), pattern)) {
                            int len = buffer.limit() - buffer.position();
                            matchBuffer = new byte[len];
                            System.arraycopy(bytes, buffer.position(), matchBuffer, 0, len);
                            matchPosition = currentPosition + buffer.limit();
                        }
                        break;
                    }
                } else {
                    buffer.clear();
                    bytesRead = channel.read(buffer);
                    if (bytesRead == -1) break;
                    buffer.flip();
                }
            }
        }

        // Only create one String object at the end if we found a match
        if (matchBuffer != null) {
            channel.position(matchPosition);
            return new String(matchBuffer, charset);
        }
        return null;
    }

    private boolean matchPattern(byte[] data, int start, int end, byte[] pattern) {
        // Simple byte array search
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
