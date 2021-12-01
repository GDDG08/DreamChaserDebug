/*
 * @Project      : RM_Infantry_Neptune_frame
 * @FilePath     : \DreamChaserDebug0\app\src\androidTest\java\com\zzh\ardunio\hc05light2\ExampleInstrumentedTest.java
 * @Descripttion : 
 * @Author       : GDDG08
 * @Date         : 2021-11-04 08:40:15
 * @LastEditors  : GDDG08
 * @LastEditTime : 2021-11-04 09:13:55
 */
package com.zzh.dreamchaser.debugBT;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.zzh.dreamchaser.debugBT", appContext.getPackageName());
    }
}