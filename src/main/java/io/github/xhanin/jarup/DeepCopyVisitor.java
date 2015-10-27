package io.github.xhanin.jarup;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

class DeepCopyVisitor implements FileVisitor<Path> {

    private final Path archiveRoot;
    private final Path source;
    private final Path target;

    public DeepCopyVisitor(Path archiveRoot, Path source, Path target) {
        this.archiveRoot = archiveRoot;
        this.source = source;
        this.target = target;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        copy(dir, resolve(dir), source.equals(dir));
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        copy(file, resolve(file), false);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
        throw exc;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        return FileVisitResult.CONTINUE;
    }

    private Path resolve(Path path) {
        return target.resolve(source.relativize(path));
    }

    private void copy(Path dir, Path to, boolean skipEntry) throws IOException {
        boolean exists = to.toFile().exists();
        Files.copy(dir, to, StandardCopyOption.REPLACE_EXISTING);
        if (!exists && !skipEntry) {
            Entries.addEntry(archiveRoot, Entries.entryName(archiveRoot, to));
        }
    }
}
