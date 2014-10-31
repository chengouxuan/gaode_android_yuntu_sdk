package com.amap.yuntu.demo;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PoiActivity extends Activity {
	private TextView mPoiTextView;
	private TextView mNameTextView;
	private TextView mLocationTextView;
	private TextView mAddressTextView;
	private TextView mCreateTextView;
	private TextView mUpdateTextView;
	private TextView mDistanceTextView;
	private LinearLayout mContainer;
	public static final String POIID = "poiid_text";
	public static final String POINAME = "poi_name";
	public static final String POILOCATION = "location_text";
	public static final String POIADDRESS = "address_text";
	public static final String POICREATETIME = "createtime_text";
	public static final String POIUPDATETIME = "update_time_text";
	public static final String POIDISTANCE = "distance_text";
	public static final String POIKEY = "poi_key";
	public static final String POIVALUE = "poi_value";

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.poi_layout);

		mPoiTextView = (TextView) findViewById(R.id.poiid_text);
		mNameTextView = (TextView) findViewById(R.id.name_text);
		mLocationTextView = (TextView) findViewById(R.id.location_text);
		mAddressTextView = (TextView) findViewById(R.id.address_text);
		mCreateTextView = (TextView) findViewById(R.id.createtime_text);
		mUpdateTextView = (TextView) findViewById(R.id.update_time_text);
		mDistanceTextView = (TextView) findViewById(R.id.distance_text);
		mContainer = (LinearLayout) findViewById(R.id.container);
		Bundle bundle = getIntent().getExtras();
		if (bundle == null) {
			return;
		}
		String poiId = bundle.getString(POIID, "");
		String poiName = bundle.getString(POINAME, "");
		String poiLocation = bundle.getString(POILOCATION, "");
		String poiAddress = bundle.getString(POIADDRESS, "");
		String poiCreateTime = bundle.getString(POICREATETIME, "");
		String poiUpdateTime = bundle.getString(POIUPDATETIME, "");
		String poiDistance = bundle.getString(POIDISTANCE, "");
		mPoiTextView.setText(poiId);
		mNameTextView.setText(poiName);
		mLocationTextView.setText(poiLocation);
		mAddressTextView.setText(poiAddress);
		mCreateTextView.setText(poiCreateTime);
		mUpdateTextView.setText(poiUpdateTime);
		mDistanceTextView.setText(poiDistance);

		ArrayList<String> keys = bundle.getStringArrayList(POIKEY);
		ArrayList<String> values = bundle.getStringArrayList(POIVALUE);
		for (int i = 0; i < keys.size(); i++) {
			View view = getLayoutInflater().inflate(R.layout.item_layout, null);
			TextView fieldText = (TextView) view
					.findViewById(R.id.poi_field_id);
			TextView valueText = (TextView) view
					.findViewById(R.id.poi_value_id);
			fieldText.setText(keys.get(i));
			valueText.setText(values.get(i));
			mContainer.addView(view);
		}

	}

	/**
	 * 
	 * 返回键监听
	 * */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(PoiActivity.this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);

			finish();

		}
		return super.onKeyDown(keyCode, event);
	}
}
