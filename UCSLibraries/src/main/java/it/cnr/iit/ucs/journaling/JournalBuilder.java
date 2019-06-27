package it.cnr.iit.ucs.journaling;

import java.util.logging.Logger;

import it.cnr.iit.ucs.properties.base.JournalProperties;

public final class JournalBuilder {

    private static final Logger log = Logger.getLogger( JournalBuilder.class.getName() );
    private JournalingInterface journalInterface;

    private JournalBuilder() {

    }

    public static JournalingInterface build( JournalProperties journalProperties ) {
        if( journalProperties.getJournalProtocol() != null && journalProperties.getJournalPath() != null ) {
            if( journalProperties.getJournalProtocol().equals( "file" ) ) {
                return buildFileJournal( journalProperties );
            }
            if( journalProperties.getJournalProtocol().equals( "syslog" ) ) {
                return new SyslogJournaling();
            }
        }
        log.warning( "No journaling " );
        return new FileJournaling();
    }

    public static JournalingInterface buildFileJournal( JournalProperties journalProperties ) {
        FileJournaling journalingFileSystem = new FileJournaling();
        journalingFileSystem.init( journalProperties );
        return journalingFileSystem;
    }
}
