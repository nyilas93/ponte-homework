package hu.ponte.hr.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class SignService {

    private static final String PRIVATE_KEY_PATH = "src/main/resources/config/keys/key.private";
    private final PrivateKey privateKey;
    private final Signature signature;

    @Autowired
    public SignService() throws Exception {
        this.privateKey = loadPrivateKey();
        this.signature = Signature.getInstance("SHA256withRSA");
    }

    public Map<String, Object> signFile(MultipartFile file) throws Exception {
        byte[] fileBytes = file.getBytes();
        signature.initSign(privateKey);
        signature.update(fileBytes);
        byte[] digitalSign = signature.sign();
        String sign = Base64.getEncoder().encodeToString(digitalSign);
        log.info("File signing was successful. Sign: {}", sign);
        Map<String, Object> signedFileWithSignature = new HashMap<>();
        signedFileWithSignature.put("fileBytes", fileBytes);
        signedFileWithSignature.put("sign", sign);
        return signedFileWithSignature;
    }

    private PrivateKey loadPrivateKey() throws Exception {
        try (FileInputStream inputStream = new FileInputStream(PRIVATE_KEY_PATH)) {
            byte[] keyBytes = inputStream.readAllBytes();
            EncodedKeySpec encodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(encodedKeySpec);
        }
    }
}
