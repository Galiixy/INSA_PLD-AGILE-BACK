package insa.lyon.justif.controller;

import insa.lyon.justif.generated.model.PlanningRequest;
import insa.lyon.justif.services.MapService;
import insa.lyon.justif.services.PlanningService;
import insa.lyon.justif.utils.exceptions.InvalidFileExtensionException;
import insa.lyon.justif.utils.exceptions.InvalidXMLException;
import insa.lyon.justif.utils.exceptions.ParsingXMLException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.io.FileInputStream;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Slf4j
@WebMvcTest(PlanningController.class)
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class PlanningControllerTest {

    @MockBean
    private PlanningService planningServiceMock;

    @MockBean
    private MapService mapServiceMock;

    @Autowired
    private MockMvc mockMvc;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void uploadPlanningTest() throws Exception {
        PlanningRequest planningRequest = new PlanningRequest();
        when(planningServiceMock.getPlanningRequest()).thenReturn(planningRequest);
        MockMultipartFile mockMultipartFile
                = new MockMultipartFile(
                "file",
                "requestsSmall1.xml",
                MediaType.TEXT_PLAIN_VALUE,
                new FileInputStream("src/main/resources/xml/requestsSmall1.xml")
        );

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .multipart("/planning")
                .file(mockMultipartFile);

        MvcResult result = mockMvc
                .perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assert !responseBody.equals("");

    }

    @Test
    public void uploadInvalidPlanningTest() throws Exception {
        doThrow(new InvalidXMLException("")).when(planningServiceMock).isValidPlanningXml(anyString());
        MockMultipartFile mockMultipartFile
                = new MockMultipartFile(
                "file",
                "invalidRequests.xml",
                MediaType.TEXT_PLAIN_VALUE,
                new FileInputStream("src/main/resources/xml/invalidRequests.xml")
        );

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .multipart("/planning")
                .file(mockMultipartFile);

        mockMvc
                .perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isUnprocessableEntity());
    }


    @Test
    public void uploadPlanningInvalidParsingTest() throws Exception {
        doThrow(new ParsingXMLException("")).when(planningServiceMock).parsePlanningRequestXml(anyString());
        MockMultipartFile mockMultipartFile
                = new MockMultipartFile(
                "file",
                "requestsSmall1.xml",
                MediaType.TEXT_PLAIN_VALUE,
                new FileInputStream("src/main/resources/xml/requestsSmall1.xml")
        );

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .multipart("/planning")
                .file(mockMultipartFile);

        mockMvc
                .perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isInternalServerError());

    }

    @Test
    public void uploadNotXMLPlanningTest() throws Exception {
        doThrow(new InvalidFileExtensionException("")).when(planningServiceMock).isValidPlanningXmlFileName(anyString());
        MockMultipartFile mockMultipartFile
                = new MockMultipartFile(
                "file",
                "planning.xsd",
                MediaType.TEXT_PLAIN_VALUE,
                new FileInputStream("src/main/resources/xsd/planning.xsd")
        );

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .multipart("/planning")
                .file(mockMultipartFile);

        mockMvc
                .perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());

    }


}
