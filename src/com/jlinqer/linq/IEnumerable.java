package com.jlinqer.linq;

import com.jlinqer.collections.List;
import com.jlinqer.collections.Set;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by Reuben Kuhnert
 * Modified by Keisuke Kato
 */
public interface IEnumerable<TSource> extends Iterable<TSource> {
// -------------------------- STATIC METHODS --------------------------

    /**
     * ﻿Generates a sequence of integral numbers within a specified range.
     *
     * @param start ﻿The value of the first integer in the sequence.
     * @param count ﻿The number of sequential integers to generate.
     * @return ﻿A List<Integer> that ﻿contains a range of sequential integral numbers.
     * @throws IndexOutOfBoundsException ﻿count is less than 0.-or-start + count -1 is larger than Integer.MaxValue.
     */
    static List<Integer> range(final int start, final int count) throws IndexOutOfBoundsException {
        if (count < 0) throw new IndexOutOfBoundsException("count is less than 0.");
        if (Integer.MAX_VALUE < (long) start + (long) count - 1)
            throw new IndexOutOfBoundsException("start + count -1 is larger than Integer.MaxValue.");

        List<Integer> list = new List<>();
        for (int i = 0; i < count; i++) {
            list.add(start + i);
        }

        return list;
    }

    /**
     * ﻿Generates a sequence that contains one repeated value.
     *
     * @param type      ﻿The type of the value to be repeated in the result sequence.
     * @param element   ﻿The value to be repeated.
     * @param count     ﻿The number of times to repeat the value in the generated sequence.
     * @param <TResult> ﻿The type of the value to be repeated in the result sequence.
     * @return ﻿A List<TResult> that contains a repeated value.
     * @throws IllegalArgumentException  ﻿type is null.
     * @throws IndexOutOfBoundsException ﻿count is less than 0.
     */
    static <TResult> List<TResult> repeat(final Class<TResult> type, final TResult element, final int count) throws IllegalArgumentException, IndexOutOfBoundsException {
        if (type == null) throw new IllegalArgumentException("type is null.");
        if (count < 0) throw new IndexOutOfBoundsException("count is less than 0.");

        List<TResult> list = new List<>();
        for (int i = 0; i < count; i++) {
            list.add(element);
        }

        return list;
    }

    /**
     * ﻿Returns an empty List<T> that has the specified ﻿type argument.
     *
     * @param type      The type to assign to the type parameter of the returned generic List<T>.
     * @param <TResult> ﻿The type to assign to the type parameter of the returned generic List<T>.
     * @return ﻿An empty List<T> whose type argument is ﻿TResult.
     * @throws IllegalArgumentException type is null.
     */
    static <TResult> List<TResult> empty(final Class<TResult> type) {
        if (type == null) throw new IllegalArgumentException("type is null.");

        return new List<>();
    }

// -------------------------- OTHER METHODS --------------------------

    /**
     * ﻿Applies an accumulator function over a sequence.
     *
     * @param accumulator ﻿An accumulator function to be invoked on each element.
     * @return ﻿The final accumulator value.
     * @throws IllegalArgumentException      accumulator is null.
     * @throws UnsupportedOperationException source contains no elements.
     */
    default TSource aggregate(final BinaryOperator<TSource> accumulator) throws IllegalArgumentException, UnsupportedOperationException {
        if (accumulator == null) throw new IllegalArgumentException("accumulator is null.");
        if (this.count() == 0) throw new UnsupportedOperationException("source contains no elements.");

        Iterator<TSource> iterator = this.iterator();
        TSource result = iterator.next();
        while (iterator.hasNext()) {
            TSource elem = iterator.next();
            result = accumulator.apply(result, elem);
        }

        return result;
    }

    /**
     * ﻿Returns the number of elements in a sequence.
     *
     * @return ﻿﻿The number of elements in the input sequence.
     */
    default int count() {
        int count = 0;
        for (TSource item : this) count++;
        return count;
    }

    /**
     * ﻿Determines whether all elements of a sequence satisfy a condition.
     *
     * @param predicate ﻿A function to test each element for a condition.
     * @return ﻿true if every element of the source sequence passes the test in the specified
     * ﻿predicate, or if the sequence is empty    { return null;} otherwise, false.
     * @throws IllegalArgumentException predicate is null.
     */
    default boolean all(final Predicate<TSource> predicate) throws IllegalArgumentException {
        if (predicate == null) throw new IllegalArgumentException("predicate is null.");

        return !this.where(x -> !predicate.test(x)).any();
    }

    /**
     * ﻿Determines whether a sequence contains any elements.
     *
     * @return ﻿true if the source sequence contains any elements    { return null;} otherwise, false.
     */
    default boolean any() {
        return this.iterator().hasNext();
    }

    /**
     * ﻿Determines whether any element of a sequence satisfies a condition.
     *
     * @param predicate ﻿A function to test each element for a condition.
     * @return ﻿true if any elements in the source sequence pass the test in the specified
     * ﻿predicate    { return null;} otherwise, false.
     * @throws IllegalArgumentException predicate is null.
     */
    default boolean any(final Predicate<TSource> predicate) throws IllegalArgumentException {
        if (predicate == null) throw new IllegalArgumentException("predicate is null.");

        return this.where(predicate).any();
    }

