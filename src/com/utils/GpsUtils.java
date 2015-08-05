package com.utils;

import java.util.Iterator;
import java.util.List;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.pyxx.app.ShareApplication;

/**
 * 定位工具类
 * 
 * @author wll
 */
public class GpsUtils {
	public static final String TAG = "GpsUtils";
	public static final boolean DEBUG = false;
	public static String GPS_FINSH = "com.city_life.gps.finish";

	public static void getLocation() {
		baidulocation();
	}

	public static void baidulocation() {
		if (ShareApplication.debug)
			System.out.println("开始定位");
		final LocationClient mLocationClient = new LocationClient(
				ShareApplication.share);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setCoorType("bd09ll");
		option.setPriority(LocationClientOption.NetWorkFirst);
		option.setPoiExtraInfo(true);
		option.setProdName("gps");
		option.setScanSpan(5000);
		mLocationClient.setLocOption(option);
		mLocationClient.registerLocationListener(new BDLocationListener() {
			@Override
			public void onReceiveLocation(BDLocation location) {
				Intent intent = new Intent(GpsUtils.GPS_FINSH);

				if (location == null) {
					PerfHelper.setInfo(PerfHelper.P_GPS,
							29.567342 + "," + 106.572127);
					PerfHelper.setInfo(PerfHelper.P_GPS_LATI, 29.567342 + "");
					PerfHelper.setInfo(PerfHelper.P_GPS_LONG, 106.572127 + "");
					PerfHelper.setInfo(PerfHelper.P_GPS_YES, false);
					ShareApplication.share.sendBroadcast(intent);
					return;
				}

				if (location.getLocType() >= 162
						&& location.getLocType() <= 167) {
					// gt = new GeoPoint((int) ((29.567342) * 1e6),
					// (int) ((106.572127) * 1e6));
					PerfHelper.setInfo(PerfHelper.P_GPS,
							29.567342 + "," + 106.572127);
					PerfHelper.setInfo(PerfHelper.P_GPS_LATI, 29.567342 + "");
					PerfHelper.setInfo(PerfHelper.P_GPS_LONG, 106.572127 + "");
					PerfHelper.setInfo(PerfHelper.P_GPS_YES, false);
				} else {
					// gt = new GeoPoint((int) (location.getLatitude() * 1e6),
					// (int) (location.getLongitude() * 1e6));
					PerfHelper.setInfo(PerfHelper.P_GPS, location.getLatitude()
							+ "," + location.getLongitude());
					PerfHelper.setInfo(PerfHelper.P_GPS_LATI,
							location.getLatitude() + "");
					PerfHelper.setInfo(PerfHelper.P_GPS_LONG,
							location.getLongitude() + "");
					PerfHelper.setInfo(PerfHelper.P_GPS_YES, true);
				}
				if (ShareApplication.debug) {
					System.out.println("定位结束:" + location.getLatitude() + ","
							+ location.getLongitude());
					System.out.println("定位结果代码:" + location.getLocType());
					ShareApplication.share.sendBroadcast(intent);
				}
				if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
					PerfHelper.setInfo(PerfHelper.P_NOW_CITY,
							location.getCity());
				}
				mLocationClient.stop();
			}

			@SuppressWarnings("unused")
			public void onReceivePoi(BDLocation location) {
				if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
					String diz = location.getCity();
				}
				// return ;
			}
		});
		mLocationClient.start();
	}

	/**
	 * 定位完成
	 */
	public void LocationOver() {

	}

	public static Location requestNetworkLocation() {
		try {
			TelephonyManager telMan = (TelephonyManager) ShareApplication.share
					.getSystemService(Context.TELEPHONY_SERVICE);
			String mcc, mnc;
			int cid, lac;
			String operator = telMan.getNetworkOperator();
			boolean isCdma = false;
			CellLocation cl = telMan.getCellLocation();
			if (cl instanceof GsmCellLocation) {
				cid = ((GsmCellLocation) cl).getCid();
				lac = ((GsmCellLocation) cl).getLac();
				mcc = operator.substring(0, 3);
				mnc = operator.substring(3);
			} else if (cl instanceof CdmaCellLocation) {
				cid = ((CdmaCellLocation) cl).getBaseStationId();
				lac = ((CdmaCellLocation) cl).getNetworkId();

				mcc = operator.substring(0, 3);
				int systemId = ((CdmaCellLocation) cl).getSystemId();
				mnc = String.valueOf(systemId);
				isCdma = true;
			} else {
				return null;
			}

			JSONObject tower = new JSONObject();
			try {
				tower.put("cell_id", cid);
				tower.put("location_area_code", lac);
				tower.put("mobile_country_code", mcc);
				tower.put("mobile_network_code", mnc);
			} catch (JSONException e) {
				if (DEBUG)
					Log.e(TAG, "JSONObject put failed", e);
			}

			JSONArray jarray = new JSONArray();
			jarray.put(tower);

			List<NeighboringCellInfo> ncis = telMan.getNeighboringCellInfo();
			Iterator<NeighboringCellInfo> iter = ncis.iterator();

			NeighboringCellInfo nci;
			JSONObject tmpTower;
			while (iter.hasNext()) {
				nci = iter.next();
				tmpTower = new JSONObject();
				try {
					tmpTower.put("cell_id", nci.getCid());
					tmpTower.put("location_area_code", nci.getLac()); // 1.6不支持这个函数，需要单另打包。
					tmpTower.put("mobile_country_code", mcc);
					tmpTower.put("mobile_network_code", mnc);
				} catch (JSONException e) {
					if (DEBUG)
						Log.e(TAG, "JSONObject put failed", e);
				}
				jarray.put(tmpTower);
			}
			JSONObject object;
			if (!isCdma)
				object = createJSONObject("cell_towers", jarray);
			else
				object = createCDMAJSONObject("cell_towers", jarray, mcc, mnc);

			String locinfo = null;
			int tryCount = 0; // 可能网络不好会导致一次请求失败，多试几次
			while (tryCount < 3 && locinfo == null) {
				tryCount++;
				try {
					DefaultHttpClient httpClient = new DefaultHttpClient();
					HttpPost httpPost = new HttpPost(
							"http://www.google.com/loc/json");
					httpPost.setEntity(new StringEntity(object.toString()));
					locinfo = EntityUtils.toString(httpClient.execute(httpPost)
							.getEntity());
				} catch (Exception e) {
					e.printStackTrace();
					try {
						Thread.sleep(2000); // 睡2秒再试
					} catch (Exception e1) {
					}
				}
			}

			if (locinfo == null)
				return null;

			if (DEBUG)
				Log.d(TAG, locinfo);
			Location location = new Location("telephone");
			JSONObject jso = new JSONObject(locinfo);
			JSONObject locObj = jso.getJSONObject("location");
			if (locObj.has("latitude") && locObj.has("longitude")) {
				location.setLatitude(locObj.getDouble("latitude"));
				location.setLongitude(locObj.getDouble("longitude"));
				location.setAccuracy(locObj.has("accuracy") ? (float) locObj
						.getDouble("accuracy") : 0.0f);
				location.setTime(System.currentTimeMillis());
				return location;
			}
		} catch (Exception e) {
			if (DEBUG)
				Log.d(TAG,
						"request GsmCellLocation failed,reason:"
								+ e.getMessage());
		}

		return null;

	}

	private static JSONObject createJSONObject(String arrayName, JSONArray array) {
		JSONObject object = new JSONObject();
		try {
			object.put("version", "1.1.0");
			object.put("host", "maps.google.com");
			object.put(arrayName, array);
		} catch (JSONException e) {
			if (DEBUG)
				Log.e(TAG, "JSONObject put failed", e);
		}
		return object;
	}

	private static JSONObject createCDMAJSONObject(String arrayName,
			JSONArray array, String mcc, String mnc) {
		JSONObject object = new JSONObject();
		try {
			object.put("version", "1.1.0");
			object.put("host", "maps.google.com");
			object.put("home_mobile_country_code", mcc);
			object.put("home_mobile_network_code", mnc);
			object.put("radio_type", "cdma");
			object.put("request_address", true);
			if ("460".equals(mcc))
				object.put("address_language", "zh_CN");
			else
				object.put("address_language", "en_US");

			object.put(arrayName, array);
		} catch (JSONException e) {
			if (DEBUG)
				Log.e(TAG, "JSONObject put failed", e);
		}
		return object;
	}

	/**
	 * 计算距离
	 * 
	 * 网 返回单位米；
	 */
	public synchronized static double distanceByLngLat(double lng1,
			double lat1, double lng2, double lat2) {
		double radLat1 = lat1 * Math.PI / 180;
		double radLat2 = lat2 * Math.PI / 180;
		double a = radLat1 - radLat2;
		double b = lng1 * Math.PI / 180 - lng2 * Math.PI / 180;
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(radLat1) * Math.cos(radLat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * 6378137.0;
		s = Math.round(s * 10000) / 10000;
		return s;
	}

	private static final double EARTH_RADIUS = 6378137.0;

	// 两个方法差不多；
	/**
	 * 计算距离 他们 直接传入自己的经纬度和另外一个点的经纬度
	 * 
	 * @param longitude1
	 * @param latitude1
	 * @param longitude2
	 * @param latitude2
	 * @return 返回单位米；
	 */
	public synchronized static double getDistance(double longitude1,
			double latitude1, double longitude2, double latitude2) {
		double Lat1 = rad(latitude1);
		double Lat2 = rad(latitude2);
		double a = Lat1 - Lat2;
		double b = rad(longitude1) - rad(longitude2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(Lat1) * Math.cos(Lat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000;
		return s;
	}

	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}
}
