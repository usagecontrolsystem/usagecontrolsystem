package it.cnr.iit.ucs.journaling;

import it.cnr.iit.ucs.properties.base.JournalProperties;

public interface JournalingInterface {

    public boolean init( JournalProperties journalProperties );

    public boolean logString( String message );

    public boolean logMultipleStrings( String... strings );

}
