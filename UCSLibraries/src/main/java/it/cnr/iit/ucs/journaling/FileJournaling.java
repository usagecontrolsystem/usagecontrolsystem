package it.cnr.iit.ucs.journaling;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import it.cnr.iit.ucs.properties.base.JournalProperties;
import it.cnr.iit.utility.FileUtility;
import it.cnr.iit.utility.errorhandling.Reject;

import journal.io.api.Journal;
import journal.io.api.Journal.WriteType;
import journal.io.api.JournalBuilder;

public class JournalingFileSystem implements JournalingInterface {

    private static final Logger log = Logger.getLogger( JournalingFileSystem.class.getName() );
    private Journal journal;

    @Override
    public boolean init( JournalProperties journalProperties ) {
        Reject.ifNull( journalProperties );
        Reject.ifBlank( journalProperties.getJournalPath() );
        try {
            File file = new File( journalProperties.getJournalPath() );
            // TODO UCS-33 NOSONAR
            if( !FileUtility.createPathIfNotExists( file ) ) {
                return false;
            }
            journal = JournalBuilder.of( file ).open();
            return true;
        } catch( Exception e ) {
            throw new IllegalStateException( "Error while initialising the journaling dir : " + e.getMessage() );
        }
    }

    @Override
    public boolean logString( String message ) {
        Reject.ifBlank( message );

        StringBuilder logStringBuilder = new StringBuilder();
        logStringBuilder.append( message );

        logStringBuilder.append( "\ttime: " + System.currentTimeMillis() );
        try {
            journal.write( logStringBuilder.toString().getBytes(), WriteType.SYNC );
            return true;
        } catch( IOException e ) {
            log.severe( "Error writing journal : " + e.getMessage() );
            return false;
        }
    }

}
