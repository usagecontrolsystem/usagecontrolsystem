package it.cnr.iit.usagecontrolframework.properties;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;

import it.cnr.iit.ucs.properties.components.Attribute;
import it.cnr.iit.ucs.properties.components.PipProperties;
import it.cnr.iit.ucs.properties.components.Table;

public class UCFPipProperties implements PipProperties {

    @Value( "${class-name}" )
    private String className;

    @Value( "${communication-type}" )
    private String communicationType;

    @Value( "${id}" )
    private String id;

    @Value( "${journalDir}" )
    private String journalDir;

    @Value( "#{new Boolean('${multiAttribute}')}" )
    private boolean multiAttribute;

    @Override
    public String getClassName() {
        return className;
    }

    public void setClassName( String className ) {
        this.className = className;
    }

    @Override
    public String getCommunicationType() {
        return communicationType;
    }

    public void setCommunicationType( String communicationType ) {
        this.communicationType = communicationType;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId( String id ) {
        this.id = id;
    }

    @Override
    public String getJournalDir() {
        return journalDir;
    }

    public void setJournalDir( String journalDir ) {
        this.journalDir = journalDir;
    }

    @Override
    public boolean isMultiattribute() {
        return multiAttribute;
    }

    public void setMultiAttribute( boolean multiAttribute ) {
        this.multiAttribute = multiAttribute;
    }

    @Override
    public String getRetrieval() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Attribute> getAttributes() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Table getTable() {
        // TODO Auto-generated method stub
        return null;
    }

}
