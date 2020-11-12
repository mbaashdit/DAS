package com.example.districtautomationsystem.Util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.logging.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
/**
 * Created by Manabendu on 29/08/20
 */
public class SslUtils {


    public static SSLContext getSslContextForCertificateFile(String fileName) {
        try {
            KeyStore keyStore = SslUtils.getKeyStore(fileName);
            SSLContext sslContext = SSLContext.getInstance("SSL");
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            sslContext.init(null, trustManagerFactory.getTrustManagers(), new SecureRandom());
            return sslContext;
        } catch (Exception e) {
            String msg = "Cannot load certificate from file";
            throw new RuntimeException(msg);
        }
    }

    private static KeyStore getKeyStore(String fileName) {
        KeyStore keyStore = null;
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream inputStream = new FileInputStream(fileName);
            Certificate ca;
            try {
                ca = cf.generateCertificate(inputStream);
            } finally {
                inputStream.close();
            }

            String keyStoreType = KeyStore.getDefaultType();
            keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);
        } catch (Exception e) {
        }
        return keyStore;
    }
}