    /**
     * ﻿﻿Computes the average of a sequence of Decimal values that are obtained
     * ﻿by invoking a transform function on each element of the input sequence.
     *
     * @param selector ﻿A transform function to apply to each element.
     * @return ﻿The average of the sequence of values.
     * @throws IllegalArgumentException      selector is null.
     * @throws UnsupportedOperationException ﻿source contains no elements.
     */
    default BigDecimal averageBigDecimal(final Function<TSource, BigDecimal> selector) throws IllegalArgumentException, UnsupportedOperationException {
        if (selector == null) throw new IllegalArgumentException("selector is null.");
        if (this.count() == 0) throw new UnsupportedOperationException("source contains no elements.");

        BigDecimal sum = BigDecimal.ZERO;
        long count = 0;

        for (TSource item : this) {
            sum = sum.add(selector.apply(item));
            count++;
        }

        return sum.divide(new BigDecimal(count));
    }

    /**
     * ﻿﻿﻿Computes the average of a sequence of Double values that are obtained
     * ﻿by invoking a transform function on each element of the input sequence.
     *
     * @param selector ﻿A transform function to apply to each element.
     * @return ﻿The average of the sequence of values.
     * @throws IllegalArgumentException      selector is null.
     * @throws UnsupportedOperationException ﻿source contains no elements.
     */
    default double averageDouble(final Function<TSource, Double> selector) throws IllegalArgumentException, UnsupportedOperationException {
        if (selector == null) throw new IllegalArgumentException("selector is null.");
        if (this.count() == 0) throw new UnsupportedOperationException("source contains no elements.");

        double sum = 0;
        long count = 0;

        for (TSource item : this) {
            sum += selector.apply(item);
            count++;
        }

        return sum / (double) count;
    }

    /**
     * ﻿﻿Computes the average of a sequence of Integer values that are obtained
     * ﻿by invoking a transform function on each element of the input sequence.
     *
     * @param selector ﻿A transform function to apply to each element.
     * @return ﻿﻿The average of the sequence of values.
     * @throws IllegalArgumentException      selector is null.
     * @throws UnsupportedOperationException ﻿source contains no elements.
     */
    default double averageInt(final Function<TSource, Integer> selector) throws IllegalArgumentException, UnsupportedOperationException {
        if (selector == null) throw new IllegalArgumentException("selector is null.");
        if (this.count() == 0) throw new UnsupportedOperationException("source contains no elements.");

        double sum = 0;
        long count = 0;

        for (TSource item : this) {
            sum += selector.apply(item);
            count++;
        }

        return sum / (double) count;
    }

    /**
     * ﻿﻿Computes the average of a sequence of Long values that are obtained
     * ﻿﻿by invoking a transform function on each element of the input sequence.
     *
     * @param selector ﻿A transform function to apply to each element.
     * @return ﻿The average of the sequence of values.
     * @throws IllegalArgumentException      selector is null.
     * @throws UnsupportedOperationException ﻿source contains no elements.
     */
    default double averageLong(final Function<TSource, Long> selector) throws IllegalArgumentException, UnsupportedOperationException {
        if (selector == null) throw new IllegalArgumentException("selector is null.");
        if (this.count() == 0) throw new UnsupportedOperationException("source contains no elements.");

        double sum = 0;
        long count = 0;

        for (TSource item : this) {
            sum += selector.apply(item);
            count++;
        }

        return sum / (double) count;
    }

    /**
     * ﻿Concatenates two sequences.
     *
     * @param second ﻿The sequence to concatenate to the first sequence.
     * @return ﻿An IEnumerable<TSource> that contains the concatenated ﻿elements of the two input sequences.
     * @throws IllegalArgumentException ﻿﻿second is null.
     */
    default IEnumerable<TSource> concat(final IEnumerable<TSource> second) throws IllegalArgumentException {
        if (second == null) throw new IllegalArgumentException("second is null.");

        List<TSource> allItems = new List<>();
        for (TSource item : this) allItems.add(item);
        for (TSource item : second) allItems.add(item);

        return allItems;
    }

    /**
     * ﻿Returns the elements of the specified sequence or the type parameter's default
     * ﻿value in a singleton collection if the sequence is empty.
     *
     * @return ﻿An IEnumerable<TSource> object that contains the default
     * ﻿value for the TSource type if source is empty    { return null;} otherwise, source.
     */
    default IEnumerable<TSource> defaultIfEmpty() {
        if (this.count() == 0) {
            List<TSource> list = new List<>();
            list.add(null);
            return list;
        }

        return this;
    }

    /**
     * ﻿Returns the elements of the specified sequence or the specified value in
     * ﻿a singleton collection if the sequence is empty.
     *
     * @param defaultValue ﻿The value to return if the sequence is empty.
     * @return ﻿An System.Collections.Generic.IEnumerable<TSource> that contains defaultValue if
     * ﻿source is empty    { return null;} otherwise, source.
     */
    default IEnumerable<TSource> defaultIfEmpty(final TSource defaultValue) {
        return (this.count() == 0) ? new List<>(defaultValue) : this;
    }

    /**
     * ﻿Returns distinct elements from a sequence by using the default equality ﻿comparer
     * ﻿to compare values.
     *
     * @return ﻿An IEnumerable<TSource> that contains distinct elements ﻿from the source sequence.
     */
    default IEnumerable<TSource> distinct() {
        IEnumerable<TSource> self = this;
        return () -> {
            Set<TSource> seenItems = new Set<>();
            return self.where(seenItems::add).iterator();
        };
    }

