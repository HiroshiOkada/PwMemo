
package com.toycode.pwmemo.nativedrivertest;

import com.google.android.testing.nativedriver.client.AdbConnection;
import com.google.android.testing.nativedriver.client.AdbConnectionBuilder;
import com.google.android.testing.nativedriver.client.AndroidNativeDriver;
import com.google.android.testing.nativedriver.client.AndroidNativeDriverBuilder;
import com.google.android.testing.nativedriver.client.AndroidNativeElement;
import com.google.android.testing.nativedriver.client.ClassNames;
import com.google.android.testing.nativedriver.common.AndroidKeys;
import com.google.android.testing.nativedriver.common.AndroidNativeBy;

import org.openqa.selenium.Keyboard;

import junit.framework.TestCase;

public class DeclareTest extends TestCase {
    private AndroidNativeDriver driver;

    static final String ADB_PATH = "/home/hiroshi/android-sdk-linux_x86/platform-tools/adb";

    public void testStartActivity() {
        driver.startActivity("com.toycode.pwmemo.MainListActivity");
        Keyboard keyboard = driver.getKeyboard();
        // menu -> "setting ..." 
        keyboard.sendKeys(AndroidKeys.MENU);
        try {
            AndroidNativeElement setting = driver.findElement(AndroidNativeBy.partialText("setting ..."));
            assertNotNull(setting);
            setting.click();
        } catch (org.openqa.selenium.NoSuchElementException e) {
            fail(e.toString());
        }
        // "Delete All"
        try {
            AndroidNativeElement deleteall = driver.findElement(AndroidNativeBy.partialText("Delete All"));
            assertNotNull(deleteall);
            deleteall.click();
        } catch (org.openqa.selenium.NoSuchElementException e) {
            fail(e.toString());
        }
        //
        Helper.Wait1sec();
        // check "Delete all password and data." and OK
        try {
            AndroidNativeElement check = driver.findElement(AndroidNativeBy.className(ClassNames.CHECKBOX));
            assertNotNull(check);            
            check.click();            
            AndroidNativeElement ok = driver.findElement(AndroidNativeBy.text("OK"));
            assertNotNull(ok);
            ok.click();
        } catch (org.openqa.selenium.NoSuchElementException e) {
            fail(e.toString());
        }

    };
    
    @Override
    protected void setUp() {
        driver = getDriver();
    }

    @Override
    protected void tearDown() {
        driver.quit();
    }

    protected AndroidNativeDriver getDriver() {
        AdbConnection adbc = new AdbConnectionBuilder()
                .withAdbPath(ADB_PATH)
                .build();
        return new AndroidNativeDriverBuilder()
                .withDefaultServer()
                .withAdbConnection(adbc)
                .build();
    }

}
