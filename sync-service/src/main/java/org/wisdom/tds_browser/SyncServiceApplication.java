package org.wisdom.tds_browser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.tdf.crypto.CryptoHelpers;
import org.tdf.crypto.sm2.SM2;
import org.tdf.crypto.sm2.SM2PrivateKey;
import org.tdf.crypto.sm2.SM2PublicKey;
import org.tdf.gmhelper.SM2Util;
import org.tdf.gmhelper.SM3Util;
import org.tdf.sunflower.types.CryptoContext;

@SpringBootApplication
@EnableScheduling
@EnableEurekaClient
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class SyncServiceApplication {


    public static void main(String[] args) {
        initTdsSDK();
        SpringApplication.run(SyncServiceApplication.class, args);
    }


    private static void initTdsSDK(){
        CryptoContext.setHashFunction(SM3Util::hash);
        CryptoContext.setSignatureVerifier((pk, msg, sig) -> new SM2PublicKey(pk).verify(msg, sig));
        CryptoContext.setSigner((sk, msg) -> new SM2PrivateKey(sk).sign(msg));
        CryptoContext.setSecretKeyGenerator(() -> SM2.generateKeyPair().getPrivateKey().getEncoded());
        CryptoContext.setGetPkFromSk((sk) -> new SM2PrivateKey(sk).generatePublicKey().getEncoded());
        CryptoContext.setEcdh((initiator, sk, pk) -> SM2.calculateShareKey(initiator, sk, sk, pk, pk, SM2Util.WITH_ID));
        CryptoContext.setPublicKeySize(CryptoContext.getPkFromSk(CryptoContext.generateSecretKey()).length);
        CryptoContext.setEncrypt(CryptoHelpers.ENCRYPT);
        CryptoContext.setDecrypt(CryptoHelpers.DECRYPT);
    }

    @Bean
    public AuditorAware<String> auditorAware() {
        return new JpaAuditorAware();
    }

}
