package com.toycode.pwmemo.nativedrivertest;

import com.google.android.testing.nativedriver.client.AndroidNativeDriver;
import com.google.android.testing.nativedriver.client.AndroidNativeDriverBuilder;

import junit.framework.TestCase;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

public class StartTest extends TestCase{
  private AndroidNativeDriver driver;

  @Override
  protected void setUp() {
    driver = getDriver();
  }

  @Override
  protected void tearDown() {
    driver.quit();
  }

  protected AndroidNativeDriver getDriver() {
    return new AndroidNativeDriverBuilder()
        .withDefaultServer()
        .build();
  }

  public void testStartActivity() {
    driver.startActivity("com.toycode.pwmemo.MainListActivity");
    WebElement ele = driver.findElement(By.id("exit_button"));
    assertEquals("Exit", ele.getText());    
  };

}
