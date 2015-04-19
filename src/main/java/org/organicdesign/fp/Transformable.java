// Copyright 2014-02-15 PlanBase Inc. & Glen Peterson
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.organicdesign.fp;

import org.organicdesign.fp.collections.UnList;
import org.organicdesign.fp.collections.UnMap;
import org.organicdesign.fp.collections.UnSet;
import org.organicdesign.fp.collections.UnSetSorted;
import org.organicdesign.fp.function.Function1;
import org.organicdesign.fp.function.Function2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeSet;

import static org.organicdesign.fp.StaticImports.un;

/**
 Represents transformations to be carried out on a collection.  This class also implements the
 methods defined in Realizable so that sub-classes can just implement foldLeft and not have to
 worry about any Realizable functions.
 @param <T>
 */
public interface Transformable<T> extends Realizable<T> {
    /**
     Lazily applies the given function to each item in the underlying data source, and returns
     a View with one item for each result.
     @return a lazy view of the same size as the input (may contain duplicates) containing the
     return values of the given function in the same order as the input values.
      * @param func a function that returns a new value for any value in the input
     */
    <U> Transformable<U> map(Function1<T,U> func);

    /**
     Lazily applies the filter function to the underlying data source and returns a new view
     containing only the items for which the filter returned true
     @return a lazy view of only the filtered items.
      * @param predicate a function that returns true for items to keep, false for items to drop
     */
    Transformable<T> filter(Function1<T,Boolean> predicate);

    /**
     Eagerly processes the entire data source for side effects.
     @param consumer the function to do the processing
     @return the unmodified sequence you started with (for chaining).
     */
    Transformable<T> forEach(Function1<T,?> consumer);

//    /**
//     Deprecated: use filter(...).head() instead.
//     Eagerly returns the first item matching the given predicate.
//     @param pred the test that the item needs to pass
//     @return the first item that passes the test, or null if no such item is found
//     */
//    @Deprecated
//    Option<T> firstMatching(Predicate<T> pred);

    /**
     Shorten this Transformable to contain no more than the specified number of items.
     @param numItems the maximum number of items in the returned view.
     @return a lazy view containing no more than the specified number of items.
     */
    Transformable<T> take(long numItems);

    /**
     Shorten this transformable to contain all items from the beginning so long as they satisfy the
     predicate.
     @return a lazy transformable containing the longest un-interrupted run of items, from the
     beginning of the transformable, that satisfy the given predicate.  This could be 0 items to
     the entire transformable.
      * @param predicate the test.
     */
    Transformable<T> takeWhile(Function1<T,Boolean> predicate);

    /**
     Note that all dropped items will be evaluated as they are dropped.  Any side effects
     (including delays) caused by evaluating these items will be incurred.  For this reason,
     you should always drop as early in your chain of functions as practical.
     @param numItems the number of items at the beginning of this view to ignore
     @return a lazy view with the specified number of items ignored.
     */
    Transformable<T> drop(long numItems);

// View and Sequence cannot inherit from these because because function arguments are contravariant.  It's OK for
// View to return a View and Sequence to return a Sequence because they are subclasses of Transformable and if
// a method returns a T, then sub-classes can return a T or "? extends T".
// But if a method takes an argument of T, then a sub-class can only take an argument of T or "? super T".
// Transformable does not provide a way to get to the first argument or the "rest".  It seems kind of a useless
// interface if the persistent Sequence is fast enough to get rid of the View.
//    /** Add the given Transformable after the end of this one. */
//    Transformable<T> append(Transformable<T> other);
//
//    /** Add the given Transformable before the beginning of this one. */
//    Transformable<T> prepend(Transformable<T> other);


    // TODO: You can always use foldLeft for this operation.  Does having reduceLeft add more clarity to the underlying code, or does it provide some useful additional functionality?
//    /**
//     Eagerly process entire data source.  This is an extremely powerful method, being the only one
//     that currently can produce more output items than input items (flatMap would do that too
//     if implemented).
//     @return
//     @param fun Starting with the first two elements of the list, combines each value in the list with the result so far.  The initial result is u.
//     */
//    T reduceLeft(BiFunction<T, T, T> fun);

