/**
 Copyright (c) Rich Hickey. All rights reserved. The use and distribution terms for this software are covered by the
 Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php) which can be found in the file epl-v10.html
 at the root of this distribution. By using this software in any fashion, you are agreeing to be bound by the terms of
 this license. You must not remove this notice, or any other, from this software.
 */

/* rich Mar 3, 2008 */

package org.organicdesign.fp.collections;

import org.organicdesign.fp.Option;
import org.organicdesign.fp.permanent.Sequence;

import java.util.Comparator;

public class PersistentTreeSet<E> implements ImSetSorted<E> {
    @SuppressWarnings("unchecked")
    static public final PersistentTreeSet EMPTY = new PersistentTreeSet(PersistentTreeMap.EMPTY);

    /** {@inheritDoc} */
    @Override public boolean contains(Object o) { return impl.containsValue(o); }

    /** {@inheritDoc} */
    @Override public int size() { return impl.size(); }

    /** {@inheritDoc} */
    @Override public boolean isEmpty() { return impl.isEmpty(); }

    /** {@inheritDoc} */
    @Override
    public UnIterator<E> iterator() {
        return impl.keySet().iterator(); // TODO: Infinite loop - Fix this!
    }

    @Override public boolean equals(Object other) {
        return (other != null) &&
                (other instanceof ImSetSorted) &&
                (this.size() == ((ImSetSorted) other).size()) &&
                UnIterable.equals(this, (ImSetSorted) other);
    }

    @Override public int hashCode() { return (size() == 0) ? 0 : UnIterable.hashCode(this); }

    @SuppressWarnings("unchecked")
    static public <T> PersistentTreeSet<T> empty() { return EMPTY; }

    final ImMapSorted<E,?> impl;

//    static public <T> PersistentTreeSet<T> create(ISeq<T> items) {
//        PersistentTreeSet<T> ret = emptyTreeSet();
//        for (; items != null; items = items.next()) {
//            ret = ret.cons(items.head());
//        }
//        return ret;
//    }
//
//    static public <T> PersistentTreeSet<T> create(Comparator<T> comp, ISeq<T> items) {
//        PersistentTreeSet<T> ret = new PersistentTreeSet<>(null, new PersistentTreeMap<>(null, comp));
//        for (; items != null; items = items.next()) {
//            ret = ret.cons(items.head());
//        }
//        return ret;
//    }

    private PersistentTreeSet(ImMapSorted<E,?> i) { impl = i; }


    public static <T> PersistentTreeSet<T> of(ImMapSorted<T,?> i) { return new PersistentTreeSet<>(i); }

    @Override public ImSetSorted<E> put(E e) {
        return (impl.containsKey(e)) ? this
                                     : new PersistentTreeSet<>(impl.assoc(e, null));
    }

    @Override public ImSetSorted<E> disjoin(E key) {
        return (impl.containsKey(key)) ? new PersistentTreeSet<>(impl.without(key))
                                       : this;
    }

    @Override public Comparator<? super E> comparator() { return impl.comparator(); }

    @Override public ImSetSorted<E> subSet(E fromElement, E toElement) {
        return PersistentTreeSet.of(impl.subMap(fromElement, toElement));
    }

    @Override public E first() { return impl.firstKey(); }

    @Override public Option<E> head() { return size() > 0 ? Option.of(impl.firstKey()) : Option.none(); }

    // TODO: Ensure that KeySet is sorted.
    @Override public Sequence<E> tail() { return impl.without(first()).keySet(); }

    @Override public E last() { return impl.lastKey(); }

//    @Override
//    public ISeq<E> rseq() {
//        return APersistentMap.KeySeq.create(((Reversible<E>) impl).rseq());
//    }
//
//    @Override
//    public Comparator comparator() {
//        return ((Sorted) impl).comparator();
//    }

//    @Override
//    public Object entryKey(E entry) {
//        return entry;
//    }

//    @SuppressWarnings("unchecked")
//    @Override
//    public ISeq<E> seq(boolean ascending) {
//        PersistentTreeMap m = (PersistentTreeMap) impl;
//        return RT.keys(m.seq(ascending));
//    }
//
//    @SuppressWarnings("unchecked")
//    @Override
//    public ISeq<E> seqFrom(Object key, boolean ascending) {
//        PersistentTreeMap m = (PersistentTreeMap) impl;
//        return RT.keys(m.seqFrom(key, ascending));
//    }

}