    /**
     * ﻿Returns the element at a specified index in a sequence or a default value
     * ﻿if the index is out of range.
     *
     * @param index ﻿The zero-based index of the element to retrieve.
     * @return ﻿default(TSource) if the index is outside the bounds of the source sequence    { return null;}
     * ﻿otherwise, the element at the specified position in the source sequence.
     */
    default TSource elementAtOrDefault(final int index) {
        return (index < 0 || this.count() < index + 1)
                ? null
                : this.elementAt(index);
    }

    /**
     * ﻿Returns the element at a specified index in a sequence.
     *
     * @param index ﻿The zero-based index of the element to retrieve.
     * @return ﻿The element at the specified position in the source sequence.
     * @throws IndexOutOfBoundsException ﻿index is less than 0 or greater than or equal to the number of elements in
     *                                   ﻿source.
     */
    default TSource elementAt(final int index) throws IndexOutOfBoundsException {
        if (index < 0 || this.count() < index + 1)
            throw new IndexOutOfBoundsException("index is less than 0 or greater than or equal to the number of elements in source.");

        if (this.count() == 0) throw new UnsupportedOperationException("The source sequence is empty.");

        Iterator<TSource> iterator = this.iterator();
        if (!iterator.hasNext())
            throw new UnsupportedOperationException("The source sequence is empty.");

        TSource returnValue = null;
        for (int i = 0; i <= index; i++) {
            returnValue = iterator.next();
        }

        return returnValue;
    }

    /**
     * ﻿Produces the set difference of two sequences by using the default equality
     * ﻿comparer to compare values.
     *
     * @param second ﻿An IEnumerable<TSource> whose elements that also occur ﻿in the first sequence will cause those elements
     *               to be removed from the returned ﻿sequence.
     * @return ﻿A sequence that contains the set difference of the elements of two sequences.
     * @throws IllegalArgumentException second is null.
     */
    default IEnumerable<TSource> except(final Iterable<TSource> second) throws IllegalArgumentException {
        if (second == null) throw new IllegalArgumentException("second is null.");

        Set<TSource> exceptItems;
        if (second instanceof Set)
            exceptItems = (Set<TSource>) second;
        else {
            exceptItems = new Set<>();
            for (TSource item : second) exceptItems.add(item);
        }

        List<TSource> subSet = new List<>();
        for (TSource item : this) {
            if (!exceptItems.contains(item)) {
                subSet.add(item);
            }
        }

        return subSet;
    }

    /**
     * ﻿Returns the first element of a sequence.
     *
     * @return ﻿The first element in the specified sequence.
     * @throws UnsupportedOperationException The source sequence is empty.
     */
    default TSource first() throws UnsupportedOperationException {
        if (this.count() == 0) throw new UnsupportedOperationException("The source sequence is empty.");

        final Iterator<TSource> iterator = this.iterator();
        if (iterator.hasNext()) return iterator.next();

        throw new UnsupportedOperationException("The source sequence is empty.");
    }

    /**
     * ﻿Returns the first element in a sequence that satisfies a specified condition.
     *
     * @param predicate ﻿A function to test each element for a condition.
     * @return ﻿The first element in the sequence that passes the test in the specified predicate ﻿function.
     * @throws IllegalArgumentException      ﻿﻿predicate is null.
     * @throws UnsupportedOperationException ﻿﻿No element satisfies the condition in predicate.-or-The source sequence is empty.
     */
    default TSource first(final Predicate<TSource> predicate) throws UnsupportedOperationException {
        if (predicate == null) throw new IllegalArgumentException("predicate is null.");
        if (this.count() == 0) throw new UnsupportedOperationException("The source sequence is empty.");

        final Iterator<TSource> iterator = this.where(predicate).iterator();
        if (iterator.hasNext()) return iterator.next();

        throw new UnsupportedOperationException("No element satisfies the condition in predicate.-or-The source sequence is empty.");
    }

    /**
     * ﻿Returns the first element of a sequence, or a default value if the sequence
     * ﻿of a sequence, or a default value if the sequence
     *
     * @return ﻿﻿﻿default(TSource) if source is empty    { return null;} otherwise, the first element in source.
     */
    default TSource firstOrDefault() {
        final Iterator<TSource> iterator = this.iterator();
        if (iterator.hasNext()) return iterator.next();

        return null;
    }

    /**
     * ﻿Returns the first element of the sequence that satisfies a condition or a
     * ﻿default value if no such element is found.
     *
     * @param predicate ﻿A function to test each element for a condition.
     * @return ﻿default(TSource) if source is empty or if no element passes the test specified
     * ﻿by predicate    { return null;} otherwise, the first element in source that passes the test
     * ﻿specified by predicate.
     * @throws IllegalArgumentException ﻿predicate is null.
     */
    default TSource firstOrDefault(final Predicate<TSource> predicate) throws IllegalArgumentException {
        if (predicate == null) throw new IllegalArgumentException("predicate is null.");

        final Iterator<TSource> iterator = this.where(predicate).iterator();
        if (iterator.hasNext()) return iterator.next();

        return null;
    }

