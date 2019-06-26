package it.cnr.iit.ucs.journaling;

import java.util.Optional;
import java.util.logging.Logger;

import it.cnr.iit.ucs.properties.base.JournalProperties;

public final class JournalBuilder {

    private static final Logger log = Logger.getLogger( JournalBuilder.class.getName() );
    private JournalingInterface journalInterface;

    private JournalBuilder() {

    }

    public static Optional<JournalingInterface> build( JournalProperties journalProperties ) {
        if( journalProperties.getJournalProtocol() != null && journalProperties.getJournalPath() != null ) {
            if( journalProperties.getJournalProtocol().equals( "file" ) ) {
                return Optional.ofNullable( buildFileJournal( journalProperties ) );
            }
            if( journalProperties.getJournalProtocol().equals( "syslog" ) ) {
                return Optional.ofNullable( new JournalingSyslog() );
            }
        }
        log.warning( "No journaling " );
        return Optional.empty();
    }

    public static JournalingInterface buildFileJournal( JournalProperties journalProperties ) {
        JournalingFileSystem journalingFileSystem = new JournalingFileSystem();
        journalingFileSystem.init( journalProperties );
        return journalingFileSystem;
    }
}
