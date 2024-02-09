package projects.javadiplom.service.interfaces;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface FilesService {
    public void createDir(String newUser);

    public void newFile(String dir, MultipartFile file);

    public Resource loadFile(String dir, String fileName);

    public void renameFile(String dir, String fileName, String newFileName);

    public void deleteFile(String dir, String fileName);


    public String list(String dir);
}
