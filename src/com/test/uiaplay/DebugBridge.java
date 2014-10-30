/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.test.uiaplay;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import com.android.SdkConstants;
import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;

public class DebugBridge {
	private static AndroidDebugBridge sDebugBridge;

	@SuppressWarnings("unused")
	public static String getAdbLocation() {
		//		String toolsDir = System.getenv("SDK_TOOLS"); //$NON-NLS-1$
		// if (toolsDir == null) {
		// return null;
		// }

		String toolsDir = "sdk_tools"; //$NON-NLS-1$
		if (toolsDir == null) {
			return null;
		}

		File sdk = new File(toolsDir);
		// // System.out.println(sdk);
		//
		// // check if adb is present in platform-tools
		// File platformTools = new File(sdk, "platform-tools");
		File adb = new File(sdk, SdkConstants.FN_ADB);
		// System.out.println(adb.exists());
		if (adb.exists()) {
			return adb.getAbsolutePath();
		}

		// check if adb is present in the tools directory
		adb = new File(toolsDir, SdkConstants.FN_ADB);
		if (adb.exists()) {
			return adb.getAbsolutePath();
		}

		// check if we're in the Android source tree where adb is in
		// $ANDROID_HOST_OUT/bin/adb
		String androidOut = System.getenv("ANDROID_HOST_OUT");
		if (androidOut != null) {
			String adbLocation = androidOut + File.separator + "bin"
					+ File.separator + SdkConstants.FN_ADB;
			if (new File(adbLocation).exists()) {
				return adbLocation;
			}
		}

		return null;
	}

	public static void init() {
		String adbLocation = getAdbLocation();
		if (adbLocation != null) {
			AndroidDebugBridge.init(false /* debugger support */);
			sDebugBridge = AndroidDebugBridge.createBridge(adbLocation, false);
		}
	}

	public static void terminate() {
		if (sDebugBridge != null) {
			sDebugBridge = null;
			AndroidDebugBridge.terminate();
		}
	}

	public static boolean isInitialized() {
		return sDebugBridge != null;
	}

	public static List<IDevice> getDevices() {
		return Arrays.asList(sDebugBridge.getDevices());
	}
}
