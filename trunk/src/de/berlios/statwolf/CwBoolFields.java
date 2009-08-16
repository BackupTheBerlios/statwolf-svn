package de.berlios.statwolf;

import org.apache.log4j.*;

public class CwBoolFields {
	
	private static final Logger LOGGER = Logger.getLogger(CwBoolFields.class);
	
	private final static Long BITMASK_FILTER = 0x1L<<0L;
	private final static Long BITMASK_AVAILABLE = 0x1L<<1L;
	private final static Long BITMASK_ARCHIVED = 0x1L<<2L;
	private final static Long BITMASK_BUGS = 0x1L<<3L;
	private final static Long BITMASK_BLACK = 0x1L<<4L;
	private final static Long BITMASK_OWNED = 0x1L<<5L;
	private final static Long BITMASK_FOUND = 0x1L<<6L;
	private final static Long BITMASK_NEW = 0x1L<<7L;
	private final static Long BITMASK_LOGUPDATE = 0x1L<<8L;
	private final static Long BITMASK_UPDATE = 0x1L<<9L;
	private final static Long BITMASK_HTML = 0x1L<<10L;
	private final static Long BITMASK_INCOMPLETE = 0x1L<<11L;
	
	public Boolean isFiltered;
	public Boolean isAvailable;
	public Boolean isArchived;
	public Boolean hasBugs;
	public Boolean isBlack;
	public Boolean isOwned;
	public Boolean isFound;
	public Boolean isNew;
	public Boolean hasLogUpdate;
	public Boolean isUpdated;
	public Boolean isHtml;
	public Boolean isIncomplete;

	public CwBoolFields(final Long fields) {
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
