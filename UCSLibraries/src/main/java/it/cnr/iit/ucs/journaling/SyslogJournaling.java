package it.cnr.iit.ucs.journaling;

import it.cnr.iit.ucs.properties.base.JournalProperties;

public class JournalingSyslog implements JournalingInterface {

    @Override
    public boolean init( JournalProperties journalProperties ) {
        return false;
    }

    @Override
    public boolean logString( String message ) {
        return false;
    }

}
