package com.pmattioli.diffresolver.api.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.Assert;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pmattioli.diffresolver.api.model.DiffApiRequest;
import com.pmattioli.diffresolver.api.model.DiffApiResponse;
import com.pmattioli.diffresolver.service.DiffResolverService;

@RunWith(SpringRunner.class)
@WebMvcTest(BinaryDiffController.class)
public class BinaryDiffControllerTest {

    private static final String BASE_PATH = "/v1/diff/";
    private static final int DIFF_ID = 1;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DiffResolverService diffResolverService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void shouldReturnSuccesfulResponseWhenDiffResolverReturnsArrayWithValuesGreaterThanZero() throws Exception {
        DiffApiResponse response =
                readObjectFromTestResouceFile("succesfulApiResponse.json", DiffApiResponse.class);
        int[] mockDiffResolverResponseArray = getExpectedDiffResolverServiceResponse(response);
        when(diffResolverService.resolveDiff(DIFF_ID)).thenReturn(mockDiffResolverResponseArray);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + DIFF_ID).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    public void shouldReturnBothAreEqualResponseWhenDiffResolverReturnsArrayWithAllZeroValues() throws Exception {
        DiffApiResponse response =
                readObjectFromTestResouceFile("bothAreEqual.json", DiffApiResponse.class);
        int[] mockDiffResolverResponseArray = {0, 0, 0, 0, 0};
        when(diffResolverService.resolveDiff(DIFF_ID)).thenReturn(mockDiffResolverResponseArray);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + DIFF_ID).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    public void shouldReturnNotEqualSizeDataResponseWhenDataProvidedHasDifferentSizes() throws Exception {
        DiffApiResponse response =
                readObjectFromTestResouceFile("filesHaveDifferentSizesResponse.json", DiffApiResponse.class);
        when(diffResolverService.resolveDiff(DIFF_ID)).thenThrow(new IllegalArgumentException(response.getMessage()));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + DIFF_ID).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isPreconditionFailed())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    public void shouldReturnSucessfulPostResponseWhenPostedWithValidDiffIdAndData() throws Exception {
        DiffApiRequest request = readObjectFromTestResouceFile("left.json", DiffApiRequest.class);
        DiffApiResponse response =
                readObjectFromTestResouceFile("succesfulPostResult.json", DiffApiResponse.class);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH + DIFF_ID + "/left")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    public void shouldReturnBadRequestWhenPostedWithNonIntegerDiffId() throws Exception {
        DiffApiRequest request = readObjectFromTestResouceFile("left.json", DiffApiRequest.class);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH + "some_string" + "/left")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));
    }

    @Test
    public void shouldReturnBadRequestWhenGetDiffWithNonIntegerDiffId() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + "some_string")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));
    }

    private <T> T readObjectFromTestResouceFile(final String fileName, Class<T> objectType) throws IOException {
        InputStream testResourceInputStream = new ClassPathResource(fileName).getInputStream();
        return objectMapper.readerFor(objectType).readValue(testResourceInputStream);

    }

    /**
     * Returns an array such as the one the {@code DiffResolverService} would return in order for the
     * controller to produce the DiffApiResponse passed by parameter.
     *
     * <p>It's a convenience method for unit-testing the {@code BinaryDiffController} using resource files.
     * @param response the {@code DiffApiResponse} from which the expected offsets array will be  constructed
     * @return the offset array that the unit test should expect for a particular {@code DiffApiResponse} to be produced
     * @throws IOException if the ObjectMapper fails to parse the offsets string from the response
     */
    private int[] getExpectedDiffResolverServiceResponse(DiffApiResponse response) throws IOException {
        int[] expectedResponse = new int[response.getLength()];
        MappingIterator<Integer> it = objectMapper.readerFor(Integer.class).readValues(response.getOffsets());
        for (; it.hasNext(); ) {
            Integer offset = it.next();
            expectedResponse[offset-1] = offset;
        }
        return expectedResponse;
    }

}