    /**
     * ﻿Groups the elements of a sequence according to a specified key selector function.
     *
     * @param keySelector ﻿A function to extract the key for each element.
     * @param <TKey>      ﻿The type of the key returned by keySelector.
     * @return ﻿An Map<TKey, IEnumerable<TSource>> ﻿object contains a sequence of objects and a key.
     * @throws IllegalArgumentException ﻿keySelector is null.
     */
    default <TKey> Map<TKey, IEnumerable<TSource>> groupBy(final Function<TSource, TKey> keySelector) throws IllegalArgumentException {
        if (keySelector == null) throw new IllegalArgumentException("keySelector is null.");

        IEnumerable<TKey> keys = this.select(keySelector);
        Set<TKey> uniqueKeys = new Set<>();
        for (TKey key : keys) {
            uniqueKeys.add(key);
        }

        Map<TKey, IEnumerable<TSource>> result = new HashMap<>();

        for (TKey uniqueKey : uniqueKeys) {
            List<TSource> value = new List<>();
            for (TSource item : this) {
                if (keySelector.apply(item).equals(uniqueKey)) {
                    value.add(item);
                }
            }
            result.put(uniqueKey, value);
        }

        return result;
    }

    /**
     * ﻿Projects each element of a sequence into a new form.
     *
     * @param selector  ﻿A transform function to apply to each element.
     * @param <TResult> ﻿The type of the value returned by selector.
     * @return ﻿An IEnumerable<TResult> whose elements are the result
     * ﻿of invoking the transform function on each element of source.
     * @throws IllegalArgumentException ﻿selector is null.
     */
    default <TResult> IEnumerable<TResult> select(final Function<TSource, TResult> selector) throws IllegalArgumentException {
        if (selector == null) throw new IllegalArgumentException("selector is null.");

        return () -> {
            java.util.Queue<TResult> queue = new ArrayDeque<>();

            final Iterator<TSource> iterator = this.iterator();
            while (iterator.hasNext()) {
                TSource item = iterator.next();
                queue.add(selector.apply(item));
            }

            return new Iterator<TResult>() {
                @Override
                public boolean hasNext() {
                    return queue.size() != 0;
                }

                @Override
                public TResult next() {
                    return queue.remove();
                }
            };
        };
    }

    /**
     * ﻿Produces the set intersection of two sequences by using the default equality
     * ﻿comparer to compare values.
     *
     * @param second ﻿An IEnumerable<TSource> whose distinct elements that ﻿also appear in the first sequence will be returned.
     * @return ﻿A sequence that contains the elements that form the set intersection of two ﻿sequences.
     * @throws IllegalArgumentException second is null.
     */
    default IEnumerable<TSource> intersect(final IEnumerable<TSource> second) throws IllegalArgumentException {
        if (second == null) throw new IllegalArgumentException("second is null.");

        Set<TSource> first;
        if (second instanceof Set) first = (Set<TSource>) second;
        else {
            first = new Set<>();
            for (TSource item : second) {
                first.add(item);
            }
        }
        return first;
    }

    /**
     * ﻿﻿Returns the last element of a sequence.
     *
     * @return ﻿The value at the last position in the source sequence.
     * @throws UnsupportedOperationException ﻿The source sequence is empty.
     */
    default TSource last() throws UnsupportedOperationException {
        if (this.count() == 0) throw new UnsupportedOperationException("The source sequence is empty.");

        final Iterator<TSource> iterator = this.iterator();
        if (!iterator.hasNext())
            throw new UnsupportedOperationException("The source sequence is empty.");

        TSource returnValue = null;
        while (iterator.hasNext()) {
            returnValue = iterator.next();
        }

        return returnValue;
    }

    /**
     * ﻿﻿﻿Returns the last element of a sequence that satisfies a specified condition.
     *
     * @param predicate ﻿A function to test each element for a condition.
     * @return ﻿﻿The last element in the sequence that passes the test in the specified predicate ﻿function.
     * @throws IllegalArgumentException      predicate is null.
     * @throws UnsupportedOperationException ﻿﻿No element satisfies the condition in predicate.-or-The source sequence is ﻿empty.
     */
    default TSource last(final Predicate<TSource> predicate) throws IllegalArgumentException, UnsupportedOperationException {
        if (predicate == null) throw new IllegalArgumentException("predicate is null.");
        if (this.count() == 0) throw new UnsupportedOperationException("The source sequence is empty.");

        final long count = this.longCount(predicate);
        if (count == 0)
            throw new UnsupportedOperationException("The source sequence is empty.");

        final Iterator<TSource> iterator = this.where(predicate).iterator();
        TSource returnValue = null;
        while (iterator.hasNext()) {
            returnValue = iterator.next();
        }

        return returnValue;
    }

    /**
     * ﻿Returns a number that represents how many elements in the specified sequence
     * ﻿satisfy a condition.
     *
     * @param predicate ﻿A function to test each element for a condition.
     * @return ﻿A number that represents how many elements in the sequence satisfy the condition
     * ﻿in the predicate function.
     * @throws IllegalArgumentException predicate is null.
     */
    default long longCount(final Predicate<TSource> predicate) throws IllegalArgumentException {
        if (predicate == null) throw new IllegalArgumentException("predicate is null.");

        return where(predicate).longCount();
    }

    /**
     * ﻿﻿﻿Returns the last element of a sequence, or a default value if the sequence
     * ﻿contains no elements.
     *
     * @return ﻿﻿default(TSource) if the source sequence is empty    { return null;} otherwise, the last element
     * ﻿in the IEnumerable<TSource>.
     */
    default TSource lastOrDefault() {
        if (this.count() == 0) return null;

        final Iterator<TSource> iterator = this.iterator();
        if (!iterator.hasNext())
            return null;

        TSource returnValue = null;
        while (iterator.hasNext()) {
            returnValue = iterator.next();
        }

        return returnValue;
    }

