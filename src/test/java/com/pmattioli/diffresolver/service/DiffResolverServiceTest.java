package com.pmattioli.diffresolver.service;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

import java.nio.charset.Charset;
import java.util.Base64;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@Import(DiffResolverServiceTestConfig.class)
public class DiffResolverServiceTest {

    private static final String DATA = "UGxlYXNlIGhpcmUgbWUh";
    private static final String ALTERED_DATA = "UGxlYXNlIEhpcmUgTWUh";
    private static final String DIFFERENT_SIZE_DATA = "UGxlYXNlIGhpcmUgbWUhIQ==";
    private static final String INVALID_SIZE_DATA = "UGxlYXNlIEhpcmUgTWUh==";
    private static final int DIFF_ID = 1;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    private DiffResolverService diffResolverService;

    @Test
    @DirtiesContext
    public void shouldReturnExpectedOffsetsArrayWhenBothLefAndRightDataHaveBeenSet() throws Exception {
        diffResolverService.setLeft(DIFF_ID, DATA);
        diffResolverService.setRight(DIFF_ID, ALTERED_DATA);

        assertThat(diffResolverService.resolveDiff(DIFF_ID),
                equalTo(new int[] { 0, 0, 0, 0, 0, 0, 0, 8, 0, 0, 0, 0, 13, 0, 0 }));

    }

    @Test
    @DirtiesContext
    public void shouldThrowIllegalStateExceptionWhenOnlyRightDataIsSet() throws Exception {

        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("No left side data for ID " + DIFF_ID);

        diffResolverService.setRight(DIFF_ID, DATA);

        diffResolverService.resolveDiff(DIFF_ID);

    }

    @Test
    @DirtiesContext
    public void shouldThrowIllegalStateExceptionWhenOnlyLeftDataIsSet() throws Exception {

        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("No right side data for ID " + DIFF_ID);

        diffResolverService.setLeft(DIFF_ID, DATA);

        diffResolverService.resolveDiff(DIFF_ID);

    }

    @Test
    @DirtiesContext
    public void shouldThrowIllegalStateExceptionWhenDataPostedHasDifferentSizes() throws Exception {

        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Files have different sizes");

        diffResolverService.setLeft(DIFF_ID, DATA);
        diffResolverService.setRight(DIFF_ID, DIFFERENT_SIZE_DATA);

        diffResolverService.resolveDiff(DIFF_ID);

    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenAttemptingToSetNotBase64EncodedLeftData() throws Exception {

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Data has to be Base64-encoded");

        diffResolverService.setLeft(DIFF_ID, INVALID_SIZE_DATA);

    }

    @Test
    public void shouldThrowIllegalArgumentExceptionWhenAttemptingToSetNotBase64EncodedRightData() throws Exception {

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Data has to be Base64-encoded");

        diffResolverService.setRight(DIFF_ID, INVALID_SIZE_DATA);

    }

    @Test
    public void shouldReturnDataSizedAllZeroArrayWhenSidesAreEqual() throws Exception {

        diffResolverService.setLeft(DIFF_ID, DATA);
        diffResolverService.setRight(DIFF_ID, DATA);

        byte[] decodedData = Base64.getDecoder().decode(DATA.getBytes(Charset.forName("UTF-8")));

        int[] actualOffsetsArray = diffResolverService.resolveDiff(DIFF_ID);

        assertArrayEquals(new int[decodedData.length], actualOffsetsArray);

    }


}
