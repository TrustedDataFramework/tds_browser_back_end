package org.wisdom.tds_browser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.ClassUtils;
import org.tdf.crypto.CryptoHelpers;
import org.tdf.crypto.ed25519.Ed25519;
import org.tdf.crypto.ed25519.Ed25519PrivateKey;
import org.tdf.crypto.ed25519.Ed25519PublicKey;
import org.tdf.crypto.sm2.SM2;
import org.tdf.crypto.sm2.SM2PrivateKey;
import org.tdf.crypto.sm2.SM2PublicKey;
import org.tdf.gmhelper.SM2Util;
import org.tdf.gmhelper.SM3Util;
import org.tdf.sunflower.types.CryptoContext;
import org.tdf.sunflower.util.FileUtils;

@SpringBootApplication
@EnableScheduling
@EnableEurekaClient
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class SyncServiceApplication {

    public static final byte[] TRIVIAL_KEY = new byte[]{
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 0,
            0, 0, 0, 1
    };


    public static void main(String[] args) {
        FileUtils.setClassLoader(ClassUtils.getDefaultClassLoader());
        SpringApplication app = new SpringApplication(SyncServiceApplication.class);
        app.addInitializers(applicationContext -> {
            loadCryptoContext();
        });
        app.run(args);
    }


    public static void loadCryptoContext() {
        switch (System.getenv("CRYPTO_HASH").toLowerCase()) {
            case "sm3":
                CryptoContext.setHashFunction(SM3Util::hash);
                break;
            case "keccak256":
            case "keccak-256":
                CryptoContext.setHashFunction(CryptoHelpers::keccak256);
                break;
            case "keccak512":
            case "keccak-512":
                CryptoContext.setHashFunction(CryptoHelpers::keccak512);
                break;
            case "sha3256":
            case "sha3-256":
                CryptoContext.setHashFunction(CryptoHelpers::sha3256);
                break;
            default:
        }
        switch (System.getenv("CRYPTO_EC").toLowerCase()) {
            case "ed25519":
                CryptoContext.setSignatureVerifier((pk, msg, sig) -> new Ed25519PublicKey(pk).verify(msg, sig));
                CryptoContext.setSigner((sk, msg) -> new Ed25519PrivateKey(sk).sign(msg));
                CryptoContext.setSecretKeyGenerator(() -> Ed25519.generateKeyPair().getPrivateKey().getEncoded());
                CryptoContext.setGetPkFromSk((sk) -> new Ed25519PrivateKey(sk).generatePublicKey().getEncoded());
                // TODO add ed25519 ecdh
                CryptoContext.setEcdh((i, sk, pk) -> TRIVIAL_KEY);
                break;
            case "sm2":
                CryptoContext.setSignatureVerifier((pk, msg, sig) -> new SM2PublicKey(pk).verify(msg, sig));
                CryptoContext.setSigner((sk, msg) -> new SM2PrivateKey(sk).sign(msg));
                CryptoContext.setSecretKeyGenerator(() -> SM2.generateKeyPair().getPrivateKey().getEncoded());
                CryptoContext.setGetPkFromSk((sk) -> new SM2PrivateKey(sk).generatePublicKey().getEncoded());
                CryptoContext.setEcdh((initiator, sk, pk) -> SM2.calculateShareKey(initiator, sk, sk, pk, pk, SM2Util.WITH_ID));
                break;
            default:
        }
        CryptoContext.setPublicKeySize(CryptoContext.getPkFromSk(CryptoContext.generateSecretKey()).length);
        CryptoContext.setEncrypt(CryptoHelpers.ENCRYPT);
        CryptoContext.setDecrypt(CryptoHelpers.DECRYPT);
    }

    @Bean
    public AuditorAware<String> auditorAware() {
        return new JpaAuditorAware();
    }

}
