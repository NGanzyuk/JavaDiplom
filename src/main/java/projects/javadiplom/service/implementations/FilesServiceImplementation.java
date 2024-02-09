package projects.javadiplom.service.implementations;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import projects.javadiplom.service.interfaces.FilesService;

import javax.persistence.Lob;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FilesServiceImplementation implements FilesService {
    String homeDir = "homeDir";

    @Override
    public void createDir(String newUser) {
        try {
            File newDir = new File(homeDir, newUser);
            if (!newDir.exists()) {
                newDir.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    @Lob
    public void newFile(String dir, MultipartFile file) {
        try {
            Path dirPath = Paths.get(homeDir + "/" + dir);
            Files.copy(file.getInputStream(), dirPath.resolve(file.getOriginalFilename()));
        } catch (Exception e) {
            if (e instanceof FileAlreadyExistsException) {
                throw new RuntimeException("Файл с таким именем уже существует");
            }
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Resource loadFile(String dir, String fileName) {
        try {
            Path dirPath = Paths.get(homeDir + "/" + dir);
            Path file = dirPath.resolve(fileName).normalize();
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Невозможно прочитать файл");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Ошибка: " + e.getMessage());
        }
    }

    @Override
    public void renameFile(String dir, String fileName, String newFileName) {
        try {
            Path dirPath = Paths.get(homeDir + "/" + dir);
            Path fileToMovePath = Paths.get(dirPath + "/" + fileName);
            Path targetPath = Paths.get(dirPath + "/" + newFileName);
            Files.move(fileToMovePath, targetPath);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteFile(String dir, String fileName) {
        try {
            Path dirPath = Paths.get(homeDir + "/" + dir);
            Path file = dirPath.resolve(fileName);
            Files.delete(file);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public String list(String dir) {
        Path dirPath = Paths.get(homeDir + "/" + dir);
        StringBuilder body = new StringBuilder();

        body.append("[");
        if (dirPath.toFile().exists() && dirPath.toFile().isDirectory()) {

            String delim = "";
            for (File item : dirPath.toFile().listFiles()) {
                body.append(delim);
                body.append("{");
                body.append("\"filename\"" + ":" + "\"" + item.getName() + "\",");
                body.append("\"size\"" + ":" + "\"" + item.length() + "\"");
                body.append("}");
                delim = ",";
            }
        }
        body.append("]");
        return body.toString();
    }
}
