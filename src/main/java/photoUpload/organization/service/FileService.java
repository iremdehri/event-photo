package photoUpload.organization.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileService {
    private final String uploadDir="uploads/events/";

    public String saveFile(MultipartFile file){
        try{
            Path copyLocation= Paths.get(uploadDir);
            if(!Files.exists(copyLocation)){
                Files.createDirectories(copyLocation);
            }

            String fileName= UUID.randomUUID().toString()+"_"+file.getOriginalFilename();
            Path targetPath=copyLocation.resolve(fileName);
            Files.copy(file.getInputStream(),targetPath, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        }catch (Exception e){
            throw new RuntimeException("Dosya kaydedilemedi: "+e.getMessage());
        }
    }
}
