package insa.lyon.justif.controller;

import insa.lyon.justif.generated.model.Intersection;
import insa.lyon.justif.generated.model.StreetMap;
import insa.lyon.justif.services.MapService;
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
@WebMvcTest(MapController.class)
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class MapControllerTest {

    @MockBean
    private MapService mapServiceMock;

    @Autowired
    private MockMvc mockMvc;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void uploadMapTest() throws Exception {
        StreetMap streetMap = new StreetMap();
        when(mapServiceMock.getStreetMap()).thenReturn(streetMap);
        MockMultipartFile mockMultipartFile
                = new MockMultipartFile(
                "file",
                "smallMap.xml",
                MediaType.TEXT_PLAIN_VALUE,
                new FileInputStream("src/main/resources/xml/smallMap.xml")
        );

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .multipart("/map")
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
    public void uploadInvalidMapTest() throws Exception {
        doThrow(new InvalidXMLException("")).when(mapServiceMock).isValidMapXml(anyString());
        MockMultipartFile mockMultipartFile
                = new MockMultipartFile(
                "file",
                "invalidMap.xml",
                MediaType.TEXT_PLAIN_VALUE,
                new FileInputStream("src/main/resources/xml/invalidMap.xml")
        );

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .multipart("/map")
                .file(mockMultipartFile);

        mockMvc
                .perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    public void uploadNotXMLMapTest() throws Exception {
        doThrow(new InvalidFileExtensionException("")).when(mapServiceMock).isValidMapXmlFileName(anyString());
        MockMultipartFile mockMultipartFile
                = new MockMultipartFile(
                "file",
                "map.xsd",
                MediaType.TEXT_PLAIN_VALUE,
                new FileInputStream("src/main/resources/xsd/map.xsd")
        );

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .multipart("/map")
                .file(mockMultipartFile);

        mockMvc
                .perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());

    }

    @Test
    public void uploadMapInvalidParsingTest() throws Exception {
        doThrow(new ParsingXMLException("")).when(mapServiceMock).parseMapXml(anyString());
        MockMultipartFile mockMultipartFile
                = new MockMultipartFile(
                "file",
                "smallMap.xml",
                MediaType.TEXT_PLAIN_VALUE,
                new FileInputStream("src/main/resources/xml/smallMap.xml")
        );

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .multipart("/map")
                .file(mockMultipartFile);

        mockMvc
                .perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isInternalServerError());

    }

    @Test
    public void getIntersectionTest() throws Exception {

        Intersection intersection = new Intersection().id("25175791");
        doReturn(intersection).when(mapServiceMock).getIntersectionById(anyString());
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/map/intersection")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[25175791]")
                .characterEncoding("UTF-8");

        MvcResult result = mockMvc
                .perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assert responseBody.contains("25175791");

    }

    @Test
    public void getInvalidIntersectionTest() throws Exception {
        doReturn(null).when(mapServiceMock).getIntersectionById(anyString());
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/map/intersection")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[11111111]")
                .characterEncoding("UTF-8");

        mockMvc
                .perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound())
                .andReturn();
    }


}
