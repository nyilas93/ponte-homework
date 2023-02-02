package hu.ponte.hr.services;

import hu.ponte.hr.controller.ImageMeta;
import hu.ponte.hr.domain.ImageEntity;
import hu.ponte.hr.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ImageStore {

    private final ImageRepository imageRepository;

    @Autowired
    public ImageStore(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public void storeFile(MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        ImageEntity image = new ImageEntity(fileName, file.getContentType(), file.getBytes(), "sign");
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
