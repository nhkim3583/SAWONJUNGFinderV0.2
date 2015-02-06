package com.nhncorp.student.sawonjungfinder;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.nhncorp.student.sawonjungfinder.bluetooth.BlueToothEnabler;
import com.nhncorp.student.sawonjungfinder.constants.Constants;
import com.nhncorp.student.sawonjungfinder.database.DbOpenHelper;
import com.nhncorp.student.sawonjungfinder.finder.FinderActivity;
import com.nhncorp.student.sawonjungfinder.registration.RegistrationActivity;
import com.nhncorp.student.sawonjungfinder.service.AlarmService;

public class MainActivity extends Activity {

	private BlueToothEnabler bluetooth;

	private TextView deviceNameText;

	private ImageButton finderBtn;
	private ImageButton registrationBtn;
	private ImageButton timeBtn;
	private ImageButton devOnOffBtn;

	private DbOpenHelper mDbOpenHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		init();
	}

	private void init() {
		getView();
		bluetooth = new BlueToothEnabler();
		boolean isBluetooth = bluetooth.enableBlueTooth();
		if (isBluetooth) {
			Toast.makeText(this, "��������� �۵� �ǰ� �ֽ��ϴ�.", Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(this, "�ش� �ܸ��� ��������� �������� �ʽ��ϴ�.", Toast.LENGTH_LONG)
					.show();
		}
		dbMake();
		deviceConfirm(0); // nothing
		deviceNameText.setText(Constants.DEVICE_NAME);
		addListener();
	}

	private void dbMake() {
		mDbOpenHelper = new DbOpenHelper(this);
		mDbOpenHelper.open();
	}

	private void getData() {
		Cursor mCursor = mDbOpenHelper.getAll();
		// ��� row�� �޾ƿ�
		mCursor.moveToFirst();
		System.out.println(mCursor.getString(mCursor.getColumnIndex("name")));// test
		System.out.println(mCursor.getString(mCursor
				.getColumnIndex("macaddress")));// test
		// �޾ƿ� row�� attribute ���� variable�� ����
		Constants.DEVICE_NAME = mCursor.getString(mCursor
				.getColumnIndex("name"));
		Constants.DEVICE_ADDRESS = mCursor.getString(mCursor
				.getColumnIndex("macaddress"));
		Constants.ALARM_STATE = mCursor.getString(mCursor
				.getColumnIndex("alarmstate"));
		Constants.DEVICE_STATE = mCursor.getString(mCursor
				.getColumnIndex("devicestate"));
	}

	private void deviceConfirm(int sel) { // 1: time 2: dev
		getData();

		if (sel == 2) { // dev��ư�� ���� ��� devicestate�� ���� ����

			if (Constants.DEVICE_STATE.equals("0")) {
				mDbOpenHelper.updateColumn(1, Constants.DEVICE_NAME,
						Constants.DEVICE_ADDRESS, Constants.ALARM_STATE, "1");
				devOnOffBtn.setImageResource(R.drawable.power_orange);
				System.out.println("po"); // ///////////////////////
			} else if (Constants.DEVICE_STATE.equals("1")) {
				mDbOpenHelper.updateColumn(1, Constants.DEVICE_NAME,
						Constants.DEVICE_ADDRESS, Constants.ALARM_STATE, "0");
				devOnOffBtn.setImageResource(R.drawable.power_black);
				System.out.println("pb"); // ///////////////////////
			}
		} else if (sel == 1) { // time��ư�� ���� ��� alarmstate�� ���� ����

			if (Constants.ALARM_STATE.equals("0")) {
				mDbOpenHelper.updateColumn(1, Constants.DEVICE_NAME,
						Constants.DEVICE_ADDRESS, "1", Constants.DEVICE_STATE);
				timeBtn.setImageResource(R.drawable.time_orange);
				System.out.println("to"); // ///////////////////////
			} else if (Constants.ALARM_STATE.equals("1")) {
				mDbOpenHelper.updateColumn(1, Constants.DEVICE_NAME,
						Constants.DEVICE_ADDRESS, "0", Constants.DEVICE_STATE);
				timeBtn.setImageResource(R.drawable.time_black);
				System.out.println("tb"); // ///////////////////////
			}
		} else if (sel == 0) { // �ƹ��ϵ� ���� ����
			if (Constants.ALARM_STATE.equals("0")) {
				timeBtn.setImageResource(R.drawable.time_black);
				System.out.println("tb"); // ///////////////////////
			} else if (Constants.ALARM_STATE.equals("1")) {
				timeBtn.setImageResource(R.drawable.time_orange);
				System.out.println("to"); // ///////////////////////
				// �� ������ �� service ����
				setService();
			}
			if (Constants.DEVICE_STATE.equals("0")) {
				devOnOffBtn.setImageResource(R.drawable.power_black);
				System.out.println("pb"); // ///////////////////////
			} else if (Constants.DEVICE_STATE.equals("1")) {
				devOnOffBtn.setImageResource(R.drawable.power_orange);
				System.out.println("po"); // ///////////////////////
			}

		}

	}

	private void addListener() {
		finderBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						FinderActivity.class);
				startActivity(intent);

			}
		});

		registrationBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						RegistrationActivity.class);
				startActivity(intent);
				MainActivity.this.finish();

			}
		});
		timeBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				deviceConfirm(1); // time
				setService();
			}
		});
		devOnOffBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				deviceConfirm(2); // dev

			}
		});

	}

	private void getView() {
		deviceNameText = (TextView) findViewById(R.id.deviceNameText);
		finderBtn = (ImageButton) findViewById(R.id.finderBtn);
		registrationBtn = (ImageButton) findViewById(R.id.registrationBtn);
		timeBtn = (ImageButton) findViewById(R.id.timeBtn);
		devOnOffBtn = (ImageButton) findViewById(R.id.devOnOffBtn);
	}

	private void setService() {
		Intent intent = new Intent(this, AlarmService.class);
		getData();
		if (Constants.ALARM_STATE.equals("1")) { // alarm is turned on
			startService(intent);
		} else { // alarm is turned off
			stopService(intent);
		}
	}
}