    /**
     * ﻿Returns the last element of a sequence that satisfies a condition or a default
     * ﻿value if no such element is found.
     *
     * @param predicate ﻿A function to test each element for a condition.
     * @return ﻿default(TSource) if the sequence is empty or if no elements pass the test
     * ﻿in the predicate function    { return null;} otherwise, the last element that passes the test
     * ﻿in the predicate function.
     * throws IllegalArgumentException predicate is null.
     */
    default TSource lastOrDefault(final Predicate<TSource> predicate) throws IllegalArgumentException {
        if (predicate == null) throw new IllegalArgumentException("predicate is null.");

        if (this.count() == 0) return null;

        final long count = this.longCount(predicate);
        if (count == 0)
            return null;

        final Iterator<TSource> iterator = this.where(predicate).iterator();
        TSource returnValue = null;
        while (iterator.hasNext()) {
            returnValue = iterator.next();
        }

        return returnValue;
    }

    /**
     * ﻿Returns the number of elements in a sequence.
     *
     * @return ﻿﻿The number of elements in the input sequence.
     */
    default long longCount() {
        long count = 0;
        for (TSource item : this) count++;
        return count;
    }

    /**
     * ﻿Invokes a transform function on each element of a sequence and returns the
     * ﻿maximum TSource value.
     *
     * @param selector ﻿A transform function to apply to each element.
     * @return ﻿The maximum value in the sequence.
     * @throws IllegalArgumentException      selector is null.
     * @throws UnsupportedOperationException ﻿source contains no elements.
     */
    default <TKey extends Comparable> TSource max(final Function<TSource, TKey> selector) throws IllegalArgumentException, UnsupportedOperationException {
        if (selector == null) throw new IllegalArgumentException("selector is null.");
        if (this.count() == 0) throw new UnsupportedOperationException("source contains no elements.");

        TSource maxItem = null;

        for (TSource item : this) {
            if (maxItem == null) {
                maxItem = item;
                continue;
            }

            final TKey second = selector.apply(item);
            final TKey first = selector.apply(maxItem);

            if (second.compareTo(first) > 0) {
                maxItem = item;
            }
        }

        return maxItem;
    }

    /**
     * ﻿Invokes a transform function on each element of a sequence and returns the
     * minimum TSource value.
     *
     * @param selector ﻿A transform function to apply to each element.
     * @return ﻿The minimum value in the sequence.
     * @throws IllegalArgumentException      selector is null.
     * @throws UnsupportedOperationException ﻿source contains no elements.
     */
    default <TKey extends Comparable> TSource min(final Function<TSource, TKey> selector) throws IllegalArgumentException, UnsupportedOperationException {
        if (selector == null) throw new IllegalArgumentException("selector is null.");
        if (this.count() == 0) throw new UnsupportedOperationException("source contains no elements.");

        TSource minItem = null;

        for (TSource item : this) {
            if (minItem == null) {
                minItem = item;
                continue;
            }

            final TKey second = selector.apply(item);
            final TKey first = selector.apply(minItem);

            if (second.compareTo(first) < 0) {
                minItem = item;
            }
        }

        return minItem;
    }

    /**
     * ﻿Filters the elements of an IEnumerable based on a specified ﻿type.
     *
     * @param type      ﻿The type to filter the elements of the sequence on.
     * @param <TResult> ﻿The type to filter the elements of the sequence on.
     * @return ﻿An IEnumerable<TSource> that contains elements from ﻿the input sequence of type TResult.
     * @throws IllegalArgumentException type is null.
     */
    default <TResult> IEnumerable<TResult> ofType(final Class<TResult> type) throws IllegalArgumentException {
        if (type == null) throw new IllegalArgumentException("type is null.");

        return this.where(x -> type.isAssignableFrom(x.getClass())).cast(type);
    }

    /**
     * ﻿Casts the elements of a IEnumerable to the specified type.
     *
     * @param toType    ﻿The type to cast the elements of source to.
     * @param <TResult> ﻿The type to cast the elements of source to.
     * @return ﻿An <TResult> that contains each element of
     * ﻿the source sequence cast to the specified type.
     * @throws IllegalArgumentException      toType is null.
     * @throws UnsupportedOperationException ﻿source contains no elements.
     */
    default <TResult> IEnumerable<TResult> cast(final Class<TResult> toType) throws IllegalArgumentException, UnsupportedOperationException {
        if (toType == null) throw new IllegalArgumentException("toType is null.");
        if (this.count() == 0) throw new UnsupportedOperationException("source contains no elements.");

        return new MapEnumerableIterator<>(this, item -> (TResult) item);
    }

    /**
     * ﻿Sorts the elements of a sequence in ascending order according to a key.
     *
     * @param keySelector ﻿A function to extract a key from an element.
     * @param <TKey>      ﻿The type of the key returned by keySelector.
     * @return ﻿An IEnumerable<TSource> whose elements are sorted according
     * ﻿to a key.
     * @throws IllegalArgumentException ﻿keySelector is null.
     */
    default <TKey extends Comparable> IEnumerable<TSource> orderBy(final Function<TSource, TKey> keySelector) throws IllegalArgumentException {
        if (keySelector == null) throw new IllegalArgumentException("keySelector is null.");

        java.util.List items = new ArrayList<TSource>();
        for (TSource item : this) items.add(item);

        items.sort(new Comparator<TSource>() {
            @Override
            public int compare(final TSource second, final TSource first) {
                final TKey firstProperty = keySelector.apply(first);
                final TKey secondProperty = keySelector.apply(second);

                return secondProperty.compareTo(firstProperty);
            }
        });

        return () -> items.iterator();
    }

