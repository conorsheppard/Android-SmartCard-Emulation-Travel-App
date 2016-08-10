package com.example.conorsheppard.SmartTravelCardEmulator.cardemulation;

import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;

import com.example.conorsheppard.SmartTravelCardEmulator.MainActivity;
import com.example.conorsheppard.SmartTravelCardEmulator.User;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class CardService extends HostApduService {
    private static final String TAG = "CardService";
    private static final String SAMPLE_LOYALTY_CARD_AID = "F222222222";
    private static final String CLA = "00";
    private static final String INS = "A4";
    private static final String P1_P2 = "0400";
    private static final String SELECT_APDU_HEADER = CLA+INS+P1_P2;
    private static final byte[] SELECT_OK_SW = hexStringToByteArray("9000");
    private static final byte[] UNKNOWN_CMD_SW = hexStringToByteArray("0000");

    @Override
    public void onDeactivated(int reason) {}

    // BEGIN_INCLUDE(processCommandApdu)
    @Override
    public byte[] processCommandApdu(byte[] commandApdu, Bundle extras) {
        byte[] cardId;
        byte[] returnArray;
        byte[] SELECT_APDU = buildSelectApdu(SAMPLE_LOYALTY_CARD_AID);
        Log.d(TAG, "in processCommandApdu");
        if(MainActivity.getUserLocalStore().isUserLoggedIn()) {
            User user = MainActivity.getUserLocalStore().getLoggedInUser();
            cardId = user.GetUuid().getBytes();
            returnArray = concatArrays(cardId, SELECT_OK_SW);
            Log.d(TAG, "sending to terminal = " + byteArrayToHexString(returnArray));
        } else {
            Log.d(TAG, "user not logged in ...");
            returnArray = "AAAA0000".getBytes();
        }
        return returnArray;
    }
    // END_INCLUDE(processCommandApdu)

    public static byte[] buildSelectApdu(String aid) {
        // Format: [CLASS | INSTRUCTION | PARAMETER 1 | PARAMETER 2 | LENGTH | DATA]
        Log.i("", SELECT_APDU_HEADER + String.format("%02X", aid.length() / 2) + aid);
        return hexStringToByteArray(SELECT_APDU_HEADER + String.format("%02X", aid.length() / 2) + aid);
    }

    public static String byteArrayToHexString(byte[] bytes) {
        final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        char[] hexChars = new char[bytes.length * 2]; // Each byte has two hex characters (nibbles)
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF; // Cast bytes[j] to int, treating as unsigned value
            hexChars[j * 2] = hexArray[v >>> 4]; // Select hex character from upper nibble
            hexChars[j * 2 + 1] = hexArray[v & 0x0F]; // Select hex character from lower nibble
        }
        return new String(hexChars);
    }


    public static byte[] hexStringToByteArray(String s) throws IllegalArgumentException {
        int len = s.length();
        if (len % 2 == 1) {
            throw new IllegalArgumentException("Hex string must have even number of characters");
        }
        byte[] data = new byte[len / 2]; // Allocate 1 byte per 2 hex characters
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static byte[] concatArrays(byte[] first, byte[]... rest) {
        int totalLength = first.length;
        for (byte[] array : rest) {
            totalLength += array.length;
        }
        byte[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (byte[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }
}
