package com.example.conorsheppard.SmartTravelCardEmulator;

import android.test.suitebuilder.annotation.SmallTest;

import com.example.conorsheppard.SmartTravelCardEmulator.cardemulation.CardService;

import junit.framework.TestCase;

public class ByteArrayToHexStringTest extends TestCase {

    private byte[] testData = "1234".getBytes();
    private String testInput = "31323334"; // 1234 as a hex string

    @SmallTest
public void testByteArrayToHexString() {

        String result = CardService.byteArrayToHexString(testData);
        assertEquals(testInput , result);
    }
}