    /**
     * ﻿Sorts the elements of a sequence in descending order according to a key.
     *
     * @param keySelector ﻿A function to extract a key from an element.
     * @param <TKey>      ﻿The type of the key returned by keySelector.
     * @return ﻿An IEnumerable<TSource> whose elements are sorted in
     * ﻿descending order according to a key.
     * @throws IllegalArgumentException ﻿keySelector is null.
     */
    default <TKey extends Comparable> IEnumerable<TSource> orderByDescending(final Function<TSource, TKey> keySelector) throws IllegalArgumentException {
        if (keySelector == null) throw new IllegalArgumentException("keySelector is null.");

        java.util.List items = new ArrayList<TSource>();
        for (TSource item : this) items.add(item);

        items.sort(new Comparator<TSource>() {
            @Override
            public int compare(final TSource second, final TSource first) {
                final TKey firstProperty = keySelector.apply(first);
                final TKey secondProperty = keySelector.apply(second);

                return firstProperty.compareTo(secondProperty);
            }
        });

        return () -> items.iterator();
    }

    /**
     * ﻿Inverts the order of the elements in a sequence.
     *
     * @return ﻿A sequence whose elements correspond to those of the input sequence in reverse ﻿order.
     */
    default IEnumerable<TSource> reverse() {
        return () -> {
            Stack<TSource> stack = new Stack<>();
            for (TSource item : IEnumerable.this) stack.push(item);

            return new Iterator<TSource>() {
                @Override
                public boolean hasNext() {
                    return !stack.empty();
                }

                @Override
                public TSource next() {
                    return stack.pop();
                }
            };
        };
    }

    /**
     * ﻿Projects each element of a sequence to an IEnumerable<TSource> ﻿and flattens the resulting sequences into one sequence.
     *
     * @param selector  ﻿A transform function to apply to each element.
     * @param <TResult> ﻿The type of the elements of the sequence returned by selector.
     * @return ﻿An IEnumerable<TSource> whose elements are the result ﻿of invoking the one-to-many transform function on each element of the input ﻿sequence.
     * @throws IllegalArgumentException ﻿selector is null.
     */
    default <TResult> IEnumerable<TResult> selectMany(final Function<TSource, IEnumerable<TResult>> selector) throws IllegalArgumentException {
        if (selector == null) throw new IllegalArgumentException("selector is null.");

        return () -> {
            java.util.Queue<TResult> queue = new ArrayDeque<>();

            final Iterator<TSource> iterator = this.iterator();
            while (iterator.hasNext()) {
                TSource item = iterator.next();
                final IEnumerable<TResult> selected = selector.apply(item);
                final Iterator<TResult> selectedIterator = selected.iterator();
                while (selectedIterator.hasNext()) {
                    queue.add(selectedIterator.next());
                }
            }

            return new Iterator<TResult>() {
                @Override
                public boolean hasNext() {
                    return queue.size() != 0;
                }

                @Override
                public TResult next() {
                    return queue.remove();
                }
            };
        };
    }

    /**
     * ﻿Determines whether two sequences are equal by comparing the elements by using
     * ﻿the default equality comparer for their type.
     *
     * @param second ﻿An IEnumerable<TSource> to compare to the first sequence.
     * @return ﻿true if the two source sequences are of equal length and their corresponding
     * ﻿elements are equal according to the default equality comparer for their type    { return null;}
     * ﻿otherwise, false.
     * @throws IllegalArgumentException second is null.
     */
    default boolean sequenceEqual(final IEnumerable<TSource> second) throws IllegalArgumentException {
        if (second == null) throw new IllegalArgumentException("second is null.");

        if (this.count() != second.count()) return false;

        for (int i = 0; i < second.count(); i++) {
            if (!second.elementAt(i).equals(this.elementAt((i)))) {
                return false;
            }
        }

        return true;
    }

    /**
     * ﻿Returns the only element of a sequence, and throws an exception if there
     * ﻿is not exactly one element in the sequence.
     *
     * @return ﻿The single element of the input sequence.
     * @throws UnsupportedOperationException ﻿The input sequence contains more than one element.-or-The input sequence is empty.
     */
    default TSource single() throws UnsupportedOperationException {
        if (this.count() != 1)
            throw new UnsupportedOperationException("The input sequence contains more than one element.-or-The input sequence is empty.");

        final Iterator<TSource> iterator = this.iterator();
        if (iterator.hasNext()) {
            TSource item = iterator.next();

            if (iterator.hasNext())
                throw new UnsupportedOperationException("The input sequence contains more than one element.");
            return item;
        }

        throw new UnsupportedOperationException("The input sequence is empty.");
    }

