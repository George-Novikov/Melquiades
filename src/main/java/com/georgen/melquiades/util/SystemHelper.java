package com.georgen.melquiades.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class SystemHelper {

    public static boolean isUnixSystem(){
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("nix") || osName.contains("nux");
    }

    public static File createFile(Path path) throws IOException {
        Path parent = path.getParent();
        if (parent != null) Files.createDirectories(path.getParent());
        Files.createFile(path);

        File file = path.toFile();
        if (isUnixSystem()) setFilePermissions(file);
        return file;
    }

    public static void setFilePermissions(File file) throws IOException {
        Set<PosixFilePermission> permissions = Arrays
                .stream(PosixFilePermission.values())
                .filter(permission -> !permission.name().startsWith("OTHERS"))
                .collect(Collectors.toSet());

        Files.setPosixFilePermissions(file.toPath(), permissions);
    }

    public static String toSystemPath(String... parts){
        return String.join(File.separator, parts);
    }
}
