package io.github.xhanin.jarup;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.newBufferedReader;
import static java.nio.file.Files.newBufferedWriter;

class Entries {

    public static void addEntry(Path archiveRoot, String entryName) throws IOException {
        try (BufferedWriter entries = newBufferedWriter(entriesPath(archiveRoot),
                UTF_8, StandardOpenOption.APPEND)) {
            entries.write(entryName(archiveRoot.resolve(entryName), entryName));
            entries.newLine();
        }
    }

    public static void removeEntry(Path archiveRoot, String entryName) throws IOException {
        Path path = entriesPath(archiveRoot);
        File temp = Files.createTempFile(archiveRoot, "temp", "jarup").toFile();
        try (BufferedReader reader = newBufferedReader(path, UTF_8);
             BufferedWriter writer = newBufferedWriter(temp.toPath(), UTF_8)) {

            String entryLine = entryName(archiveRoot.resolve(entryName), entryName);
            String currentLine;
            while ((currentLine = reader.readLine()) != null) {
                String current = currentLine.trim();
                if (current.equals(entryLine)) {
                    continue;
                }
                writer.write(current);
                writer.newLine();
            }
        }

        File target = path.toFile();
        if (!temp.renameTo(target)) {
            throw new FileSystemException(
                String.format("Could not copy temporary entries files <%s> to <%s>", temp.getPath(), target.getPath())
            );
        }
    }

    public static String entryName(Path root, Path path) {
        String name = root.relativize(path).toString();
        return entryName(path, name);
    }

    public static String entryName(Path path, String name) {
        name = name.replace(File.separatorChar, '/');
        if (Files.isDirectory(path)) {
            name = name.endsWith("/") ? name : (name + File.separator);
        }
        return name;
    }

    public static String entryName(String filePath) {
        int index = filePath.indexOf(":/");
        if (index == -1) {
            return filePath;
        }
        return filePath.substring(index + 2);
    }

    public static Path entriesPath(Path root) {
        return root.resolve("___jarup___entries");
    }
}
