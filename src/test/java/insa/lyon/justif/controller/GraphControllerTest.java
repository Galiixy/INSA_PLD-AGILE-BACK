package insa.lyon.justif.controller;

import insa.lyon.justif.generated.model.PlanningRequest;
import insa.lyon.justif.generated.model.StreetMap;
import insa.lyon.justif.services.GraphService;
import insa.lyon.justif.services.MapService;
import insa.lyon.justif.services.PlanningService;
import insa.lyon.justif.utils.algo.dijkstra.Graph;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(GraphController.class)
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class GraphControllerTest {

    @MockBean
    private GraphService graphServiceMock;

    @MockBean
    private MapService mapServiceMock;

    @MockBean
    private PlanningService planningServiceMock;


    @Autowired
    private MockMvc mockMvc;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    public void generateGraphTest() throws Exception {
        doReturn(new StreetMap()).when(mapServiceMock).getStreetMap();
        doNothing().when(graphServiceMock).setStreetMap(any(StreetMap.class));
        when(graphServiceMock.generateGraph()).thenReturn(new Graph());
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/graph");

        mockMvc
                .perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void shortestPathsGraphTest() throws Exception {
        when(graphServiceMock.computeShortestPaths(any(PlanningRequest.class))).thenReturn(new HashMap<>());
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post("/graph/shortest-paths");

        mockMvc
                .perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
    }

}
