package com.nhncorp.student.sawonjungfinder.finder;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.TextView;

import com.nhncorp.student.sawonjungfinder.R;
import com.nhncorp.student.sawonjungfinder.constants.Constants;
import com.wizturn.sdk.central.Central;
import com.wizturn.sdk.central.CentralManager;
import com.wizturn.sdk.peripheral.Peripheral;
import com.wizturn.sdk.peripheral.PeripheralScanListener;

public class FinderActivity extends Activity {

	private TextView distanceText;
	private CentralManager centralManager;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
		if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				centralManager.stopScanning();
				this.finish();
			}
		}
		return false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_finder);
		init();

	}

	private void init() {
		getView();
		setCentralManager(this.getApplicationContext());
	}

	private void setView(Double distance) {
		distanceText.setText(distance + "M");
		if (distance > 15) { // //////////
			Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			vibrator.vibrate(1000);
		} else { // //////////// // ////////////
		}

	}

	private void getView() {
		distanceText = (TextView) findViewById(R.id.distanceText);

	}

	private void setCentralManager(Context context) {
		centralManager = CentralManager.getInstance();
		centralManager.init(context);
		centralManager.setPeripheralScanListener(new PeripheralScanListener() {
			@Override
			public void onPeripheralScan(Central central,
					final Peripheral peripheral) {
				if (Constants.DEVICE_ADDRESS.equals(peripheral.getBDAddress())) {
					System.out.println("onPeripheralScan() : peripheral : "
							+ peripheral); // /////////////////////////////////////////////////////

					runOnUiThread(new Runnable() {
						public void run() {
							setView(peripheral.getDistance());
						}
					});

				}
			}

		});
		centralManager.startScanning();
	}

}
