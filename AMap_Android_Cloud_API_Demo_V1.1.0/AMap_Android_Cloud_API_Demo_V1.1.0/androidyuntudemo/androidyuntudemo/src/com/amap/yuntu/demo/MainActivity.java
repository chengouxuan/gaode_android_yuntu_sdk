package com.amap.yuntu.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.amap.api.cloud.model.AMapCloudException;
import com.amap.api.cloud.model.CloudItem;
import com.amap.api.cloud.model.CloudItemDetail;
import com.amap.api.cloud.model.LatLonPoint;
import com.amap.api.cloud.search.CloudResult;
import com.amap.api.cloud.search.CloudSearch;
import com.amap.api.cloud.search.CloudSearch.OnCloudSearchListener;
import com.amap.api.cloud.search.CloudSearch.SearchBound;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.yuntudemo.overlay.PoiOverlay;
import com.amap.yuntudemo.util.AMapUtil;
import com.amap.yuntudemo.util.ToastUtil;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;

public class MainActivity extends Activity implements OnMarkerClickListener,
		InfoWindowAdapter, OnCloudSearchListener, OnInfoWindowClickListener {
	private MapView mapView;
	private AMap mAMap;
	private CloudSearch mCloudSearch;
	private String mTableID = "546ac195e4b08f472bf42a6d"; // 用户tableid，从官网下载测试数据后在云图中新建地图并导入，获取相应的tableid
	private String mId = ""; // 用户table 行编号
	private String mKeyWord = "公园"; // 搜索关键字
	private CloudSearch.Query mQuery;
	private LatLonPoint mCenterPoint = new LatLonPoint(39.942753, 116.428650); // 周边搜索中心点
	private LatLonPoint mPoint1 = new LatLonPoint(39.941711, 116.382248);
	private LatLonPoint mPoint2 = new LatLonPoint(39.884882, 116.359566);
	private LatLonPoint mPoint3 = new LatLonPoint(39.878120, 116.437630);
	private LatLonPoint mPoint4 = new LatLonPoint(39.941711, 116.382248);
	private PoiOverlay mPoiCloudOverlay;
	private List<CloudItem> mCloudItems;
	private ProgressDialog progDialog = null;
	private Marker mCloudIDMarer;
	private String TAG = "AMapYunTuDemo";
	private String mLocalCityName = "东城区";

	private CloudItemDetail item;

	private ArrayList<CloudItem> items = new ArrayList<CloudItem>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);// 此方法必须重写
		init();
	}

	public void searchById(View view) {
		showProgressDialog("searchById");
		items.clear();
		mCloudSearch.searchCloudDetailAsyn(mTableID, mId);
	}

	public void searchByBound(View view) {
		showProgressDialog("searchByBound");
		items.clear();
		SearchBound bound = new SearchBound(new LatLonPoint(
				mCenterPoint.getLatitude(), mCenterPoint.getLongitude()), 4000);
		try {
			mQuery = new CloudSearch.Query(mTableID, mKeyWord, bound);
			mQuery.setPageSize(10);
			CloudSearch.Sortingrules sorting = new CloudSearch.Sortingrules("_location",
					false);
			mQuery.setSortingrules(sorting);
			mCloudSearch.searchCloudAsyn(mQuery);// 异步搜索
		} catch (AMapCloudException e) {
			e.printStackTrace();
		}
		
	}

	public void searchByPolygon(View view) {
		showProgressDialog("searchByPolygon");
		items.clear();
		List<LatLonPoint> points = new ArrayList<LatLonPoint>();
		points.add(mPoint1);
		points.add(mPoint2);
		points.add(mPoint3);
		points.add(mPoint4);
		SearchBound bound =new SearchBound(points);
		try {
			mQuery = new CloudSearch.Query(mTableID, mKeyWord, bound);
			mCloudSearch.searchCloudAsyn(mQuery);
		} catch (AMapCloudException e) {
			e.printStackTrace();
		}
	
	}

	public void searchByLocal(View view) {
		showProgressDialog("searchByLocal");
		items.clear();
		SearchBound bound = new SearchBound(mLocalCityName);
		try {
			mQuery = new CloudSearch.Query(mTableID, mKeyWord, bound);
			mCloudSearch.searchCloudAsyn(mQuery);
		} catch (AMapCloudException e) {
			e.printStackTrace();
		}
	
	}

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		if (mAMap == null) {
			mAMap = mapView.getMap();
		}
		mCloudSearch = new CloudSearch(this);
		mCloudSearch.setOnCloudSearchListener(this);
		mAMap.setOnMarkerClickListener(this);
		mAMap.setOnInfoWindowClickListener(this);
		mAMap.setInfoWindowAdapter(this);
		mAMap.setOnInfoWindowClickListener(this);

	}

	/**
	 * 显示进度框
	 */
	private void showProgressDialog(String message) {
		if (progDialog == null)
			progDialog = new ProgressDialog(this);
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(true);
		progDialog.setMessage("正在搜索:\n" + mTableID + "\n搜索方式:" + message);
		progDialog.show();
	}

	/**
	 * 隐藏进度框
	 */
	private void dissmissProgressDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
		}
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	@Override
	public void onCloudItemDetailSearched(CloudItemDetail item, int rCode) {
		dissmissProgressDialog();// 隐藏对话框
		if (rCode == 0 && item != null) {
			if (mCloudIDMarer != null) {
				mCloudIDMarer.destroy();
			}
			mAMap.clear();
			LatLng position = AMapUtil.convertToLatLng(item.getLatLonPoint());
			mAMap.animateCamera(CameraUpdateFactory
					.newCameraPosition(new CameraPosition(position, 18, 0, 30)));
			mCloudIDMarer = mAMap.addMarker(new MarkerOptions()
					.position(position)
					.title(item.getTitle())
					.icon(BitmapDescriptorFactory
							.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
			items.add(item);
			Log.d(TAG, "_id" + item.getID());
			Log.d(TAG, "_location" + item.getLatLonPoint().toString());
			Log.d(TAG, "_name" + item.getTitle());
			Log.d(TAG, "_address" + item.getSnippet());
			Log.d(TAG, "_caretetime" + item.getCreatetime());
			Log.d(TAG, "_updatetime" + item.getUpdatetime());
			Log.d(TAG, "_distance" + item.getDistance());
			Iterator iter = item.getCustomfield().entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				Object key = entry.getKey();
				Object val = entry.getValue();
				Log.d(TAG, key + "   " + val);
			}
		}

	}

	@Override
	public void onCloudSearched(CloudResult result, int rCode) {
		dissmissProgressDialog();

		if (rCode == 0) {
			if (result != null && result.getQuery() != null) {
				if (result.getQuery().equals(mQuery)) {
					mCloudItems = result.getClouds();

					if (mCloudItems != null && mCloudItems.size() > 0) {
						mAMap.clear();
						mPoiCloudOverlay = new PoiOverlay(mAMap, mCloudItems);
						mPoiCloudOverlay.removeFromMap();
						mPoiCloudOverlay.addToMap();
						// mPoiCloudOverlay.zoomToSpan();
						for (CloudItem item : mCloudItems) {
							items.add(item);
							Log.d(TAG, "_id " + item.getID());
							Log.d(TAG, "_location "
									+ item.getLatLonPoint().toString());
							Log.d(TAG, "_name " + item.getTitle());
							Log.d(TAG, "_address " + item.getSnippet());
							Log.d(TAG, "_caretetime " + item.getCreatetime());
							Log.d(TAG, "_updatetime " + item.getUpdatetime());
							Log.d(TAG, "_distance " + item.getDistance());
							Iterator iter = item.getCustomfield().entrySet()
									.iterator();
							while (iter.hasNext()) {
								Map.Entry entry = (Map.Entry) iter.next();
								Object key = entry.getKey();
								Object val = entry.getValue();
								Log.d(TAG, key + "   " + val);
							}
						}
						if (mQuery.getBound().getShape()
								.equals(SearchBound.BOUND_SHAPE)) {// 圆形
							mAMap.addCircle(new CircleOptions()
									.center(new LatLng(mCenterPoint
											.getLatitude(), mCenterPoint
											.getLongitude())).radius(5000)
									.strokeColor(
									// Color.argb(50, 1, 1, 1)
											Color.RED)
									.fillColor(Color.argb(50, 1, 1, 1))
									.strokeWidth(25));

							mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
									new LatLng(mCenterPoint.getLatitude(),
											mCenterPoint.getLongitude()), 12));

						} else if (mQuery.getBound().getShape()
								.equals(SearchBound.POLYGON_SHAPE)) {
							mAMap.addPolygon(new PolygonOptions()
									.add(AMapUtil.convertToLatLng(mPoint1))
									.add(AMapUtil.convertToLatLng(mPoint2))
									.add(AMapUtil.convertToLatLng(mPoint3))
									.add(AMapUtil.convertToLatLng(mPoint4))
									.fillColor(Color.LTGRAY)
									.strokeColor(Color.RED).strokeWidth(1));
							LatLngBounds bounds = new LatLngBounds.Builder()
									.include(AMapUtil.convertToLatLng(mPoint1))
									.include(AMapUtil.convertToLatLng(mPoint2))
									.include(AMapUtil.convertToLatLng(mPoint3))
									.build();
							mAMap.moveCamera(CameraUpdateFactory
									.newLatLngBounds(bounds, 50));
						} else if ((mQuery.getBound().getShape()
								.equals(SearchBound.LOCAL_SHAPE))) {
							mPoiCloudOverlay.zoomToSpan();
						}

					} else {
						ToastUtil.show(this, R.string.no_result);
					}
				}
			} else {
				ToastUtil.show(this, R.string.no_result);
			}
		} else {
			ToastUtil.show(this, R.string.error_network);
		}

	}

	@Override
	public View getInfoContents(Marker arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View getInfoWindow(Marker arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onMarkerClick(Marker arg0) {
		return false;
	}

	@Override
	public void onInfoWindowClick(Marker arg0) {
		String tile = arg0.getTitle();
		for (CloudItem item : items) {
			if (tile.equals(item.getTitle())) {
				Intent intent = new Intent(MainActivity.this, PoiActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString(PoiActivity.POIID, item.getID());
				bundle.putString(PoiActivity.POINAME, item.getTitle());
				bundle.putString(PoiActivity.POILOCATION, "{"
						+ item.getLatLonPoint().getLatitude() + ","
						+ item.getLatLonPoint().getLatitude() + "}");
				bundle.putString(PoiActivity.POIADDRESS, item.getSnippet());
				bundle.putString(PoiActivity.POICREATETIME,
						item.getCreatetime());
				bundle.putString(PoiActivity.POIUPDATETIME,
						item.getUpdatetime());
				bundle.putString(PoiActivity.POIDISTANCE,
						"" + item.getDistance());
				HashMap<String, String> customField = item.getCustomfield();
				ArrayList<String> keys = new ArrayList<String>();
				ArrayList<String> values = new ArrayList<String>();
				for (Entry<String, String> map : customField.entrySet()) {
					keys.add(map.getKey());
					values.add(map.getValue());
				}
				bundle.putStringArrayList(PoiActivity.POIKEY, keys);
				bundle.putStringArrayList(PoiActivity.POIVALUE, values);

				intent.putExtras(bundle);
				startActivity(intent);
				break;
			}

		}

	}

	/**
	 * 
	 * 返回键监听
	 * */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {

			finish();

		}
		return super.onKeyDown(keyCode, event);
	}

}
