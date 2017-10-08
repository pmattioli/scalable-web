package com.pmattioli.diffresolver.service.impl;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Base64;
import java.util.logging.Logger;

import org.springframework.util.Assert;

/**
 * Provides a very simple implementation for the {@code resolveDiff} method according to the {@code DiffResolverService}
 * interface, including validation.
 *
 * <p>This implementation conforms to the requirements defined for this API, and aims to demonstrate what the author
 * understood was expected for the task. However, in a real-world application a higher-performance third party library
 * should be used. Examples of these are:
 * <ul>
 * <li><a href="https://sourceforge.net/projects/javaxdelta/">javaxdelta</a></li>
 * <li><a href="https://sourceforge.net/projects/jbdiff/">jbdiff</a></li>
 * </ul>
 * @author pmattioli
 */
public class SimpleDiffResolverService extends AbstractDiffResolverService {

    private static final Logger LOG = Logger.getLogger(SimpleDiffResolverService.class.getName());

    @Override
    public int[] resolveDiff(int diffId) {

        Assert.state(super.exists(diffId), "Diff ID " + diffId + " doesn't exist");

        byte[] decodedLeftData = getDecodedData(super.getLeft(diffId),"No left side data for ID " + diffId);
        byte[] decodedRightData = getDecodedData(super.getRight(diffId),"No right side data for ID " + diffId);

        return doResolveDiff(decodedLeftData, decodedRightData);

    }

    /**
     * This method is protected so the algorithm can be replaced while reusing the decoding logic, if desired.
     *
     * @param leftSide a {@code byte[]} representing a UTF-8 byte stream for the left side of the diff comparison.
     * @param rightSide a {@code byte[]} representing a UTF-8 byte stream for the right side of the diff comparison.
     * @return the offsets array according to the {@code DiffResolverService} interface
     */
    protected int[] doResolveDiff(byte[] leftSide, byte[] rightSide) {

        Assert.state(leftSide.length == rightSide.length, "Files have different sizes");

        int[] offsetsForDiffs = new int[leftSide.length];
        for(int i=0; i < leftSide.length; i++){
            if (leftSide[i] != rightSide[i]){
                offsetsForDiffs[i] = i+1;
            }
        }

        LOG.info("Resulting offsets array: " + Arrays.toString(offsetsForDiffs));

        return offsetsForDiffs;

    }

    private byte[] getDecodedData(String encodedData, String exceptionMessage) {
        Assert.state(encodedData != null, exceptionMessage);
        return Base64.getDecoder().decode(encodedData.getBytes(Charset.forName("UTF-8")));
    }
}
