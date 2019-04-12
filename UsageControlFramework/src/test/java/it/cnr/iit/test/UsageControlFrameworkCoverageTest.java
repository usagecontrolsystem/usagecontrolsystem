package it.cnr.iit.test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@ActiveProfiles( "test" )
@SpringBootTest
@RunWith( SpringRunner.class )
public class UsageControlFrameworkCoverageTest extends UCFBaseTests {
    @PostConstruct
    private void init() throws JAXBException, URISyntaxException, IOException {
        log.info( "Init tests" );
    }

    @Before
    public void setUp() {
        log.info( "setUp >>>>>>>>>>>>>>>>>>" );
        // nothing to do for now
    }

    @Test
    public void usageControlFrameworkCoverageTest()
            throws JAXBException, URISyntaxException, IOException, NoSuchMethodException, SecurityException,
            InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        // UsageControlFramework usageControlFramework = new UsageControlFramework();
    }

}
