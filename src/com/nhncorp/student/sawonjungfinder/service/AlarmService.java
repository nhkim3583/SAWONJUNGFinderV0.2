package com.nhncorp.student.sawonjungfinder.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;

import com.nhncorp.student.sawonjungfinder.R;
import com.nhncorp.student.sawonjungfinder.constants.Constants;
import com.nhncorp.student.sawonjungfinder.database.DbOpenHelper;
import com.wizturn.sdk.central.Central;
import com.wizturn.sdk.central.CentralManager;
import com.wizturn.sdk.peripheral.Peripheral;
import com.wizturn.sdk.peripheral.PeripheralScanListener;

public class AlarmService extends Service {

	private CentralManager centralManager = null;

	// notification
	NotificationManager notificationManager;
	Notification notification;

	// thread 사용 위한 선언
	private Thread mUiThread;
	final Handler mHandler = new Handler();

	private static int alarmCount = 0;

	private DbOpenHelper mDbOpenHelper;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {

		super.onCreate();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		super.onStartCommand(intent, flags, startId);
		getData();
		System.out.println("======================getData()================");

		if (Constants.ALARM_STATE.equals("1")) {
			setCentralManager();
			System.out.println("=====================start service========="
					+ Constants.DEVICE_NAME + "===================");
		}
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		System.out.println("=================stop service====================");
		super.onDestroy();
		centralManager.stopScanning();
	}

	private void setCentralManager() {
		if (centralManager != null) {
			centralManager = CentralManager.getInstance();
			centralManager.init(getApplicationContext());
			centralManager
					.setPeripheralScanListener(new PeripheralScanListener() {
						@Override
						public void onPeripheralScan(Central central,
								final Peripheral peripheral) {
							if (Constants.DEVICE_ADDRESS.equals(peripheral
									.getBDAddress())) {
								runOnUiThread(new Runnable() {
									public void run() {

										setNotification(peripheral
												.getDistance());
										System.out
												.println("notification================================");
									}

								});

							}
						}

					});
			centralManager.startScanning();
		}
	}

	private void setNotification(double distance) {
		// push notification
		if (distance > 20 && alarmCount < 5) { // distance 값의 조절이 필요함
			alarmCount++;
			Intent intent = new Intent(
					"com.nhncorp.student.sawonjungfinder.service");
			PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
					intent, 0);

			notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			notification = new Notification.Builder(getApplicationContext())
					.setContentTitle("사원증이 멀어졌습니다.")
					.setContentText("사원증의 위치를 확인하십시오")
					.setSmallIcon(R.drawable.main_icon)
					.setTicker("사원증을 가지고 계십니까?").setAutoCancel(true)
					.setVibrate(new long[] { 1000, 1000 })
					.setContentIntent(pendingIntent).build();
			System.out.println("push===============================");
			notificationManager.notify(0, notification);

			// vibrate setting
			Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			vibrator.vibrate(1000);
		} else {
			alarmCount = 0;
		}
	}

	public final void runOnUiThread(Runnable action) {
		if (Thread.currentThread() != mUiThread) {
			mHandler.post(action);
		} else {
			action.run();
		}
	}

	private void getData() {
		mDbOpenHelper = new DbOpenHelper(getApplicationContext());
		mDbOpenHelper.open();
		Cursor mCursor = mDbOpenHelper.getAll();
		// 모든 row를 받아옴
		mCursor.moveToFirst();
		// 받아온 row의 attribute 값을 variable에 저장
		Constants.DEVICE_NAME = mCursor.getString(mCursor
				.getColumnIndex("name"));
		Constants.DEVICE_ADDRESS = mCursor.getString(mCursor
				.getColumnIndex("macaddress"));
		Constants.ALARM_STATE = mCursor.getString(mCursor
				.getColumnIndex("alarmstate"));
	}

}
