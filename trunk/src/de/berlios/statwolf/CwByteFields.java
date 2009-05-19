package de.berlios.statwolf;

import org.apache.log4j.*;

public class CwByteFields {
	
	private static Logger logger = Logger.getLogger(CwByteFields.class);
	
	final Byte BYTEOFFSET_DIFF = 0;
	final Byte BYTEOFFSET_TERR = 1;
	final Byte BYTEOFFSET_TYPE = 2;
	final Byte BYTEOFFSET_SIZE = 3;
	final Byte BYTEOFFSET_DNFLOGS = 4;
	
	Integer difficulty;
	Integer terrain;
	Integer cacheType;
	Integer cacheSize;
	Integer dnfLogs;

	public CwByteFields(Long fields) {
		difficulty = extractByteFromLong(fields, BYTEOFFSET_DIFF);
		terrain = extractByteFromLong(fields, BYTEOFFSET_TERR);
		cacheType = extractByteFromLong(fields, BYTEOFFSET_TYPE);
		cacheSize = extractByteFromLong(fields, BYTEOFFSET_SIZE);
		dnfLogs = extractByteFromLong(fields, BYTEOFFSET_DNFLOGS);
	}
	
	private Integer extractByteFromLong(Long value, Byte offset) {
		Long mask = 0xFFL << ((long)offset*8L);
		Long tmpval = value & mask;
		Long ret = (tmpval >>> ((long)offset*8L));
		return ret.intValue();
	}
}
