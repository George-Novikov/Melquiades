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
import java.util.stream.IntStream;

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

    public String constructString(byte[] bytes, int i){
        String result = new String(bytes, 0, i, charset);
        System.out.println("Index is: " + i + " and buffer position is " + buffer.position());
        System.arraycopy(bytes, i + 1, bytes, 0, buffer.position() - i - 1);
        buffer.position(buffer.position() - i - 1);
        return result;
    }

    public int[] allIndicesOf(ByteBuffer buf, byte[] b) {
        if (b.length == 0) {
            return new int[0];
        }
        return IntStream.rangeClosed(buf.position(), buf.limit() - b.length)
                .filter(i -> IntStream.range(0, b.length).allMatch(j -> buf.get(i + j) == b[j]))
                .toArray();
    }

    public boolean hasSubArray(ByteBuffer buf, byte[] b) {
        if (b.length == 0) return false;

        return IntStream.rangeClosed(buf.position(), buf.limit() - b.length)
                .filter(i -> IntStream.range(0, b.length).allMatch(j -> buf.get(i + j) == b[j]))
                .toArray().length > 0;
    }

    public boolean isStartingWith(byte[] bytes, int start, char[] chars){
        for (int i = start; i < chars.length; i++) {
            if (bytes[i] != chars[i]) return false;
        }
        return true;
    }

    private boolean contains(byte[] buffer, char[] chars) {
        if (buffer.length == 0 || chars.length == 0) return false;

        int scanStart = 0;

        for (int i = 0; i < chars.length; i++) {
            if (buffer[i] == chars[i]) scanStart = i;
        }

        for (int x = scanStart; x < chars.length; x++){
            if (buffer[x] != chars[x]) return false;
        }

        return true;
    }

    public List<String> readAll(int offset) throws IOException {
        channel.position(offset);
        List<String> lines = new ArrayList<>();

        while (channel.read(buffer) > 0) {
            buffer.flip();
            String line = new String(buffer.array());
            lines.add(line);
            if (line.contains(System.lineSeparator())){
                channel.position(channel.position() + line.indexOf(System.lineSeparator()));
            }
//            channel.position(channel.position() + line.length());
            buffer.clear();
        }
        return lines;
    }

    public long findPosition(int offset) throws IOException {
        channel.position(offset);

        while (channel.read(buffer) > 0) {
            buffer.flip();
            System.out.println(new String(buffer.array()));
            System.out.println(buffer.position());
            buffer.clear();
        }
        return 0;
    }

    public long findPosition(String condition) throws IOException {
        return findPosition(condition, 0);
    }

    public long findPosition(String condition, int offset) throws IOException {
        char[] chars = condition.toCharArray();
        channel.position(offset);

        while (channel.read(buffer) > 0) {
            buffer.flip();
            if (contains(bytes, chars)){
                System.out.println(new String(buffer.array()));
                System.out.println(buffer.position());
            }
            buffer.clear();
        }
        return 0;
    }

    @Override public void close() throws IOException {
        if (channel.isOpen()) channel.close();
    }
}
