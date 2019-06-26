package it.cnr.iit.ucs.journaling;

import it.cnr.iit.ucs.properties.base.JournalProperties;

public interface JournalInterface {

    public boolean init( JournalProperties journalProperties );

    public boolean logString( String message );

}
