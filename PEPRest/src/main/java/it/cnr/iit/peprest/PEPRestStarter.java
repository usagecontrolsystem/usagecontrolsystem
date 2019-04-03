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
package it.cnr.iit.peprest;

import java.util.Collections;
import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import it.cnr.iit.peprest.configuration.PEPRestConfiguration;
import it.cnr.iit.peprest.configuration.PEPRestConfigurationLoader;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class PEPRestStarter extends SpringBootServletInitializer {

    private static final Logger LOG = Logger.getLogger( PEPRestStarter.class.getName() );

    /**
     * Spring boot method for configuring the current application, right now it
     * automatically scan for interfaces annotated via spring boot methods in all
     * sub classes --> CLASSES THAT ARE IN SUBPACKAGES of this class
     */
    @Override
    protected SpringApplicationBuilder configure(
            SpringApplicationBuilder application ) {
        return application.sources( PEPRestStarter.class );
    }

    /**
     * Docker is a SwaggerUI configuration component, in particular specifies to
     * use the V2.0 (SWAGGER_2) of swagger generated interfaces it also tells to
     * include only path that are under / if other rest interfaces are added with
     * different base path, they won't be included this path selector can be
     * removed if all interfaces should be documented.
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
        return new ApiInfoBuilder().title( "PEP REST API" ).description( "API for PEP" )
            .version( "1.0" ).contact( "antonio.lamarra@iit.cnr.it" ).build();
    }

    public static void main( String[] args ) {
        // TODO use spring beans and load the config only once
        Optional<PEPRestConfiguration> optConfiguration = PEPRestConfigurationLoader.getConfiguration();

        if( !optConfiguration.isPresent() ) {
            LOG.severe( PEPRestConfigurationLoader.CONFIG_ERR_MESSAGE );
            return;
        }

        PEPRestConfiguration configuration = optConfiguration.get();

        SpringApplication app = new SpringApplication( PEPRestStarter.class );
        app.setDefaultProperties( Collections.singletonMap( "server.port", configuration.getPep().getPort() ) );
        app.run( args );
    }

}