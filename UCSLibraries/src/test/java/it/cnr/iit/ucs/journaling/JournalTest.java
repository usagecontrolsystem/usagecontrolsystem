package it.cnr.iit.ucs.journaling;

import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.junit.Test;

public class JournalTest {

    JournalInterface journalInterface = new JournalInterface() {
        @Override
        public boolean init( JournalPropertiesInterface journalProperties ) {
            return true;
        }

        @Override
        public boolean logString( String string ) {
            return true;
        }
    };

    JournalPropertiesInterface fileSystem = new JournalPropertiesInterface() {
        @Override
        public String getUri() {
            return "/tmp/journalTest";
        }

        @Override
        public HashMap<String, String> getAdditionalProperties() {
            return null;
        }
    };

    JournalInterface fileSystemJournal = new JournalingFileSystem();

    public void testInit( JournalInterface journalInterface, JournalPropertiesInterface journalPropertiesInterface ) {
        assertTrue( journalInterface.init( journalPropertiesInterface ) );
    }

    public void testWrite( JournalInterface journalInterface, String message ) {
        assertTrue( journalInterface.logString( message ) );
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
    public void urlTest() throws MalformedURLException {
        String path = "file://./file.txt";
        String network = "http://ciao.com";
        URL url = new URL( path );
        url = new URL( network );
        System.out.println( url.getProtocol() );
    }

}
