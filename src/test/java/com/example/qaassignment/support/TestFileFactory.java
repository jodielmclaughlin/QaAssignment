package com.example.qaassignment.support;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class TestFileFactory {

    private TestFileFactory() {
    }

    public record ArchiveEntry(String fileName, String type){

    }

    public static Path createZip(Path inputFolder, String zipFileName, List<ArchiveEntry> entries) throws IOException {
        Files.createDirectories(inputFolder);
        Path zipPath = inputFolder.resolve(zipFileName);

        try(ZipOutputStream output = new ZipOutputStream(Files.newOutputStream(zipPath))){
            for(ArchiveEntry entry:entries){
                output.putNextEntry(new ZipEntry(entry.fileName()));
                output.write(contentFor(entry.type()));
                output.closeEntry();
            }
        }
        return zipPath;
    }

    private static byte[] contentFor(String type){
        return switch(type.toUpperCase(Locale.ROOT)){
            case "PDF" -> "%PDF-1.4\n% test PDF\n%%EOF"
                    .getBytes(StandardCharsets.ISO_8859_1);
            case"JPEG" -> new byte[]{
                    (byte) 0xFF, (byte) 0xD8, (byte) 0xFF,
                    (byte) 0xE0, 0x00, 0x10,
                    0x4A, 0x46, 0x49, 0x46,
                    0x00, 0x01,
                    (byte) 0xFF, (byte) 0xD9
            };
            case "TEXT" -> "Unsupported text"
                    .getBytes(StandardCharsets.UTF_8);
            case "JSON" -> "{\"unsupported\":true}"
                    .getBytes(StandardCharsets.UTF_8);
            case "XLSX" -> "Unsupported spreadsheet"
                    .getBytes(StandardCharsets.UTF_8);

            case "EXE" -> new byte[]{0x4D, 0x5A, 0x00, 0x00};

            case "ZIP" -> new byte[]{0x50, 0x4B, 0x03, 0x04};

            default -> throw new IllegalArgumentException(
                    "Unknown fixture type: " + type
            );
        };
    }
}
