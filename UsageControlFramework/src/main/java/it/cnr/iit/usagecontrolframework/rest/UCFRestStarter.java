/*******************************************************************************
 * Copyright 2018 IIT-CNR
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package it.cnr.iit.usagecontrolframework.rest;

import java.util.Collections;
import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import it.cnr.iit.ucs.configuration.UCSConfiguration;
import it.cnr.iit.usagecontrolframework.entry.UCSConfigurationLoader;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * This is the deployer for the rest api provided by the usage control framework
 *
 * @author antonio
 *
 */
@SpringBootApplication
@EnableSwagger2
@EnableAsync
public class UCFRestStarter extends SpringBootServletInitializer {

    private static final Logger log = Logger.getLogger( UCFRestStarter.class.getName() );

    /**
     * Spring boot method for configuring the current application, right now it
     * automatically scan for interfaces annotated via spring boot methods in all
     * sub classes --> CLASSES THAT ARE IN SUBPACKAGES of this class
     */
    @Override
    protected SpringApplicationBuilder configure( SpringApplicationBuilder application ) {
        return application.sources( UCFRestStarter.class );
    }

    /**
     * Docker is a SwaggerUI configuration component, in particular specifies to use
     * the V2.0 (SWAGGER_2) of swagger generated interfaces it also tells to include
     * only path that are under / if other rest interfaces are added with different
     * base path, they won't be included this path selector can be removed if all
     * interfaces should be documented.
     */
    @Bean
    public Docket documentation() {
        Docket docket = new Docket( DocumentationType.SWAGGER_2 );
        docket.apiInfo( metadata() );
        return docket.select().paths( PathSelectors.regex( "/.*" ) ).build();
    }

    /**
     * it just tells swagger that no special configuration are requested
     *
     */
    @Bean
    public UiConfiguration uiConfig() {
        return UiConfiguration.DEFAULT;
    }

    /**
     * the metadata are information visualized in the /basepath/swagger-ui.html
     * interface, only for documentation
     */
    private ApiInfo metadata() {
        return new ApiInfoBuilder().title( "Usage Control System REST API" ).description( "API for Usage Control System" )
            .version( "1.0" ).contact( "antonio.lamarra@iit.cnr.it" ).build();
    }

    public static void main( String args[] ) {
        Optional<UCSConfiguration> optConfiguration = UCSConfigurationLoader.getConfiguration();

        if( !optConfiguration.isPresent() ) {
            log.severe( UCSConfigurationLoader.CONFIG_ERR_MESSAGE );
            return;
        }

        SpringApplication app = new SpringApplication( UCFRestStarter.class );
        app.setDefaultProperties(
            Collections.singletonMap( "server.port", optConfiguration.get().getGeneral().getPort() ) );
        app.run( args );
    }

}
