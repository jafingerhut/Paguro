// Copyright 2015 PlanBase Inc. & Glen Peterson
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

package org.organicdesign.fp.tuple;

import java.util.Objects;

// ======================================================================================
// THIS CLASS IS GENERATED BY /tupleGenerator/TupleGenerator.java.  DO NOT EDIT MANUALLY!
// ======================================================================================

/**
 Holds 6 items of potentially different types.  Designed to let you easily create immutable
 subclasses (to give your data structures meaningful names) with correct equals(), hashCode(), and
 toString() methods.
 */
public class Tuple6<A,B,C,D,E,F> {
    // Fields are protected so that sub-classes can make accessor methods with meaningful names.
    protected final A _1;
    protected final B _2;
    protected final C _3;
    protected final D _4;
    protected final E _5;
    protected final F _6;

    /**
     Constructor is protected (not public) for easy inheritance.  Josh Bloch's "Item 1" says public
     static factory methods are better than constructors because they have names, they can return
     an existing object instead of a new one, and they can return a sub-type.  Therefore, you
     have more flexibility with a static factory as part of your public API then with a public
     constructor.
     */
    protected Tuple6(A a, B b, C c, D d, E e, F f) {
        _1 = a; _2 = b; _3 = c; _4 = d; _5 = e; _6 = f;
    }

    /** Public static factory method */
    public static <A,B,C,D,E,F> Tuple6<A,B,C,D,E,F> of(A a, B b, C c, D d, E e, F f) {
        return new Tuple6<>(a, b, c, d, e, f);
    }

    /** Returns the 1st field */
    public A _1() { return _1; }
    /** Returns the 2nd field */
    public B _2() { return _2; }
    /** Returns the 3rd field */
    public C _3() { return _3; }
    /** Returns the 4th field */
    public D _4() { return _4; }
    /** Returns the 5th field */
    public E _5() { return _5; }
    /** Returns the 6th field */
    public F _6() { return _6; }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
               _1 + "," + _2 + "," + _3 + "," + _4 + "," + _5 + "," + _6 + ")";
    }

    @Override
    public boolean equals(Object other) {
        // Cheapest operation first...
        if (this == other) { return true; }
        if (!(other instanceof Tuple6)) { return false; }
        // Details...
        @SuppressWarnings("rawtypes") final Tuple6 that = (Tuple6) other;

        return Objects.equals(this._1, that._1()) &&
               Objects.equals(this._2, that._2()) &&
               Objects.equals(this._3, that._3()) &&
               Objects.equals(this._4, that._4()) &&
               Objects.equals(this._5, that._5()) &&
               Objects.equals(this._6, that._6());
    }

    @Override
    public int hashCode() {
        // First 2 fields match Tuple2 which implements java.util.Map.Entry as part of the map
        // contract.
        int ret = 0;
        if (_1 != null) { ret = _1.hashCode(); }
        if (_2 != null) { ret = ret ^ _2.hashCode(); }
        if (_3 != null) { ret = ret + _3.hashCode(); }
        if (_4 != null) { ret = ret + _4.hashCode(); }
        if (_5 != null) { ret = ret + _5.hashCode(); }
        if (_6 != null) { ret = ret + _6.hashCode(); }
        return ret;
    }
}