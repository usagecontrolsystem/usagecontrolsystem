
package it.cnr.iit.utility;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Logger;

public class LogProfiler {

    protected Logger log = Logger.getLogger( this.getClass().getName() );
    private ArrayList<String> logList;
    private long timestamp;
    private static LogProfiler instance;

    private LogProfiler() {
        logList = new ArrayList<>();
    }

    public void log( String message ) {
        long curTimestamp = getCurrentTimestamp();
        double elapsed = ( (double) ( curTimestamp - getTimestamp() ) ) / 1000f;
        String elapsedStr = String.format( "%.2fs", elapsed );

        logList.add( message + " [ " + elapsedStr + " ]" );

        setTimestamp( curTimestamp );
    }

    public void dumpToFile( String path ) {
        Path out = Paths.get( path );

        try {
            Files.write( out, logList, Charset.defaultCharset() );
        } catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public void clearLogs() {
        logList.clear();
    }

    private long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    public long getTimestamp() {
        return timestamp;
    }

    private void setTimestamp( long timestamp ) {
        this.timestamp = timestamp;
    }

    private String ConvertSecondsToHumanReadableString( double secondtTime ) {
        TimeZone tz = TimeZone.getTimeZone( "UTC" );
        SimpleDateFormat df = new SimpleDateFormat( "HH:mm:ss" );
        df.setTimeZone( tz );
        String time = df.format( new Date( (long) ( secondtTime * 1000L ) ) );

        return time;

    }

    public synchronized static LogProfiler getInstance() {
        if( instance == null ) {
            instance = new LogProfiler();
        }
        return instance;
    }

}