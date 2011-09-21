package com.toycode.pwmemo.nativedrivertest;

import com.google.android.testing.nativedriver.client.AndroidNativeDriver;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Helper {
    static final String TMP_DIR = System.getProperty("java.io.tmpdir");

    public static void TakeScreenShot(AndroidNativeDriver driver, String name) {
        byte[] ss = driver.getScreenshotAs(OutputType.BYTES);
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(new File(TMP_DIR, name));
            fos.write(ss);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static WebElement waitFindElementByText(AndroidNativeDriver driver, String text) {
        for ( int i=0; i<5; i++){
            Wait1sec();
            WebElement ret = driver.findElementByText(text);
            if (ret != null) {
                return ret;
            }
        }
        throw new RuntimeException("Timeout");
    }
   
    public static void Wait1sec() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
    }
}
