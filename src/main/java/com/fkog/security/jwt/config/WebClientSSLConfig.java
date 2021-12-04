package com.fkog.security.jwt.config;

import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.util.ResourceUtils;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientSSLConfig {
	@Value("${server.port}")
	private int serverPort;

	@Value("${trust.store.password}")
	private String trustStorePassword;

	@Value("${trust.store}")
	private Resource trustStore;

//	@Bean
//	public WebClient getWebClient() throws Exception {
//		return WebClient.builder().baseUrl("http://localhost:8080").build();
//	}
	
	@Bean
	public WebClient getWebClient() throws Exception {
//		SslContext sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE)
//				.build();
//		HttpClient httpClient = HttpClient.create().secure(t -> t.sslContext(sslContext));
//		return WebClient.builder().baseUrl("https://localhost:8443")
//				.clientConnector(new ReactorClientHttpConnector(httpClient)).build();
//		https://localhost:8443/v1/public/hello
		// ******************************* working code

		KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		TrustManagerFactory trustManagerFactory = TrustManagerFactory
				.getInstance(TrustManagerFactory.getDefaultAlgorithm());

		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		keyStore.load(new FileInputStream((ResourceUtils.getFile(trustStore.getURL()))),
				trustStorePassword.toCharArray());

		// Set up key manager factory to use our key store

		keyManagerFactory.init(keyStore, trustStorePassword.toCharArray());

		// truststore
		KeyStore tStore = KeyStore.getInstance("PKCS12");
		tStore.load(new FileInputStream((ResourceUtils.getFile(trustStore.getURL()))),
				trustStorePassword.toCharArray());

		trustManagerFactory.init(tStore);

		SslContext sslContext = SslContextBuilder.forClient().keyManager(keyManagerFactory)
				.trustManager(trustManagerFactory).build();

		HttpClient httpClient = HttpClient.create().secure(sslSpec -> sslSpec.sslContext(sslContext));
		return WebClient.builder().baseUrl("https://localhost:8443/")
				.clientConnector(new ReactorClientHttpConnector(httpClient)).build();
	}
	
}
