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
package it.cnr.iit.ucsrest.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.DocExpansion;
import springfox.documentation.swagger.web.ModelRendering;
import springfox.documentation.swagger.web.OperationsSorter;
import springfox.documentation.swagger.web.TagsSorter;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * This is the deployer for the rest api provided by the usage control system
 *
 * @author Antonio La Marra
 */
@SpringBootApplication
@EnableSwagger2
@EnableAsync
@ComponentScan( basePackages = { "it.cnr.iit" } )
public class UCSRestStarter extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure( SpringApplicationBuilder application ) {
        return application.sources( UCSRestStarter.class );
    }

    @Bean
    public Docket documentation() {
        Docket docket = new Docket( DocumentationType.SWAGGER_2 );
        docket.apiInfo( metadata() );
        return docket.select().paths( PathSelectors.regex( "/.*" ) ).build();
    }

    @Bean
    public UiConfiguration uiConfig() {
        return UiConfigurationBuilder.builder()
            .deepLinking( true )
            .displayOperationId( false )
            .defaultModelsExpandDepth( 1 )
            .defaultModelExpandDepth( 1 )
            .defaultModelRendering( ModelRendering.EXAMPLE )
            .displayRequestDuration( false )
            .docExpansion( DocExpansion.NONE )
            .filter( false )
            .maxDisplayedTags( null )
            .operationsSorter( OperationsSorter.ALPHA )
            .showExtensions( false )
            .tagsSorter( TagsSorter.ALPHA )
            .supportedSubmitMethods( UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS )
            .validatorUrl( null )
            .build();
    }

    private ApiInfo metadata() {
        return new ApiInfoBuilder()
            .title( "Usage Control System REST API" )
            .description( "Usage Control System" )
            .version( "1.0" ).contact( "antonio.lamarra@iit.cnr.it" ).build();
    }

    public static void main( String[] args ) {
        SpringApplication app = new SpringApplication( UCSRestStarter.class );
        app.run( args );
    }

}
