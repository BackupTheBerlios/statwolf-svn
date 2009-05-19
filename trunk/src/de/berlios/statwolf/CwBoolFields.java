package de.berlios.statwolf;

import org.apache.log4j.*;

public class CwBoolFields {
	
	private static Logger logger = Logger.getLogger(CwBoolFields.class);
	
	final Long BITMASK_FILTER = 0x1L<<0L;
	final Long BITMASK_AVAILABLE = 0x1L<<1L;
	final Long BITMASK_ARCHIVED = 0x1L<<2L;
	final Long BITMASK_BUGS = 0x1L<<3L;
	final Long BITMASK_BLACK = 0x1L<<4L;
	final Long BITMASK_OWNED = 0x1L<<5L;
	final Long BITMASK_FOUND = 0x1L<<6L;
	final Long BITMASK_NEW = 0x1L<<7L;
	final Long BITMASK_LOGUPDATE = 0x1L<<8L;
	final Long BITMASK_UPDATE = 0x1L<<9L;
	final Long BITMASK_HTML = 0x1L<<10L;
	final Long BITMASK_INCOMPLETE = 0x1L<<11L;
	
	Boolean isFiltered;
	Boolean isAvailable;
	Boolean isArchived;
	Boolean hasBugs;
	Boolean isBlack;
	Boolean isOwned;
	Boolean isFound;
	Boolean isNew;
	Boolean hasLogUpdate;
	Boolean isUpdated;
	Boolean isHtml;
	Boolean isIncomplete;

	public CwBoolFields(Long fields) {
		isFiltered = ((fields & BITMASK_FILTER) > 0);
		isAvailable = ((fields & BITMASK_AVAILABLE) > 0);
		isArchived = ((fields & BITMASK_ARCHIVED) > 0);
		hasBugs = ((fields & BITMASK_BUGS) > 0);
		isBlack = ((fields & BITMASK_BLACK) > 0);
		isOwned = ((fields & BITMASK_OWNED) > 0);
		isFound = ((fields & BITMASK_FOUND) > 0);
		isNew = ((fields & BITMASK_NEW) > 0);
		hasLogUpdate = ((fields & BITMASK_LOGUPDATE) > 0);
		isUpdated = ((fields & BITMASK_UPDATE) > 0);
		isHtml = ( (fields & BITMASK_HTML) > 0);
		isIncomplete = ( (fields & BITMASK_INCOMPLETE) > 0);
	}

}
