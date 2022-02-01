package sep.firstbank.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.AttributeConverter;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Base64;

@Component
public class SensitiveDataConverter implements AttributeConverter<String, String> {

    private static final String AES = "AES";
    @Value("${encryption.keystore-name}")
    private String keystoreName;
    @Value("${encryption.keystore-password}")
    private String keystorePassword;
    @Value("${encryption.keystore-entry-name}")
    private String entryName;
    @Value("${encryption.keystore-entry-password}")
    private String entryPassword;

    private Key key;
    private Cipher cipher;

    @PostConstruct
    public void Initialize() throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, KeyStoreException, CertificateException, UnrecoverableKeyException {
        FileInputStream fis = new FileInputStream(keystoreName);
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(fis, keystorePassword.toCharArray());
        key = ks.getKey(entryName, entryPassword.toCharArray());
        key = new SecretKeySpec(key.getEncoded(), AES);
        cipher = Cipher.getInstance(AES);
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return Base64.getEncoder().encodeToString(cipher.doFinal(attribute.getBytes()));
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Base64.getDecoder().decode(dbData)));
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            throw new IllegalStateException(e);
        }
    }
}
