package hu.ponte.hr.services;

import hu.ponte.hr.controller.ImageMeta;
import hu.ponte.hr.domain.ImageEntity;
import hu.ponte.hr.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ImageStore {

    private final ImageRepository imageRepository;
    private final SignService signService;

    @Autowired
    public ImageStore(ImageRepository imageRepository, SignService signService) {
        this.imageRepository = imageRepository;
        this.signService = signService;
    }

    public void storeFile(MultipartFile file) throws Exception {
        Map<String, Object> signedFileWithSignature = signService.signFile(file);
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        ImageEntity image = new ImageEntity(
                fileName,
                file.getContentType(),
                (byte[]) signedFileWithSignature.get("fileBytes"),
                (String) signedFileWithSignature.get("sign"));
        imageRepository.save(image);
    }

    public List<ImageMeta> getAllImage() {
        return imageRepository.findAll().stream()
                .map(imageEntity -> ImageMeta.builder()
                        .id(imageEntity.getId())
                        .name(imageEntity.getName())
                        .mimeType(imageEntity.getType())
                        .size(imageEntity.getData().length)
                        .digitalSign(imageEntity.getSign())
                        .build())
                .collect(Collectors.toList());
    }

    public byte[] getImageData(String id) {
        ImageEntity imageEntity = imageRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return imageEntity.getData();
    }
}
