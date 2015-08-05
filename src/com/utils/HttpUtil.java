package com.utils;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

/**
 * 
 * @author zhangyi
 * 
 */
public class HttpUtil {

	/**
	 * 直接通过HTTP协议提交数据到服务器,实现表单提交功能
	 * 
	 * @param actionUrl
	 *            上传路径
	 * @param params
	 *            请求参数 key为参数名,value为参数值
	 * @param file
	 *            上传文件
	 */
	public static InputStream post(String actionUrl,
			Map<String, String> params, FormFile[] files) throws Exception {
		DataOutputStream outStream = null;
		InputStream ins = null;
		HttpURLConnection conn = null;

		try {
			String BOUNDARY = UUID.randomUUID().toString(); // 数据分隔线
			String MULTIPART_FORM_DATA = "multipart/form-data";
			URL url;
			url = new URL(actionUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);// 允许输入
			conn.setDoOutput(true);// 允许输出
			conn.setUseCaches(false);// 不使用Cache
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("Content-Type", MULTIPART_FORM_DATA
					+ "; boundary=" + BOUNDARY);

			StringBuilder sb = new StringBuilder();

			// 上传的表单参数部分
			outStream = new DataOutputStream(conn.getOutputStream());
			if (params != null) {
				for (Map.Entry<String, String> entry : params.entrySet()) {// 构建表单字段内容
					sb.append("--");
					sb.append(BOUNDARY);
					sb.append("\r\n");
					sb.append("Content-Disposition: form-data; name=\""
							+ entry.getKey() + "\"\r\n\r\n");
					sb.append(entry.getValue());
					sb.append("\r\n");
				}

				outStream.write(sb.toString().getBytes());// 发送表单字段数据
			}

			// 上传的文件部分
			if (null != files) {
				for (FormFile file : files) {
					StringBuilder split = new StringBuilder();
					split.append("--");
					split.append(BOUNDARY);
					split.append("\r\n");
					split.append("Content-Disposition: form-data;name=\""
							+ file.getFormname() + "\";filename=\""
							+ file.getFilname() + "\"\r\n");
					split.append("Content-Type: " + file.getContentType()
							+ "\r\n\r\n");
					outStream.write(split.toString().getBytes());
					outStream.write(file.getData(), 0, file.getData().length);
					outStream.write("\r\n".getBytes());
				}
				byte[] end_data = ("--" + BOUNDARY + "--\r\n").getBytes();// 数据结束标志
				outStream.write(end_data);
			}
			outStream.flush();
			int cah = conn.getResponseCode();
			if (cah != 200)
				throw new RuntimeException("请求url失败");
			ins = conn.getInputStream();
			return ins;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("网络异常：请确认是否已打开网络连接");
		}
	}

}
