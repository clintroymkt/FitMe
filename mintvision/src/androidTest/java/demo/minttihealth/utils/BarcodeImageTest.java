package demo.minttihealth.utils;


import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class BarcodeImageTest {

    @Test
    public void bitmap() {
        assertNotNull(BarcodeImage.bitmap(InstrumentationRegistry.getInstrumentation().getContext(), "0"));
    }
}