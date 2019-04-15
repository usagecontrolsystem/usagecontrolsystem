package it.cnr.iit.usagecontrolframework.rest.jgiven;

import org.springframework.http.HttpStatus;

import com.tngtech.jgiven.config.AbstractJGivenConfiguration;

public class CHRestJGivenConfiguration  extends AbstractJGivenConfiguration {

    @Override
    public void configure() {
        setFormatter( HttpStatus.class, new HttpStatusFormatter() );
    }

}
