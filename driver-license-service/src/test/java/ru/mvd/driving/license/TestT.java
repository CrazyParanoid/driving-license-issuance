package ru.mvd.driving.license;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import ru.mvd.driving.license.application.CommandProcessor;
import ru.mvd.driving.license.application.CreateDrivingLicenseCommand;
import ru.mvd.driving.license.application.Testservice;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "3600000")
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Import(EmbeddedMongoWithTransactionsConfig.class)
@ContextConfiguration(classes = {Application.class})
public class TestT {
    @Autowired
    private CommandProcessor<CreateDrivingLicenseCommand> createDrivingLicenseCommandProcessor;
    @Autowired
    private Testservice testservice;

    @Test
    public void tst(){
        testservice.test();
    }
}
