package it.cnr.iit.ucs.pipreader;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import it.cnr.iit.utility.Utility;
import it.cnr.iit.utility.errorhandling.Reject;

import journal.io.api.Journal;
import journal.io.api.Journal.WriteType;
import journal.io.api.JournalBuilder;

public class PIPJournalHelper {

    private static Logger log = Logger.getLogger( PIPJournalHelper.class.getName() );
    private Journal journal;

    public PIPJournalHelper( String journalDir ) {
        configureJournal( journalDir );
    }

    private boolean configureJournal( String journalDir ) {
        Reject.ifBlank( journalDir );
        try {
            File file = new File( journalDir );
            // TODO UCS-33 NOSONAR
            if( !Utility.createPathIfNotExists( file ) ) {
                return false;
            }
            journal = JournalBuilder.of( file ).open();
            return true;
        } catch( Exception e ) {
            throw new IllegalStateException( "Error while initialising the journaling dir : " + e.getMessage() );
        }
    }

    public void logReadOperation( String... string ) {
        Reject.ifNull( string );

        StringBuilder logStringBuilder = new StringBuilder();
        logStringBuilder.append( "VALUE READ: " + string[0] );

        if( string.length > 1 ) {
            logStringBuilder.append( " FOR FILTER: " + string[1] );
        }
        logStringBuilder.append( "\t AT: " + System.currentTimeMillis() );
        try {
            journal.write( logStringBuilder.toString().getBytes(), WriteType.SYNC );
        } catch( IOException e ) {
            log.severe( "Error writing journal : " + e.getMessage() );
        }
    }
}
