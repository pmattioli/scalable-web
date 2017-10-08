package com.pmattioli.diffresolver.service;

/**
 * Provides methods for setting both sides of a Binary Diff comparison and obtaining an {@code int[]} representing the
 * offsets in which both data strings provided differ.
 *
 * <p>The interface expects te data provided to be non-null, equal-sized and Base64-encoded.
 * @author pmattioli
 */

public interface DiffResolverService {

    /**
     * Identifies the provided data with the specified diffId for later reference. This data will be decoded to UTF-8
     * and compared byte to byte to the data provided via de setRight method when the resolveDiff method is invoked.
     *
     * @param diffId an {@code int} value which identifies the provided data with the specified DiffID for later
     *               reference. It has to match the value provided for the right side of the comparison.
     * @param data a Base64-encoded {@code String} representing the left side of a binary diff comparison.
     *
     * @throws IllegalArgumentException when the data provided is not a valid Base64-encoded string
     */
    void setLeft(int diffId, String data);

    /**
     * Identifies the provided data with the specified diffId for later reference. This data will be decoded to UTF-8
     * and compared byte to byte to the data provided via de setLeft method when the resolveDiff method is invoked.
     *
     * @param diffId an {@code int} value which identifies the provided data with the specified DiffID for later
     *               reference. It has to match the value provided for the left side of the comparison.
     * @param data a Base64-encoded {@code String} representing the right side of a binary diff comparison.
     *
     * @throws IllegalArgumentException when the data provided is not a valid Base64-encoded string
     */
    void setRight(int diffId, String data);

    /**
     * Returns an {@code int[]} of equal length than the byte size of the data provided via the
     * setLeft and setRight methods after decoding.
     *
     * <p>The array will have a value of zero for all indexes where the provided sides are equal,
     * and the offset value (index + 1) for indexes at which they differ.
     *
     * <p>This method behaves similar to the 'cmp -l' Unix command in the way it presents offset indexes.
     *
     * @param diffId an {@code int} value representing a diff ID for which both setLeft and setRight methods have been
     *               invoked with valid Base64 encoded equal-sized data.
     *
     * @throws IllegalStateException unless both setLeft and setRight methods have been invoked for the
     * specified diffId, or when the left and right data posted have different sizes.
     *
     * @return the offsets array
     */
    int[] resolveDiff(int diffId);

}