    /**
     * ﻿﻿Returns the only element of a sequence that satisfies a specified condition,
     * ﻿and throws an exception if more than one such element exists.
     *
     * @param predicate ﻿The single element of the input sequence that satisfies a condition.
     * @return ﻿﻿The single element of the input sequence that satisfies a condition.
     * @throws IllegalArgumentException      predicate is null.
     * @throws UnsupportedOperationException ﻿No element satisfies the condition in predicate.-or-More than one element
     *                                       ﻿satisfies the condition in predicate.-or-The source sequence is empty.
     */
    default TSource single(final Predicate<TSource> predicate) throws IllegalArgumentException, UnsupportedOperationException {
        if (predicate == null) throw new IllegalArgumentException("predicate is null.");

        final Iterator<TSource> iterator = this.where(predicate).iterator();
        if (iterator.hasNext()) {
            TSource item = iterator.next();

            if (iterator.hasNext())
                throw new UnsupportedOperationException("The input sequence contains more than one element.");
            return item;
        }

        throw new UnsupportedOperationException("The input sequence is empty.");
    }

    /**
     * ﻿Returns the only element of a sequence, or a default value if the sequence
     * ﻿is empty    { return null;} this method throws an exception if there is more than one element
     * ﻿in the sequence.
     *
     * @return ﻿The single element of the input sequence, or default(TSource) if the sequence
     * ﻿contains no elements.
     */
    default TSource singleOrDefault() throws UnsupportedOperationException {
        if (0 == this.count()) return null;
        if (1 < this.count())
            throw new UnsupportedOperationException("The input sequence contains more than one element.-or-The input sequence is empty.");

        final Iterator<TSource> iterator = this.iterator();
        if (iterator.hasNext()) {
            TSource item = iterator.next();

            if (iterator.hasNext()) return null;
            return item;
        }

        return null;
    }

    /**
     * ﻿Returns the only element of a sequence that satisfies a specified condition
     * ﻿or a default value if no such element exists    { return null;} this method throws an exception
     * ﻿if more than one element satisfies the condition.
     *
     * @param predicate ﻿A function to test an element for a condition.
     * @return ﻿The single element of the input sequence that satisfies the condition, or ﻿default(TSource) if no such element is found.
     * @throws IllegalArgumentException predicate is null.
     */
    default TSource singleOrDefault(final Predicate<TSource> predicate) throws IllegalArgumentException {
        if (predicate == null) throw new IllegalArgumentException("predicate is null.");

        final Iterator<TSource> iterator = this.where(predicate).iterator();
        if (iterator.hasNext()) {
            TSource returnValue = iterator.next();

            if (iterator.hasNext()) return null;
            return returnValue;
        }

        return null;
    }

    /**
     * ﻿Bypasses a specified number of elements in a sequence and then returns the
     * ﻿remaining elements.
     *
     * @param count ﻿The number of elements to skip before returning the remaining elements.
     * @return ﻿An IEnumerable<TSource> that contains the elements that occur after the specified index in the input sequence.
     */
    default IEnumerable<TSource> skip(final int count) {
        return () -> {
            final Iterator<TSource> iterator = this.iterator();

            for (int i = 0; i < count; i++) {
                if (!iterator.hasNext()) break;
                iterator.next();
            }

            java.util.Queue<TSource> queue = new ArrayDeque<>();
            while (iterator.hasNext()) {
                queue.add(iterator.next());
            }

            return new Iterator<TSource>() {
                @Override
                public boolean hasNext() {
                    return queue.size() != 0;
                }

                @Override
                public TSource next() {
                    return queue.remove();
                }
            };
        };
    }

    /**
     * ﻿Bypasses elements in a sequence as long as a specified condition is true
     * ﻿and then returns the remaining elements.
     *
     * @param predicate ﻿A function to test each element for a condition.
     * @return ﻿An System.Collections.Generic.IEnumerable<TSource> that contains the elements from
     * ﻿the input sequence starting at the first element in the linear series that
     * ﻿does not pass the test specified by predicate.
     * @throws IllegalArgumentException predicate is null.
     */
    default IEnumerable<TSource> skipWhile(final Predicate<TSource> predicate) throws IllegalArgumentException {
        if (predicate == null) throw new IllegalArgumentException("predicate is null.");

        return () -> {
            java.util.Queue<TSource> queue = new ArrayDeque<>();

            final boolean none = (this.count(predicate) == this.count());
            if (!none) {
                final Iterator<TSource> iterator = this.iterator();
                TSource item = null;
                while (iterator.hasNext()) {
                    item = iterator.next();
                    if (!predicate.test(item)) {
                        break;
                    }
                }

                queue.add(item);
                while (iterator.hasNext()) {
                    queue.add(iterator.next());
                }
            }

            return new Iterator<TSource>() {
                @Override
                public boolean hasNext() {
                    return queue.size() != 0;
                }

                @Override
                public TSource next() {
                    return queue.remove();
                }
            };
        };
    }

    /**
     * ﻿Returns a number that represents how many elements in the specified sequence
     * ﻿satisfy a condition.
     *
     * @param predicate ﻿A function to test each element for a condition.
     * @return ﻿A number that represents how many elements in the sequence satisfy the condition
     * ﻿in the predicate function.
     * @throws IllegalArgumentException predicate is null.
     */
    default int count(final Predicate<TSource> predicate) throws IllegalArgumentException {
        if (predicate == null) throw new IllegalArgumentException("predicate is null.");

        return this.where(predicate).count();
    }

