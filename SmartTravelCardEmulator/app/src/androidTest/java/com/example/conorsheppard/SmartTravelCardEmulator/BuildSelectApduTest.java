package com.example.conorsheppard.SmartTravelCardEmulator;

import android.test.suitebuilder.annotation.SmallTest;

import com.example.conorsheppard.SmartTravelCardEmulator.cardemulation.CardService;

import junit.framework.TestCase;

public class BuildSelectApduTest extends TestCase{
    private String testAID = "F222222222";
    private String testResult = "00A4040005F222222222";

    @SmallTest
    public void testBuildSelectApdu() {

        byte[] result = CardService.buildSelectApdu(testAID);
        assertEquals(testResult, CardService.byteArrayToHexString(result));

    }
}
