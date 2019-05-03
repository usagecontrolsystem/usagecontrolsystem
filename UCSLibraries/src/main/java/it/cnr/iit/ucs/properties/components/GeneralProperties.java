package it.cnr.iit.ucs.properties.components;

import it.cnr.iit.ucs.properties.base.JournalProperties;
import it.cnr.iit.ucs.properties.base.UriProperties;

public interface GeneralProperties extends UriProperties, JournalProperties {
    public boolean isSchedulerEnabled();
}
