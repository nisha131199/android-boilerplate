package com.example.baseproject.utils;

import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import okhttp3.OkHttpClient;

public final class HttpUtils {
    private static SSLContext sslContext;
    private static HostnameVerifier hostnameVerifier;
    private static HttpUtils httpUtils;
    private TrustManager[] trustAllCerts;

    public HttpUtils() {
        setUpINSECURESSLContext(true);
    }

    public static HttpUtils getInstance() {
        if (httpUtils == null) {
            httpUtils = new HttpUtils();
        }
        return httpUtils;
    }

    public static SSLContext getSslContext() {
        return sslContext;
    }

    public static HostnameVerifier getHostnameVerifier() {
        return hostnameVerifier;
    }

    private void setUpINSECURESSLContext(boolean trustAllCert) {
        if (trustAllCert) {
            try {
                hostnameVerifier = (hostname, session) -> true;

                // Create a trust manager that does not validate certificate chains
                trustAllCerts = new TrustManager[]{new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(
                            X509Certificate[] chain,
                            String authType) {
                    }

                    @Override
                    public void checkServerTrusted(
                            X509Certificate[] chain,
                            String authType) {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }};

                // Install the all-trusting trust manager
                sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trustAllCerts,
                        new java.security.SecureRandom());
            } catch (KeyManagementException | NoSuchAlgorithmException e) {
                Log.e("TAG", "setUpINSECURESSLContext: " + e.getLocalizedMessage());
            }
        }

    }

    public OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext
                    .getSocketFactory();

            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient = okHttpClient.newBuilder()
                    .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(1, TimeUnit.MINUTES)
                    .readTimeout(1, TimeUnit.MINUTES)
                    .hostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER).build();

            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static void setUpSelfsignedSSLContext(File caFile) {
        InputStream caInput;
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            caInput = new FileInputStream(caFile);
            Certificate ca = cf.generateCertificate(caInput);
            caInput.close();

            // Create a KeyStore containing the trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // Create an SSLContext that uses our TrustManager
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);

        } catch (CertificateException | IOException | NoSuchAlgorithmException |
                KeyStoreException | KeyManagementException e) {
            Log.e("TAG", "setUpSelfsignedSSLContext: " + e.getLocalizedMessage());
        }
    }

    public OkHttpClient.Builder getUnsafeOkHttpClientBuilder() {
        try {
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true);

            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
