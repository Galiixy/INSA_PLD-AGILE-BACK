package insa.lyon.justif.controller;

import insa.lyon.justif.generated.model.Tour;
import insa.lyon.justif.services.PlanningService;
import insa.lyon.justif.services.TourService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(TourController.class)
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class TourControllerTest {

    @MockBean
    private TourService tourServiceMock;

    @MockBean
    private PlanningService planningServiceMock;

    @Autowired
    private MockMvc mockMvc;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getTourTest() throws Exception {
        doReturn(new Tour()).when(tourServiceMock).computeOptimizedTour(any(Tour.class), anyString());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get("/tour/any-strategy");

        mockMvc
                .perform(request)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();
    }


}
