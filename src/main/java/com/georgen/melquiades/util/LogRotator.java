package com.georgen.melquiades.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class LogRotator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static boolean isOldFile(String path) throws IOException {
        return isOldFile(Paths.get(path));
    }

    public static boolean isOldFile(Path path) throws IOException {
        if (!Files.exists(path)) {
            throw new IOException("File does not exist: " + path);
        }

        FileTime creationTime = Files.readAttributes(path, BasicFileAttributes.class).creationTime();
        LocalDate creationDate = LocalDate.from(
                creationTime.toInstant().atZone(ZoneId.systemDefault())
        );

        return !creationDate.equals(LocalDate.now());
    }

    public static void zipRotate(String path) throws IOException {
        zipRotate(Paths.get(path));
    }

    public static void zipRotate(Path path) throws IOException {
        if (!Files.exists(path)) throw new IOException("File does not exist: " + path);

        FileTime creationTime = Files.readAttributes(path, BasicFileAttributes.class).creationTime();
        LocalDate creationDate = LocalDate.from(
                creationTime.toInstant().atZone(ZoneId.systemDefault())
        );

        String originalFileName = path.getFileName().toString();
        String zipFileName = originalFileName + "." +
                creationDate.format(DATE_FORMATTER) + ".zip";
        Path zipPath = path.resolveSibling(zipFileName);

        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
            ZipEntry entry = new ZipEntry(originalFileName);
            zos.putNextEntry(entry);

            Files.copy(path, zos);
            zos.closeEntry();
        }

        // Verify zip file was created successfully
        if (!Files.exists(zipPath) || Files.size(zipPath) == 0) {
            throw new IOException("Failed to create zip archive: " + zipPath);
        }

        // Replace original file with empty file
        Files.write(path, new byte[0], StandardOpenOption.TRUNCATE_EXISTING);
    }
}
