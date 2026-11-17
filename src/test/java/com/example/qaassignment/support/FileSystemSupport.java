package com.example.qaassignment.support;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;


public class FileSystemSupport {

    private FileSystemSupport(){

    }

    public static void createDirectories(List<Path> folders) throws IOException {
        for(Path folder : folders){
            Files.createDirectories(folder);
        }
    }

    public static void emptyDirectory(Path folder) throws IOException{
        if(Files.notExists(folder)){
            return;
        }

        try(var paths = Files.walk(folder)){
            for(Path path : paths
                    .sorted(Comparator.reverseOrder())
                    .filter(candidate -> !candidate.equals(folder))
                    .toList()) {
                Files.deleteIfExists(path);
            }
        }
    }

    public static List<Path> regularFiles(Path folder) throws IOException{
        try (var files = Files.list(folder)){
            return files.filter(Files::isRegularFile)
                    .toList();
        }
    }

}
