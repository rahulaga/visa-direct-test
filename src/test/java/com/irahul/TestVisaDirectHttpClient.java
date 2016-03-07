package com.irahul;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.util.Base64;
import java.util.UUID;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.json.JSONObject;
import org.junit.Test;

public class TestVisaDirectHttpClient {
	private static final String apiUser = "XXXXXX";
	private static final String password = "XXXXXXX";

	@Test
	public void testVisaDirect() throws Exception {
		System.out.println("Starting...");

		// load the keystore containing the client certificate
		final KeyStore keystore = KeyStore.getInstance("jks");
		InputStream keystoreInput = new FileInputStream(new File("c:/java/jdk1.8.0_45/jre/lib/security/keystore.jks"));
		keystore.load(keystoreInput, "secret".toCharArray());

		// load the trust store
		KeyStore truststore = KeyStore.getInstance("jks");
		InputStream truststoreInput = new FileInputStream(new File("c:/java/jdk1.8.0_45/jre/lib/security/cacerts"));
		truststore.load(truststoreInput, "changeit".toCharArray());

		SSLContext sslcontext = SSLContexts.custom().loadTrustMaterial(keystore, new TrustSelfSignedStrategy())
				.loadKeyMaterial(keystore, "secret".toCharArray())
				.loadTrustMaterial(truststore, new TrustSelfSignedStrategy()).loadKeyMaterial(truststore, "changeit".toCharArray()).build();

		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext);
		CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();

		HttpPost post = new HttpPost("https://sandbox.visa.com/rsrv_vpp/v1/acnl");
		post.setHeader("Content-Type", "application/json");
		post.setHeader("Accept", "application/json");
		String clientTransactionid=UUID.randomUUID().toString();
		//post.setHeader("X-Client-Transaction-ID", clientTransactionid);

		// add basic auth
		String authString = apiUser + ":" + password;
		byte[] authEncBytes = Base64.getEncoder().encode(authString.getBytes());
		String authStringEnc = new String(authEncBytes);
		System.out.println("auth string: " + authString);
		System.out.println("Base64 encoded auth string: " + authStringEnc);
		post.setHeader("Authorization", "Basic " + authStringEnc);

		// add json body
		URL path = TestVisaDirectHttpClient.class.getResource("/request.json");
		System.out.println("Request file=" + path);
		byte[] jsonBytes = Files.readAllBytes(Paths.get(path.toURI()));
		String json = new String(jsonBytes, "UTF8");
		
		JSONObject jobj = new JSONObject(json);
		String sysTrace = Util.createSystemsTraceAuditNumber();
		jobj.put("SystemsTraceAuditNumber", sysTrace);
		jobj.put("RetrievalReferenceNumber", Util.createRRN(sysTrace));
		System.out.println("String out"+jobj.toString());

		StringEntity body = new StringEntity(jobj.toString());
		post.setEntity(body);

		//proxy
		//HttpHost target = new HttpHost("sandbox.visa.com", 443, "https");
        HttpHost proxy = new HttpHost("internet.visa.com", 80, "http");

        RequestConfig config = RequestConfig.custom()
                .setProxy(proxy)
                .build();
        //post.setConfig(config);
		
        System.out.println("request="+post.toString());
		// Send post request
		HttpResponse response = httpclient.execute(post);

		// int responseCode = conn.getResponseCode();
		// System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post body : " + jobj.toString());
		System.out.println("Response:"+response.toString());
		System.out.println("X-Client-Transaction-ID="+clientTransactionid);
		//System.out.println("Response: " + response.getEntity().);
	}
}
