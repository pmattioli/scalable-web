package com.pmattioli.diffresolver.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.Assert;

import com.pmattioli.diffresolver.service.DiffResolverService;

/**
 * Provides implementations for methods {@code setLeft} and {@code setRight} according to the
 * {@code DiffResolverService} interface, including validation.
 *
 * <p>The goal of providing a base implementation for these methods is convenience. Since both are efficient (O(1)),
 * it can be assumed that an implementation of the {@code DiffResolverService} will want to focus on the Diff algorithm
 * rather than on these two methods.
 * @author pmattioli
 */
public abstract class AbstractDiffResolverService implements DiffResolverService {

    private static final String BASE_64_ENCODED_PATTERN =
            "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";

    private enum Sides {
        LEFT_SIDE_INDEX,
        RIGHT_SIDE_INDEX
    }

    private Map<Integer, String[]> diffMap = new HashMap<>();

    @Override
    public void setLeft(int diffId, String data) {
        setDiffSide(diffId, data, Sides.LEFT_SIDE_INDEX.ordinal());
    }

    @Override
    public void setRight(int diffId, String data) {
        setDiffSide(diffId, data, Sides.RIGHT_SIDE_INDEX.ordinal());
    }

    protected String getLeft(int diffId) {
        return getDiffSide(diffId, Sides.LEFT_SIDE_INDEX.ordinal());
    }

    protected String getRight(int diffId) {
        return getDiffSide(diffId, Sides.RIGHT_SIDE_INDEX.ordinal());
    }

    protected boolean exists(int diffId) {
        return diffMap.containsKey(diffId);
    }

    private void setDiffSide(int diffId, String encodedData, int sideIndex) {
        Assert.notNull(encodedData, "Encoded data can't be null");
        Assert.isTrue(encodedData.matches(BASE_64_ENCODED_PATTERN), "Data has to be Base64-encoded");

        String[] diffSides = diffMap.get(diffId);
        if (diffSides == null){
            diffSides = new String[2];
        }
        diffSides[sideIndex] = encodedData;
        diffMap.put(diffId, diffSides);
    }

    private String getDiffSide(int diffId, int sideIndex) {
        String[] diffSides = diffMap.get(diffId);
        Assert.notNull(diffSides, "No diff found for id " + diffId);
        return diffSides[sideIndex];
    }

}
