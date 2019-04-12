package it.cnr.iit.utility.errorhandling;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import it.cnr.iit.utility.errorhandling.exception.PreconditionException;

/**
 * Utility class to make it easier and better readable to check parameters at the beginning of methods
 *
 * Note: DO NOT REFACTOR DUPLICATED CODE IN THIS CLASS WITHOUT VERIFYING THE CODE-ANALYZE FEATURES OF IDEs.
 *
 * Methods are simplified as much as possible, so that code analyze of IDEs can detect null and other checks.
 * Methods should be simple enough for the code analyze. Avoid using of:
 *  - many method calls
 *  - enclosed if-statements
 *  - local variables
 */
public class Reject {
    private Reject() {}

    public static PreconditionException always( @NonNull String description ) {
        throw always( description, null );
    }

    public static PreconditionException alwaysBecauseOfMissingSwitchCase( @NonNull Object missingCase ) {
        throw always( "Unhandled value in switch statement: " + missingCase );
    }

    public static PreconditionException alwaysFormat( @NonNull String formatString, Object... formatArgs ) {
        throw always( formatOrNull( formatString, formatArgs ), null );
    }

    public static PreconditionException always( @NonNull String description, Throwable ex ) {
        throw new PreconditionException( description, ex );
    }

    public static void ifTrue( boolean condition ) {
        ifTrue( condition, "ifTrue" );
    }

    public static void ifTrue( boolean condition, @NonNull String description ) {
        ifBoolean( condition, false, description );
    }

    public static void ifTrueFormat( boolean condition, @NonNull String description, Object... args ) {
        ifBoolean( condition, false, description, args );
    }

    public static void ifFalse( boolean condition ) {
        ifFalse( condition, "ifFalse" );
    }

    public static void ifFalse( boolean condition, @NonNull String description ) {
        ifBoolean( condition, true, description );
    }

    public static void ifFalseFormat( boolean condition, @NonNull String description, Object... args ) {
        ifBoolean( condition, true, description, args );
    }

    private static void ifBoolean( boolean condition, boolean expected, @NonNull String description ) {
        if( condition != expected ) {
            String defaultText = expected ? ": expected true, was false" : ": expected false, was true";
            throw new PreconditionException( description + defaultText );
        }
    }

    private static void ifBoolean( boolean condition, boolean expected, @NonNull String description, Object... args ) {
        if( condition != expected ) {
            String defaultText = expected ? ": expected true, was false" : ": expected false, was true";
            throw new PreconditionException( formatOrNull( description, args ) + defaultText );
        }
    }

    public static void ifNull( Object objectToTest ) {
        ifNull( objectToTest, "ifNull" );
    }

    public static void ifNull( Object objectToTest, @NonNull String description ) {
        if( objectToTest == null ) {
            throw new PreconditionException( description + ": expected to be not null, but is null" );
        }
    }

    public static void ifNullFormat( Object objectToTest, @NonNull String description, Object... formatArgs ) {
        if( objectToTest == null ) {
            throw new PreconditionException( formatOrNull( description, formatArgs ) + ": expected to be not null, but is null" );
        }
    }

    public static <T> void ifElementIsNull( Iterable<T> objectsToTest ) {
        for( Object objectToTest : objectsToTest ) {
            ifNull( objectToTest );
        }
    }

    public static void ifElementIsNull( Object... objectsToTest ) {
        for( Object objectToTest : objectsToTest ) {
            ifNull( objectToTest );
        }
    }

    public static void ifNotNull( Object objectToTest ) {
        ifNotNull( objectToTest, "ifNotNull" );
    }

    public static void ifNotNull( Object objectToTest, @NonNull String description ) {
        if( objectToTest != null ) {
            throw new PreconditionException( description + ": expected to be null, but is non-null" );
        }
    }

    public static <T> T checkNotNull( T objectToTest ) {
        ifNull( objectToTest );
        return objectToTest;
    }

    public static void ifBlank( String stringToTest ) {
        ifBlank( stringToTest, "ifBlank" );
    }

    public static void ifBlank( String stringToTest, @NonNull String description ) {
        ifTrue( StringUtils.isEmpty( stringToTest ), description );
    }

    public static <T> void ifEquals( T actual, @NonNull T unexpected ) {
        ifEquals( actual, unexpected, "ifEquals" );
    }

    public static <T> void ifEquals( T actual, @NonNull T unexpected, @NonNull String description ) {
        if( unexpected.equals( actual ) ) {
            throw new PreconditionException( description + ": expected anything but <" + unexpected + ">, but was <" + actual + ">" );
        }
    }

