package com.pmattioli.diffresolver.integration;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pmattioli.diffresolver.api.model.DiffApiRequest;
import com.pmattioli.diffresolver.api.model.DiffApiResponse;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BinaryDiffApiIntegrationTest {

    private static final int DIFF_ID = 1;

    @LocalServerPort
    private int port;

    private URL base;

    @Autowired
    private TestRestTemplate template;

    @Autowired
    ObjectMapper objectMapper;

    @Before
    public void setUp() throws Exception {
        this.base = new URL("http://localhost:" + port + "/v1/diff/");
    }

    @Test
    @DirtiesContext
    public void getDiffSucceedsWhenBothSidesArePosted() throws Exception {

        post(DIFF_ID + "/left", "left.json");
        post(DIFF_ID + "/right", "right.json");

        ResponseEntity<String> response = template.getForEntity(base.toString() + DIFF_ID, String.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        assertResponseEqualsJsonFile(response, "succesfulApiResponse.json");

    }

    @Test
    @DirtiesContext
    public void getDiffProducesSidesAreEqualMessageWhenBothSidesArePostedWithTheSameData() throws Exception {

        post(DIFF_ID + "/left", "left.json");
        post(DIFF_ID + "/right", "left.json");

        ResponseEntity<String> response = template.getForEntity(base.toString() + DIFF_ID, String.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        assertResponseEqualsJsonFile(response, "bothAreEqual.json");

    }

    @Test
    @DirtiesContext
    public void getDiffProducesSidesAreNotEqualSizeMessageWhenBothSidesArePostedWithDifferentSizeData() throws Exception {

        post(DIFF_ID + "/left", "left.json");
        post(DIFF_ID + "/right", "notEqualSizeData.json");

        ResponseEntity<String> response = template.getForEntity(base.toString() + DIFF_ID, String.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.PRECONDITION_FAILED));
        assertResponseEqualsJsonFile(response, "filesHaveDifferentSizesResponse.json");

    }

    @Test
    @DirtiesContext
    public void getDiffFailsWhenOnlyLeftSideIsPosted() throws Exception {

        post(DIFF_ID + "/left", "left.json");

        ResponseEntity<String> response = template.getForEntity(base.toString() + DIFF_ID, String.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.PRECONDITION_FAILED));
        assertResponseEqualsJsonFile(response, "noRightSideData.json");

    }

    @Test
    @DirtiesContext
    public void getDiffFailsWhenOnlyRightSideIsPosted() throws Exception {

        post(DIFF_ID + "/right", "left.json");

        ResponseEntity<String> response = template.getForEntity(base.toString() + DIFF_ID, String.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.PRECONDITION_FAILED));
        assertResponseEqualsJsonFile(response, "noLeftSideData.json");

    }

    @Test
    public void getDiffFailsWhenNoDatahasBeenPosted() throws Exception {

        ResponseEntity<String> response = template.getForEntity(base.toString() + DIFF_ID, String.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.PRECONDITION_FAILED));
        assertResponseEqualsJsonFile(response, "diffDoesNotExist.json");

    }

    private void post(final String path, final String requestFileName) throws Exception {

        ResponseEntity<String> response = template.postForEntity(base.toString() + path,
                getJsonRequestEntityFromResource(requestFileName), String.class);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        assertResponseEqualsJsonFile(response, "succesfulPostResult.json");

    }

    private void assertResponseEqualsJsonFile(ResponseEntity<String> response, final String fileName) throws IOException {

        DiffApiResponse actual = objectMapper.readValue(response.getBody(), DiffApiResponse.class);
        DiffApiResponse expected = objectMapper.readValue(new ClassPathResource(fileName).getInputStream(),
                DiffApiResponse.class);

        assertThat(actual, equalTo(expected));

    }

    private HttpEntity<DiffApiRequest> getJsonRequestEntityFromResource(String fileName) throws IOException {

        DiffApiRequest body = objectMapper.readValue(new ClassPathResource(fileName).getInputStream(),
                DiffApiRequest.class);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);

        return new HttpEntity<>(body, headers);

    }

}
