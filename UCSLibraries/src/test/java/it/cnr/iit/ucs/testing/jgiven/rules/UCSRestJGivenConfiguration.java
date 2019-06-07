package it.cnr.iit.ucs.testing.jgiven.rules;

import org.springframework.http.HttpStatus;

import com.tngtech.jgiven.config.AbstractJGivenConfiguration;

public class UCSRestJGivenConfiguration extends AbstractJGivenConfiguration {

    @Override
    public void configure() {
        setFormatter( HttpStatus.class, new HttpStatusFormatter() );
    }

}