    public static <T> void ifNotEquals( T actual, @NonNull T expected ) {
        ifNotEquals( actual, expected, "ifNotEquals" );
    }

    public static <T> void ifNotEquals( T actual, @NonNull T expected, @NonNull String description ) {
        if( !expected.equals( actual ) ) {
            throw new PreconditionException( description + ": expected <" + expected + "> but was <" + actual + ">" );
        }
    }

    public static <T> void ifEmpty( T[] array, @NonNull String description ) {
        if( array == null || array.length == 0 ) {
            throw new PreconditionException( description );
        }
    }

    public static void ifEmpty( Map<?, ?> map, @NonNull String description ) {
        if( map == null || map.isEmpty() ) {
            throw new PreconditionException( description );
        }
    }

    public static void ifEmpty( Collection<?> collection ) {
        ifEmpty( collection, "ifEmptyCollection" );
    }

    public static void ifEmpty( Map<?, ?> map ) {
        ifEmpty( map, "ifEmptyMap" );
    }

    public static void ifEmpty( Collection<?> collection, @NonNull String description ) {
        if( collection == null || collection.isEmpty() ) {
            throw new PreconditionException( description );
        }
    }

    public static <T> void ifNotEmpty( T[] array, @NonNull String description ) {
        if( array == null || array.length != 0 ) {
            throw new PreconditionException( description );
        }
    }

    public static void ifNotEmpty( Collection<?> collection, @NonNull String description ) {
        if( collection == null || !collection.isEmpty() ) {
            throw new PreconditionException( description );
        }
    }

    public static <T> void ifIn( @NonNull Collection<T> collection, T item, @NonNull String description ) {
        ifTrue( collection.contains( item ), description );
    }

    public static <T> void ifNotIn( @NonNull Collection<T> collection, T item ) {
        ifNotIn( collection, item, "ifNotIn" );
    }

    public static <T> void ifNotIn( @NonNull Collection<T> collection, T item, @NonNull String description ) {
        ifNotInFormat( collection, item, description );
    }

    public static <T> void ifNotInFormat( @NonNull Collection<T> collection, T item, @NonNull String description, Object... args ) {
        ifFalseFormat( collection.contains( item ), description, args );
    }

    public static void ifNegative( long value, @NonNull String description ) {
        ifLessThan( value, 0, description );
    }

    public static void ifLessThan( long value, long minValue ) {
        ifLessThan( value, minValue, "Expected a value >= " + minValue + ", but got " + value );
    }

    public static void ifLessThan( long value, long minValue, @NonNull String description ) {
        if( value < minValue ) {
            throw new PreconditionException( description );
        }
    }

    public static void ifGreaterThan( long value, long maxValue, @NonNull String description ) {
        if( value > maxValue ) {
            throw new PreconditionException( description );
        }
    }

    public static void ifGreaterThanFormat( long value, long maxValue, @NonNull String description, Object... formatArgs ) {
        if( value > maxValue ) {
            throw new PreconditionException( formatOrNull( description, formatArgs ) );
        }
    }

    public static void ifPresent( @NonNull Optional<?> optional ) {
        ifPresent( optional, "ifPresent" );
    }

    public static void ifPresent( @NonNull Optional<?> optional, @NonNull String description ) {
        ifTrue( optional.isPresent(), description );
    }

    public static void ifAbsent( @NonNull Optional<?> optional ) {
        ifAbsent( optional, "ifAbsent" );
    }

    public static void ifAbsent( @NonNull Optional<?> optional, @NonNull String description ) {
        ifFalse( optional.isPresent(), description );
    }

    public static void ifAbsentFormat( Optional<?> optional, String description, Object... args ) {
        ifFalseFormat( optional.isPresent(), description, args );
    }

    private static String formatOrNull( String formatString, Object[] formatArgs ) {
        if( formatString == null || formatArgs == null || formatArgs.length == 0 ) {
            return formatString;
        }
        return String.format( formatString, formatArgs );
    }

    public static void ifInvalidObjectState( boolean initialized, String objectName, Logger log ) {
        if( !initialized ) {
            log.log( Level.SEVERE, "Object {0} not initialized", objectName );
            throw new IllegalStateException( "Object " + objectName + " not initialized" );
        }
    }

    public static void ifNull( Object... objects ) {
        for( Object object : objects ) {
            ifNull( object );
        }
    }

    public static void ifNull( String... objects ) {
        for( String object : objects ) {
            ifNull( object );
        }
    }
}
