package hu.ponte.hr.controller.upload;

import hu.ponte.hr.services.ImageStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Component
@RequestMapping("api/file")
public class UploadController {

    private final ImageStore imageStore;

    @Autowired
    public UploadController(ImageStore imageStore) {
        this.imageStore = imageStore;
    }

    @RequestMapping(value = "post", method = RequestMethod.POST)
    @ResponseBody
    public String handleFormUpload(@RequestParam("file") MultipartFile file) throws IOException {
        log.info("Handle file upload request: {}", file.getOriginalFilename());
        imageStore.storeFile(file);
        log.info("Successful file upload: {}", file.getOriginalFilename());
        return "Successful file upload: " + file.getOriginalFilename();
    }
}
