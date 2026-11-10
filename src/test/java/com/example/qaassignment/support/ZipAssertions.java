package com.example.qaassignment.support;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.*;

public final class ZipAssertions {
    private ZipAssertions(){

    }

    public static List<String> entryNames(Path zipPath) throws IOException{
        try(
            ZipInputStream input =
                    new ZipInputStream(Files.newInputStream(zipPath))) {
            return readEntryNames(input);
            }
        }


    public static byte[] decodeBase64(Path encodedFile) throws IOException{
        String encoded = Files.readString(encodedFile).trim();
        return Base64.getDecoder().decode(encoded);
    }

    public static void assertValidZip(byte[] zipBytes) throws IOException{
        assertNotNull(zipBytes, "Decoded ZIP content should not be null");
        assertTrue(zipBytes.length > 0, "Decoded ZIP content should not be empty");

        try(ZipInputStream input = new ZipInputStream(new ByteArrayInputStream(zipBytes))) {
            assertNotNull(input.getNextEntry(), "Decoded ZIP should contain at least on entry");
        }
    }


    private static List<String> readEntryNames(ZipInputStream input) throws IOException{
        List<String> names = new ArrayList<>();
        ZipEntry entry;

        while((entry = input.getNextEntry()) != null){
            if(!entry.isDirectory()) {
                names.add(entry.getName());
            }
            input.closeEntry();
        }
        return names;
    }
}

