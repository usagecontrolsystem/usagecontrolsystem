package iit.cnr.it.peprest.integration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.tngtech.jgiven.integration.spring.EnableJGiven;

@Configuration
@EnableJGiven
@ComponentScan(basePackages = {"iit.cnr.it.peprest"})
public class PEPRestTestContext {

}
