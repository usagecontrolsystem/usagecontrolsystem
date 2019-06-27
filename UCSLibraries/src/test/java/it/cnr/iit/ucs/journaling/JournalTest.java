package it.cnr.iit.ucs.journaling;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.junit.Test;

import it.cnr.iit.ucs.properties.base.JournalProperties;

public class JournalTest {

    JournalingInterface journalInterface = new JournalingInterface() {
        @Override
        public boolean init( JournalProperties journalProperties ) {
            return true;
        }

        @Override
        public boolean logString( String string ) {
            return true;
        }

        @Override
        public boolean logMultipleStrings( String... strings ) {
            return true;
        }
    };

    JournalProperties fileSystem = new JournalProperties() {
        @Override
        public String getJournalPath() {
            return "/tmp/journalTest";
        }

        @Override
        public HashMap<String, String> getJournalAdditionalProperties() {
            return null;
        }

        @Override
        public String getJournalProtocol() {
            return "file";
        }
    };

    JournalingInterface fileSystemJournal = new FileJournaling();

    public void testInit( JournalingInterface journalInterface, JournalProperties journalPropertiesInterface ) {
        assertTrue( journalInterface.init( journalPropertiesInterface ) );
    }

    public void testWrite( JournalingInterface journalInterface, String message ) {
        assertTrue( journalInterface.logString( message ) );
    }

    public void testWriteMultiple( JournalingInterface journalInterface, String... message ) {
        assertTrue( journalInterface.logMultipleStrings( message ) );
    }

    @Test
    public void abstractTest() {
        testInit( journalInterface, null );
        testWrite( journalInterface, null );
    }

    @Test
    public void testFSjournal() {
        testInit( fileSystemJournal, fileSystem );
        testWrite( fileSystemJournal, "HELLO" );
    }

    @Test
    public void testFSjournalBuilder() {
        JournalingInterface journalInterface = JournalBuilder.build( fileSystem );
        testInit( journalInterface, fileSystem );
        testWrite( journalInterface, "HELLO" );
        testWriteMultiple( journalInterface, "Ciao", "Hello", "Ciao", "Hello" );
    }

}
