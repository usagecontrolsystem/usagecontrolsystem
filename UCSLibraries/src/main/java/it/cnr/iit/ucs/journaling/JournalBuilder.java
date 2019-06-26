package it.cnr.iit.ucs.journaling;

import java.util.Optional;

import it.cnr.iit.ucs.properties.base.JournalProperties;
import it.cnr.iit.utility.errorhandling.Reject;

public final class JournalBuilder {
    private JournalingInterface journalInterface;

    private JournalBuilder() {

    }

    public static Optional<JournalingInterface> build( JournalProperties journalProperties ) {
        Reject.ifNullStringArray( journalProperties.getJournalProtocol(), journalProperties.getJournalPath() );
        if( journalProperties.getJournalProtocol().equals( "file" ) ) {
            return Optional.ofNullable( buildFileJournal( journalProperties ) );
        }
        return Optional.empty();
    }

    public static JournalingInterface buildFileJournal( JournalProperties journalProperties ) {
        JournalingFileSystem journalingFileSystem = new JournalingFileSystem();
        journalingFileSystem.init( journalProperties );
        return journalingFileSystem;
    }
}
