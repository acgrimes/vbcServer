package com.dd.vbc.enums;

import com.dd.vbc.domain.Serialization;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum ReturnCode implements Serializable {
	SUCCESS             (0, "Records retrieved successfully"),
	FAILURE             (1, "Exception occurred"),
	NO_ROWS_FOUND       (100, "No records found"),
	UNKNOWN_ERROR       (500, "Unknown error found"),
	INVALID_PARAMETERS  (501, "Invalid parameter passed"),
	UNSUPPORTED_VERSION (502, "Unsupported version");

	private final int code;
	private String message;

	ReturnCode(int code, String message) {
		this.code = code;
		this.message = message;
	}


	public int getCode() {
		return code;
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}


	public static ReturnCode fromCode(int code) {
		for(ReturnCode returnCode : values()) {
			if(returnCode.code == code) {
				return returnCode;
			}
		}
		return null;
	}

	private static final Map<Integer, ReturnCode> lookup = new HashMap<>();

	static{
		int ordinal = 0;
		for (ReturnCode returnCode : EnumSet.allOf(ReturnCode.class)) {
			lookup.put(ordinal, returnCode);
			ordinal+= 1;
		}
	}

	public static ReturnCode fromOrdinal(int ordinal) {
		return lookup.get(ordinal);
	}

	public byte[] serialize() {
		Serialization serial = new Serialization();
		byte[] returnCodeBytes = serial.serializeInt(ordinal());
		byte[] messageBytes = serial.serializeString(message);
		byte[] messageLength = serial.serializeInt(messageBytes.length);
		return serial.concatenateBytes(returnCodeBytes, messageLength, messageBytes);
	}

	public static ReturnCode deserialize(byte[] returnCode) {
		Serialization serial = new Serialization();
		int enumValue = serial.deserializeInt(returnCode);
		return ReturnCode.fromOrdinal(enumValue);
	}
}