    /**
     * ﻿Computes the sum of the sequence of BigDecimal values that are obtained
     * ﻿by invoking a transform function on each element of the input sequence.
     *
     * @param selector ﻿A transform function to apply to each element.
     * @return ﻿selector is null.
     * @throws IllegalArgumentException
     */
    default BigDecimal sumBigDecimal(final Function<TSource, BigDecimal> selector) throws IllegalArgumentException {
        if (selector == null) throw new IllegalArgumentException("selector is null.");

        BigDecimal sum = BigDecimal.ZERO;
        for (TSource item : this) sum = sum.add(selector.apply(item));
        return sum;
    }

    /**
     * ﻿Computes the sum of the sequence of Double values that are obtained
     * ﻿by invoking a transform function on each element of the input sequence.
     *
     * @param selector ﻿A transform function to apply to each element.
     * @return ﻿selector is null.
     * @throws IllegalArgumentException
     */
    default double sumDouble(final Function<TSource, Double> selector) throws IllegalArgumentException {
        if (selector == null) throw new IllegalArgumentException("selector is null.");

        double sum = 0d;
        for (TSource item : this) sum += selector.apply(item);
        return sum;
    }

    /**
     * ﻿Computes the sum of the sequence of Integer values that are obtained
     * ﻿by invoking a transform function on each element of the input sequence.
     *
     * @param selector ﻿A transform function to apply to each element.
     * @return ﻿selector is null.
     * @throws IllegalArgumentException
     */
    default int sumInt(final Function<TSource, Integer> selector) throws IllegalArgumentException {
        if (selector == null) throw new IllegalArgumentException("selector is null.");

        int sum = 0;
        for (TSource item : this) sum += selector.apply(item);
        return sum;
    }

    /**
     * ﻿Computes the sum of the sequence of Long values that are obtained
     * ﻿by invoking a transform function on each element of the input sequence.
     *
     * @param selector ﻿A transform function to apply to each element.
     * @return ﻿selector is null.
     * @throws IllegalArgumentException
     */
    default long sumLong(final Function<TSource, Long> selector) throws IllegalArgumentException {
        if (selector == null) throw new IllegalArgumentException("selector is null.");

        long sum = 0l;
        for (TSource item : this) sum += selector.apply(item);
        return sum;
    }

    /**
     * ﻿Returns a specified number of contiguous elements from the start of a sequence.
     *
     * @param count ﻿The number of elements to return.
     * @return ﻿An IEnumerable<TSource> that contains the specified
     * ﻿number of elements from the start of the input sequence.
     */
    default IEnumerable<TSource> take(final long count) {
        return () -> {
            final Iterator<TSource> iterator = this.iterator();

            java.util.Queue<TSource> queue = new ArrayDeque<>();
            for (int i = 0; i < count; i++) {
                if (!iterator.hasNext()) break;
                queue.add(iterator.next());
            }

            return new Iterator<TSource>() {
                @Override
                public boolean hasNext() {
                    return queue.size() != 0;
                }

                @Override
                public TSource next() {
                    return queue.remove();
                }
            };
        };
    }

    /**
     * ﻿Returns elements from a sequence as long as a specified condition is true.
     *
     * @param predicate ﻿A function to test each element for a condition.
     * @return ﻿An IEnumerable<TSource> that contains the elements from
     * @throws IllegalArgumentException predicate is null.
     */
    default IEnumerable<TSource> takeWhile(final Predicate<TSource> predicate) throws IllegalArgumentException {
        if (predicate == null) throw new IllegalArgumentException("predicate is null.");

        return () -> {
            java.util.Queue<TSource> queue = new ArrayDeque<>();

            final boolean none = (this.count(predicate) == 0);
            if (!none) {
                final Iterator<TSource> iterator = this.iterator();
                while (iterator.hasNext()) {
                    final TSource item = iterator.next();
                    if (!predicate.test(item)) break;
                    queue.add(item);
                }
            }

            return new Iterator<TSource>() {
                @Override
                public boolean hasNext() {
                    return queue.size() != 0;
                }

                @Override
                public TSource next() {
                    return queue.remove();
                }
            };
        };
    }

    /**
     * Creates a List<TSource> from an IEnumerable<TSource>.
     *
     * @return A List<TSource> that contains elements from the input sequence.
     */
    default List<TSource> toList() {
        return new List<>(this);
    }

    /**
     * ﻿Produces the set union of two sequences by using the default equality comparer.
     *
     * @param second ﻿An IEnumerable<TSource> whose distinct elements form ﻿the second set for the union.
     * @return ﻿An IEnumerable<TSource> that contains the elements from ﻿both input sequences, excluding duplicates.
     * @throws IllegalArgumentException second is null.
     */
    default IEnumerable<TSource> union(final IEnumerable<TSource> second) throws IllegalArgumentException {
        if (second == null) throw new IllegalArgumentException("second is null.");

        Set<TSource> allItems = new Set<>();
        for (TSource item : this) allItems.add(item);
        for (TSource item : second) allItems.add(item);

        return allItems;
    }

    /**
     * ﻿Filters a sequence of values based on a predicate.
     *
     * @param predicate ﻿A function to test each element for a condition.
     * @return ﻿An IEnumerable<TSource> that contains elements from
     * ﻿the input sequence that satisfy the condition.
     * @throws IllegalArgumentException predicate is null.
     */
    default IEnumerable<TSource> where(final Predicate<TSource> predicate) throws IllegalArgumentException {
        if (predicate == null) throw new IllegalArgumentException("predicate is null.");

        return new WhereEnumerableIterator<>(this, predicate);
    }
}