    /**
     One of the two higher-order functions that can produce more output items than input items
     (when u is a collection). FlatMap is the other, but foldLeft is eager while flatMap is lazy.
     FoldLeft can also produce a single (scalar) value.  In that form, it is often called reduce().

     @return an eagerly evaluated result which could be a single value like a sum, or a collection.
      * @param u the accumulator and starting value.  This will be passed to the function on the
      first iteration to be combined with the first member of the underlying data source.  For some
      operations you'll need to pass an identity, e.g. for a sum, pass 0, for a product, pass 1 as
      this parameter.
     * @param fun combines each value in the list with the result so far.  The initial result is u.
     */
    <U> U foldLeft(U u, Function2<U,T,U> fun);

    /**
     A form of foldLeft() that handles early termination.  If foldLeft replaces a loop, and return
     is a more general form of break, then this can do anything a loop can do.  If you want to
     terminate based on an input T value rather than an output U, make U = Tuple2(T,V) and have
     terminateWith(Tuple2(T,V) tv) { if tv._1()... }

     @return an eagerly evaluated result which could be a single value like a sum, or a collection.
      * @param u the accumulator and starting value.  This will be passed to the function on the
      first iteration to be combined with the first member of the underlying data source.  For some
      operations you'll need to pass an identity, e.g. for a sum, pass 0, for a product, pass 1 as
      this parameter.
     * @param fun combines each value in the list with the result so far.  The initial result is u.
     * @param terminateWhen returns true when the termination condition is reached and will stop
processing the input at that time, returning the latest u.
     */
    <U> U foldLeft(U u, Function2<U,T,U> fun, Function1<U,Boolean> terminateWhen);


    // Sub-classes cannot inherit from this because the function that you pass in has to know the actal return type.
    // Have to implement this independently on sub-classes.
//    /**
//     One of the two higher-order functions that can produce more output items than input items.
//     foldLeft is the other, but flatMap is lazy while foldLeft is eager.
//     @return a lazily evaluated collection which is expected to be larger than the input
//     collection.  For a collection that's the same size, map() is more efficient.  If the expected
//     return is smaller, use filter followed by map if possible, or vice versa if not.
//     @param fun yields a Transformable of 0 or more results for each input item.
//     */
//    <U> Transformable<U> flatMap(Function<T,? extends Transformable<U>> func);

    @Override
    default ArrayList<T> toJavaArrayList() {
        return foldLeft(new ArrayList<T>(), (ts, t) -> {
            ts.add(t);
            return ts;
        });
    }

    @Override
    default UnList<T> toUnList() { return un(toJavaArrayList()); }

    @Override
    default <U> HashMap<T,U> toJavaHashMap(final Function1<T,U> f1) {
        return foldLeft(new HashMap<T,U>(), (ts, t) -> {
            ts.put(t, f1.applyEx(t));
            return ts;
        });
    }

    @Override
    default <U> UnMap<T,U> toUnMap(Function1<T,U> f1) { return un(toJavaHashMap(f1)); }

    @Override
    default <U> HashMap<U,T> toReverseJavaHashMap(final Function1<T,U> f1) {
        return foldLeft(new HashMap<U,T>(), (ts, t) -> {
            ts.put(f1.applyEx(t), t);
            return ts;
        });
    }

    @Override
    default <U> UnMap<U,T> toReverseUnMap(Function1<T,U> f1) {
        return un(toReverseJavaHashMap(f1));
    }

    @Override
    default TreeSet<T> toJavaTreeSet(Comparator<? super T> comparator) {
        return foldLeft(new TreeSet<T>(comparator), (ts, t) -> {
            ts.add(t);
            return ts;
        });
    }
    @Override
    default TreeSet<T> toJavaTreeSet() { return toJavaTreeSet(null); }


    @Override
    default UnSetSorted<T> toUnSetSorted(Comparator<? super T> comparator) {
        return un(toJavaTreeSet(comparator));
    }
    @Override
    default UnSetSorted<T> toUnSetSorted() { return toUnSetSorted(null); }

    @Override
    default HashSet<T> toJavaHashSet() {
        return foldLeft(new HashSet<T>(), (ts, t) -> {
            ts.add(t);
            return ts;
        });
    }

    @Override
    default UnSet<T> toUnSet() { return un(toJavaHashSet()); }

    @Override
    @SuppressWarnings("unchecked")
    default T[] toTypedArray() {
        ArrayList<T> al = toJavaArrayList();
        return al.toArray((T[]) new Object[al.size()]);
    }

    @Override
    default Iterator<T> toIterator() {
        // Maybe not so performant, but gives a chance to see if this is even a useful method.
        return toJavaArrayList().iterator();
    }
}
