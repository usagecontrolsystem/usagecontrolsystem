package it.cnr.iit.utility;

import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import it.cnr.iit.ucs.properties.base.CommonProperties;
import it.cnr.iit.utility.errorhandling.Reject;

public class ReflectionsUtility {

    private static final Logger log = Logger.getLogger( ReflectionsUtility.class.getName() );

    private ReflectionsUtility() {}

    private static <T> Set<Class<? extends T>> getSubTypesOf( String packageName, Class<T> clazz ) {
        Collection<URL> classLoadersList = ClasspathHelper.forPackage(
            packageName, ClasspathHelper.contextClassLoader(), ClasspathHelper.staticClassLoader() );

        Reflections reflections = new Reflections( new ConfigurationBuilder()
            .setScanners( new SubTypesScanner( false ) )
            .setUrls( classLoadersList )
            .filterInputsBy( new FilterBuilder().include( FilterBuilder.prefix( packageName ) ) ) );
        return reflections.getSubTypesOf( clazz );
    }

    public static <T> Optional<Class<T>> getClassFromPackageName( String name, Class<T> clazz ) {
        try {
            return Optional.of( (Class<T>) Class.forName( name ) );
        } catch( ClassNotFoundException e ) {
            log.warning( e.getMessage() );
        }
        return Optional.empty();
    }

    private static <T> Optional<Class<T>> getClassFromSimpleName( String name, Class<T> clazz ) {
        Set<Class<? extends T>> classList = getSubTypesOf( "", clazz );
        for( Class<?> c : classList ) {
            if( c.getSimpleName().equalsIgnoreCase( name ) ) {
                log.log( Level.INFO, () -> "Match found for " + name + " : " + c.getName() );
                return Optional.of( (Class<T>) c );
            }
        }
        return Optional.empty();
    }

    public static <T> Optional<Class<T>> getClass( String name, Class<T> clazz ) {
        if( name.contains( "." ) ) {
            return getClassFromPackageName( name, clazz );
        }
        return getClassFromSimpleName( name, clazz );
    }

    public static <T> Optional<T> buildComponent( CommonProperties properties, Class<T> clazz ) {
        Reject.ifBlank( properties.getName() );
        try {
            Class<?> propClazz = properties.getClass().getInterfaces()[0]; // TODO UCS-32 NOSONAR
            Optional<Class<T>> actualClass = getClass( properties.getName(), clazz );
            if( actualClass.isPresent() ) {
                Constructor<?> constructor = actualClass.get().getConstructor( propClazz );
                T obj = (T) constructor.newInstance( properties );
                return Optional.of( obj );
            }
        } catch( Exception e ) {
            log.severe( () -> "Failed building " + properties.getName() + " : " + e.getMessage() );
        }
        return Optional.empty();
    }

}
