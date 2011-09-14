package com.toycode.pwmemo.nativedrivertest;

import com.google.android.testing.nativedriver.client.AdbConnection;
import com.google.android.testing.nativedriver.client.AdbConnectionBuilder;
import com.google.android.testing.nativedriver.client.AndroidNativeDriver;
import com.google.android.testing.nativedriver.client.AndroidNativeDriverBuilder;


import junit.framework.TestCase;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class StartTest extends TestCase{
  private AndroidNativeDriver driver;

  static final String ADB_PATH = "/home/hiroshi/android-sdk-linux_x86/platform-tools/adb";
  static final String PNG_PATH = "/home/hiroshi/screenshot.png";
  
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

    public void testStartActivity() {
        driver.startActivity("com.toycode.pwmemo.MainListActivity");

        byte[] ss = driver.getScreenshotAs(OutputType.BYTES);
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(PNG_PATH);
            fos.write(ss);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        WebElement ele = driver.findElement(By.id("exit_button"));
        assertEquals("Exit", ele.getText());
  };

}
