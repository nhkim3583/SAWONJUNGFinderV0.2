package com.nhncorp.student.sawonjungfinder.bluetooth;

import android.bluetooth.BluetoothAdapter;

public class BlueToothEnabler {

	private BluetoothAdapter bluetoothAdapter;

	public boolean enableBlueTooth() {
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter == null) {
			return false; // ������� ��������
		} else {

			if (bluetoothAdapter.isEnabled()) {
				return true; // ����
			} else {
				bluetoothAdapter.enable();
				return true; // ����

			}
		}
	}

}
