package ru.mvd.driving.license.input;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.mvd.driving.license.AbstractTest;
import ru.mvd.driving.license.Application;
import ru.mvd.driving.license.application.*;
import ru.mvd.driving.license.domain.TestDomainObjectsFactory;
import ru.mvd.driving.license.domain.model.DrivingLicense;
import ru.mvd.driving.license.domain.model.DrivingLicenseId;
import ru.mvd.driving.license.domain.model.PersonId;

import static ru.mvd.driving.license.TestValues.*;

@ActiveProfiles("test")
@WebAppConfiguration
@ContextConfiguration(classes = {Application.class})
public class DrivingLicenseControllerTest extends AbstractTest {
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private TestCommandFactory testCommandFactory;
    @Autowired
    private TestDomainObjectsFactory testDomainObjectsFactory;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext)
                .build();
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule());
    }

    @Test
    @SneakyThrows
    public void testPostIssueDrivingLicenseCommand() {
        IssueDrivingLicenseCommand command = testCommandFactory.createIssueDrivingLicenseCommand();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/driving-licenses")
                .content(objectMapper.writeValueAsString(command))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        DrivingLicenseResponse response = objectMapper.readValue(content, DrivingLicenseResponse.class);
        Assert.assertEquals(mvcResult.getResponse().getContentType(), "application/hal+json");
        Assert.assertEquals(mvcResult.getResponse().getStatus(), HttpStatus.CREATED.value());
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getId(), SERIES + NUMBER);
    }

    @Test
    @SneakyThrows
    public void testDeduplication() {
        IssueDrivingLicenseCommand command = testCommandFactory.createIssueDrivingLicenseCommand();
        DrivingLicense drivingLicense = testDomainObjectsFactory.newDrivingLicense();
        Mockito.when(drivingLicenseRepository.findByPersonId(ArgumentMatchers.any(PersonId.class)))
                .thenReturn(drivingLicense);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/driving-licenses")
                .content(objectMapper.writeValueAsString(command))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        Assert.assertEquals(mvcResult.getResponse().getContentType(), "text/plain;charset=UTF-8");
        Assert.assertEquals(mvcResult.getResponse().getStatus(), HttpStatus.BAD_REQUEST.value());
        Assert.assertEquals(content, "The person with id 258890 already has driving license");
    }

    @Test
    @SneakyThrows
    public void testPostInvalidIssueDrivingLicenseCommand() {
        IssueDrivingLicenseCommand command = testCommandFactory.createInvalidIssueDrivingLicenseCommand();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/driving-licenses")
                .content(objectMapper.writeValueAsString(command))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        Assert.assertEquals(mvcResult.getResponse().getContentType(), "text/plain;charset=UTF-8");
        Assert.assertEquals(mvcResult.getResponse().getStatus(), HttpStatus.BAD_REQUEST.value());
        Assert.assertTrue(content.contains("personId can't be null or empty")
                && content.contains("attachments can't be empty"));
    }

    @Test
    @SneakyThrows
    public void testPostRevokeDrivingLicenseCommand(){
        DrivingLicense drivingLicense = testDomainObjectsFactory.newDrivingLicense();
        Mockito.when(drivingLicenseRepository.findByDrivingLicenseId(ArgumentMatchers.any(DrivingLicenseId.class)))
                .thenReturn(drivingLicense);
        RevokeDrivingLicenseCommand command = testCommandFactory.createRevokeDrivingLicenseCommand();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/driving-licenses/revocation")
                .content(objectMapper.writeValueAsString(command))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        DrivingLicenseResponse response = objectMapper.readValue(content, DrivingLicenseResponse.class);
        Assert.assertEquals(mvcResult.getResponse().getContentType(), "application/hal+json");
        Assert.assertEquals(mvcResult.getResponse().getStatus(), HttpStatus.OK.value());
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getId(), SERIES + NUMBER);
    }

    @Test
    @SneakyThrows
    public void testPostProlongRevocationCommand(){
        DrivingLicense drivingLicense = testDomainObjectsFactory.newDrivingLicense();
        drivingLicense.revoke(REVOCATION_END_DATE, JUDGMENT_FILE_ID);
        Mockito.when(drivingLicenseRepository.findByDrivingLicenseId(ArgumentMatchers.any(DrivingLicenseId.class)))
                .thenReturn(drivingLicense);
        ProlongRevocationCommand command = testCommandFactory.createProlongRevocationCommand();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/driving-licenses/revocation/prolong")
                .content(objectMapper.writeValueAsString(command))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        DrivingLicenseResponse response = objectMapper.readValue(content, DrivingLicenseResponse.class);
        Assert.assertEquals(mvcResult.getResponse().getContentType(), "application/hal+json");
        Assert.assertEquals(mvcResult.getResponse().getStatus(), HttpStatus.OK.value());
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getId(), SERIES + NUMBER);
    }

    @Test
    @SneakyThrows
    public void testPostProlongRevocationCommandForNotRevokedDrivingLicense(){
        DrivingLicense drivingLicense = testDomainObjectsFactory.newDrivingLicense();
        Mockito.when(drivingLicenseRepository.findByDrivingLicenseId(ArgumentMatchers.any(DrivingLicenseId.class)))
                .thenReturn(drivingLicense);
        ProlongRevocationCommand command = testCommandFactory.createProlongRevocationCommand();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/driving-licenses/revocation/prolong")
                .content(objectMapper.writeValueAsString(command))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        Assert.assertEquals(mvcResult.getResponse().getContentType(), "text/plain;charset=UTF-8");
        Assert.assertEquals(mvcResult.getResponse().getStatus(), HttpStatus.BAD_REQUEST.value());
        Assert.assertEquals(content, "Wrong invocation for current state");
    }

    @Test
    @SneakyThrows
    public void testDisableDrivingLicenseCommand(){
        DrivingLicense drivingLicense = testDomainObjectsFactory.newDrivingLicense();
        Mockito.when(drivingLicenseRepository.findByDrivingLicenseId(ArgumentMatchers.any(DrivingLicenseId.class)))
                .thenReturn(drivingLicense);
        DisableDrivingLicenseCommand command = testCommandFactory.createDisableDrivingLicenseCommand();

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/driving-licenses/disable")
                .content(objectMapper.writeValueAsString(command))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        String content = mvcResult.getResponse().getContentAsString();
        DrivingLicenseResponse response = objectMapper.readValue(content, DrivingLicenseResponse.class);
        Assert.assertEquals(mvcResult.getResponse().getContentType(), "application/hal+json");
        Assert.assertEquals(mvcResult.getResponse().getStatus(), HttpStatus.OK.value());
        Assert.assertNotNull(response);
        Assert.assertEquals(response.getId(), SERIES + NUMBER);
    }

}
