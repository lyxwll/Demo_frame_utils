package com.pyxx.dao;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.os.Build;

import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.HttpUtil;
import com.loopj.android.http.RequestParams;
import com.pyxx.app.ShareApplication;
import com.utils.FinalVariable;
import com.utils.JniUtils;

/**
 * SSLSocketFactory类，通过它可创建SSLSocket，并可获得相应的加密套件
 * 
 * @author wll
 */
public class MySSLSocketFactory extends SSLSocketFactory {

	// SSLSocket是扩展的Socket并提供使用SSL或TLS协议的安全套接字。
	SSLContext sslContext = SSLContext.getInstance("TLS");

	public MySSLSocketFactory(KeyStore truststore)
			throws NoSuchAlgorithmException, KeyManagementException,
			KeyStoreException, UnrecoverableKeyException {

		super(truststore);

		TrustManager tm = new X509TrustManager() {

			public void checkClientTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {

			}

			public void checkServerTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {

			}

			public X509Certificate[] getAcceptedIssuers() {

				return null;

			}

		};

		sslContext.init(null, new TrustManager[] { tm }, null);

	}

	@Override
	public Socket createSocket(Socket socket, String host, int port,
			boolean autoClose) throws IOException, UnknownHostException {

		return sslContext.getSocketFactory().createSocket(socket, host, port,
				autoClose);

	}

	@Override
	public Socket createSocket() throws IOException {

		return sslContext.getSocketFactory().createSocket();

	}

	private static final int REQUEST_TIMEOUT = 15 * 1000;// 设置请求超时10秒钟
	private static final int SO_TIMEOUT = 15 * 1000; // 设置等待数据超时时间10秒钟

	public static HttpClient getNewHttpClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);
			SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
			HttpConnectionParams.setConnectionTimeout(params, REQUEST_TIMEOUT);
			HttpConnectionParams.setSoTimeout(params, SO_TIMEOUT);
			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));
			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, registry);
			DefaultHttpClient httpclient = new DefaultHttpClient(ccm, params);
			httpclient.getParams().setParameter(
					CoreProtocolPNames.USER_AGENT,
					"Palmtrends (Android " + Build.MODEL + "; OS "
							+ Build.VERSION.RELEASE + "; "
							+ ShareApplication.share.getPackageName() + ")");
			return httpclient;
		} catch (Exception e) {
			BasicHttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams,
					REQUEST_TIMEOUT);
			HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
			DefaultHttpClient httpclient = new DefaultHttpClient(httpParams);
			httpclient.getParams().setParameter(
					CoreProtocolPNames.USER_AGENT,
					"Palmtrends (Android " + Build.MODEL + "; OS "
							+ Build.VERSION.RELEASE + "; "
							+ ShareApplication.share.getPackageName() + ")");
			return httpclient;
		}
	}

	private static final int REQUEST_TIMEOUT_SUBMIT = 30 * 1000;// 设置请求超时10秒钟
	private static final int SO_TIMEOUTT_SUBMIT = 30 * 1000; // 设置等待数据超时时间10秒钟

	public static HttpClient getNewHttpClientsubmit() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);
			SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
			HttpConnectionParams.setConnectionTimeout(params,
					REQUEST_TIMEOUT_SUBMIT);
			HttpConnectionParams.setSoTimeout(params, SO_TIMEOUTT_SUBMIT);
			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));
			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, registry);
			DefaultHttpClient httpclient = new DefaultHttpClient(ccm, params);
			httpclient.getParams().setParameter(
					CoreProtocolPNames.USER_AGENT,
					"Palmtrends (Android " + Build.MODEL + "; OS "
							+ Build.VERSION.RELEASE + "; "
							+ ShareApplication.share.getPackageName() + ")");
			return httpclient;
		} catch (Exception e) {
			BasicHttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams,
					REQUEST_TIMEOUT_SUBMIT);
			HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUTT_SUBMIT);
			DefaultHttpClient httpclient = new DefaultHttpClient(httpParams);
			httpclient.getParams().setParameter(
					CoreProtocolPNames.USER_AGENT,
					"Palmtrends (Android " + Build.MODEL + "; OS "
							+ Build.VERSION.RELEASE + "; "
							+ ShareApplication.share.getPackageName() + ")");
			return httpclient;
		}
	}

	public static String getinfo_submit(String url, List<NameValuePair> param)
			throws Exception {
		// param.add(new BasicNameValuePair("uid", PerfHelper
		// .getStringData(PerfHelper.P_USERID)));
		// param.add(new BasicNameValuePair("platform", "a"));
		param.add(new BasicNameValuePair("mobile", Build.MODEL));
		param.add(new BasicNameValuePair("pid", FinalVariable.pid));
		param.add(new BasicNameValuePair("e", JniUtils.getkey()));
		HttpClient httpclient = MySSLSocketFactory.getNewHttpClientsubmit();
		HttpPost hp = new HttpPost(url);
		if (ShareApplication.debug) {
			String str = url;
			for (NameValuePair bp : param) {
				BasicNameValuePair b = (BasicNameValuePair) bp;
				str += "&" + b.getName() + "=" + b.getValue();
			}
			System.out.println(str);
		}
		hp.setEntity(new UrlEncodedFormEntity(param, "utf-8"));
		HttpResponse response = httpclient.execute(hp);
		String json = EntityUtils.toString(response.getEntity(), "utf-8");

		if (json != null && !(json.startsWith("{") || json.startsWith("["))) {
			json = json.substring(1, json.length());
		}
		return json;
	}

	public static String getinfo(String url, List<NameValuePair> param)
			throws Exception {
		// param.add(new BasicNameValuePair("uid", PerfHelper
		// .getStringData(PerfHelper.P_USERID)));
		// param.add(new BasicNameValuePair("platform", "a"));
		param.add(new BasicNameValuePair("mobile", Build.MODEL));
		param.add(new BasicNameValuePair("pid", FinalVariable.pid));
		HttpClient httpclient = MySSLSocketFactory.getNewHttpClient();
		HttpPost hp = new HttpPost(url);
		if (ShareApplication.debug) {
			String str = url;
			for (NameValuePair bp : param) {
				BasicNameValuePair b = (BasicNameValuePair) bp;
				str += "&" + b.getName() + "=" + b.getValue();
			}
			System.out.println(str);
		}
		hp.setEntity(new UrlEncodedFormEntity(param, "utf-8"));
		HttpResponse response = httpclient.execute(hp);
		String json = EntityUtils.toString(response.getEntity(), "utf-8");

		if (json != null && !(json.startsWith("{") || json.startsWith("["))) {
			json = json.substring(1, json.length());
		}
		return json;
	}

	public static String getinfo_Get(String url) throws Exception {
		HttpClient httpclient = MySSLSocketFactory.getNewHttpClient();
		// HttpGet hp = new HttpGet(url + "?mobile="
		// + URLEncoder.encode(Build.MODEL, "utf-8") + "&pid="
		// + FinalVariable.pid + "&e=" + JniUtils.getkey());
		HttpGet hp = new HttpGet(url);
		HttpResponse response = httpclient.execute(hp);
		String json = EntityUtils.toString(response.getEntity(), "utf-8");
		if (json != null && !(json.startsWith("{") || json.startsWith("["))) {
			json = json.substring(1, json.length());
		}
		return json;
	}

	public static void http_post_json(String url, RequestParams params,
			BaseJsonHttpResponseHandler<String> handler) {
		HttpUtil.post(url, params, handler);
	}
}