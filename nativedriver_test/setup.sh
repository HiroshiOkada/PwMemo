#!/bin/sh

adb shell am instrument com.toycode.pwmemo/com.google.android.testing.nativedriver.server.ServerInstrumentation
adb forward tcp:54129 tcp:54129
sleep 1
adb logcat | grep ServerInstrumentation

