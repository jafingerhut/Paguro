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

package org.organicdesign.fp;

import org.junit.Test;
import org.organicdesign.fp.collections.ImList;
import org.organicdesign.fp.collections.ImMap;
import org.organicdesign.fp.function.Function1;
import org.organicdesign.fp.tuple.Tuple2;
import org.organicdesign.fp.tuple.Tuple3;

import static org.junit.Assert.assertEquals;
import static org.organicdesign.fp.StaticImports.tup;
import static org.organicdesign.fp.StaticImports.vec;
import static org.organicdesign.fp.UsageExampleTest.EType.*;

// Usage examples are put in this unit test to prove that they work.
public class UsageExampleTest {
    // Define some field name constants with a standard Java Enum
    enum EType { HOME, WORK };

    // Part one of a 3-part example for defining data briefly and/or in a way that's easy to read.
    @Test public void dataDefinitionExample1() {
        // Make a list of "people".  This is type-safe even though it looks like JSON.  Unlike JSON,
        // the compiler verifies the types of all your data! The next two examples show different
        // ways to "fix" the huge type signatures, so you can ignore them for now.
        //
        // The tup() method is imported from StaticImports and creates Tuples.  Tuples are basically
        // low-budget immutable objects.  This method is overloaded to handle 2 or 3 arguments
        // (more arguments in the future).  We'll see shortly how you can leverage Tuples to avoid
        // object-oriented programming, or extend them to do object-oriented programming with less
        // boilerplate.  For this example, let's concentrate on the data definition and
        // transformation.
        //
        // Also impoarted from StaticImports, vec() creates an immutable list of any length.
        // There are also set() and map() methods that are not covered in this example.
        ImList<Tuple3<String,String,ImList<Tuple2<EType,String>>>> people =
                vec(tup("Jane", "Smith", vec(tup(HOME, "a@b.c"),
                                             tup(WORK, "b@c.d"))),
                    tup("Fred", "Tase", vec(tup(HOME, "c@d.e"),
                                            tup(WORK, "d@e.f"))));

        // Everything has build-in toString() methods.  Collections show the first 3-5 elements.
        assertEquals("PersistentVector(" +
                     "Tuple3(Jane,Smith,PersistentVector(Tuple2(HOME,a@b.c),Tuple2(WORK,b@c.d)))," +
                     "Tuple3(Fred,Tase,PersistentVector(Tuple2(HOME,c@d.e),Tuple2(WORK,d@e.f))))",
                     people.toString());

        // Let's look at Jane:
        Tuple3<String,String,ImList<Tuple2<EType,String>>> jane = people.get(0);

        assertEquals("Tuple3(Jane,Smith,PersistentVector(Tuple2(HOME,a@b.c),Tuple2(WORK,b@c.d)))",
                     jane.toString());

        // Let's make a map that we can use later to look up people by their email address.
        ImMap<String,Tuple3<String,String,ImList<Tuple2<EType,String>>>> peopleByEmail =

                // flatMap can be confusing the first time you see it.  We may need to produce
                // multiple entries in the resulting map (dictionary) for each person.  DBA's call
                // this a one-to-many relationship.  FlatMap is the way to produce zero or more
                // items for each input item.
                // Note: person._3() is the vector of email addresses
                people.flatMap(person -> person._3()
                                               // Map (dictionary) entries are key value pairs.
                                               // Tuple2 implements Map.Entry so we can set up
                                               // the pair right here.
                                               // Note: mail._2() is the address
                                               .map(mail -> tup(mail._2(), person)))
                        // Now convert the result into an immutable map.  This function takes
                        // another function which is normally used to convert data into key/value
                        // pairs, but we already have key/value pairs, so we just pass the identity
                        // function (which returns its argument unchanged)
                      .toImMap(Function1.identity());

        // Let's look at the map we just created
        assertEquals("PersistentHashMap(" +
                     "Tuple2(d@e.f," +
                     "Tuple3(Fred,Tase,PersistentVector(Tuple2(HOME,c@d.e),Tuple2(WORK,d@e.f))))," +
                     "Tuple2(a@b.c," +
                     "Tuple3(Jane,Smith,PersistentVector(Tuple2(HOME,a@b.c),Tuple2(WORK,b@c.d))))," +
                     "Tuple2(b@c.d," +
                     "Tuple3(Jane,Smith,PersistentVector(Tuple2(HOME,a@b.c),Tuple2(WORK,b@c.d))))," +
                     "Tuple2(c@d.e," +
                     "Tuple3(Fred,Tase,PersistentVector(Tuple2(HOME,c@d.e),Tuple2(WORK,d@e.f)))))",
                     peopleByEmail.toString());

        // Look up Jane by her address
        assertEquals(jane, peopleByEmail.get("b@c.d"));

        // Conclusion:
        // For the price of a few long type signatures, we can write very succinct Java code
        // without all the pre-work of defining types.  This is great for experiments, one-off
        // reports, or other low-investment high-value projects.
        //
        // Next we'll look at two different ways to eliminate or simplify those type signatures.
    }

    @Test public void dataDefinitionExample2() {
        // Fluent interfaces can take maximum advantage of Java's type inferencing.  Here is
        // more-or-less the same code as the above, still type-checked by the compiler, but without
        // specifying any types explicitly:

        assertEquals("Jane",
                     // This is the "people" data structure from above
                     vec(tup("Jane", "Smith", vec(tup(HOME, "a@b.c"),
                                                  tup(WORK, "b@c.d"))),
                         tup("Fred", "Tase", vec(tup(HOME, "c@d.e"),
                                                 tup(WORK, "d@e.f"))))
                             // Create a map to look up people by their address
                             .flatMap(person -> person._3()
                                                      .map(mail -> tup(mail._2(), person)))
                             .toImMap(Function1.identity())
                                     // Look up Jane by her address
                             .get("b@c.d")
                                     // Get her first name
                             ._1());

        // Conclusion:
        // This kind of loose and free coding is great for trying out ideas, but it can be a little
        // hard for the person after you to read, especially with all the _1(), _2(), and _3()
        // methods on the tuples.  Naming your data types well can completely fix the legibility
        // problem.
    }

    // The previous examples could have been cleaned up very easily with ML's type aliases.  Second
    // choice would be to case classes like Scala.  The best we can do in Java is to use objects.
    // At least Tuples give us immutable fields, equals(), hashCode(), and toString() methods and
    // for free!
    static class Email extends Tuple2<EType,String> {
        Email(EType t, String s) { super(t, s); }
        public static Email of(EType t, String s) { return new Email(t, s); }

        // You can give more descriptive names to the field getters
        public EType type() { return _1(); }
        public String address() { return _2(); }
    }

    // Notice in this type signature, we can replace Tuple2<EType,String> with Email
    static class Person extends Tuple3<String,String,ImList<Email>> {
        Person(String f, String l, ImList<Email> es) { super(f, l, es); }
        public static Person of(String f, String l, ImList<Email> es) {
            return new Person(f, l, es);
        }

        // Descriptive names are not used in the examples below, but they are a good idea!
        public String first() { return _1(); }
        public String last() { return _2(); }
        public ImList<Email> emailAddrs() { return _3(); }
    }
    @Test public void dataDefinitionExample3() {

        // The type signatures from the first example become very simple
        ImList<Person> people =
                vec(Person.of("Jane", "Smith", vec(Email.of(HOME, "a@b.c"),
                                                   Email.of(WORK, "b@c.d"))),
                    Person.of("Fred", "Tase", vec(Email.of(HOME, "c@d.e"),
                                                  Email.of(WORK, "d@e.f"))));

        // Notice that the tuples are smart enough to take their new names, Person and Email instead
        // of Tuple3 and Tuple2:
        assertEquals("PersistentVector(" +
                     "Person(Jane,Smith," +
                     "PersistentVector(Email(HOME,a@b.c),Email(WORK,b@c.d)))," +
                     "Person(Fred,Tase," +
                     "PersistentVector(Email(HOME,c@d.e),Email(WORK,d@e.f))))",
                     people.toString());

        // This type signature couldn't be simpler:
        Person jane = people.get(0);

        assertEquals("Person(Jane,Smith," +
                     "PersistentVector(Email(HOME,a@b.c),Email(WORK,b@c.d)))",
                     jane.toString());

        // Let's use our new, descriptive field getter methods:
        assertEquals("Jane", jane.first());
        assertEquals("Smith", jane.last());

        Email janesAddr = jane.emailAddrs().get(0);
        assertEquals(HOME, janesAddr.type());
        assertEquals("a@b.c", janesAddr.address());

        // Another simplified type signature.  Also notice that we are using descriptive method
        // names instead of _1(), _2(), and _3().
        ImMap<String,Person> peopleByEmail =
                people.flatMap(person -> person.emailAddrs()
                                               .map(mail -> tup(mail.address(), person)))
                      .toImMap(Function1.identity());

        assertEquals("PersistentHashMap(" +
                     "Tuple2(d@e.f," +
                     "Person(Fred,Tase,PersistentVector(Email(HOME,c@d.e),Email(WORK,d@e.f))))," +
                     "Tuple2(a@b.c," +
                     "Person(Jane,Smith,PersistentVector(Email(HOME,a@b.c),Email(WORK,b@c.d))))," +
                     "Tuple2(b@c.d," +
                     "Person(Jane,Smith,PersistentVector(Email(HOME,a@b.c),Email(WORK,b@c.d))))," +
                     "Tuple2(c@d.e," +
                     "Person(Fred,Tase,PersistentVector(Email(HOME,c@d.e),Email(WORK,d@e.f)))))",
                     peopleByEmail.toString());

        // Now look them up:
        assertEquals(jane, peopleByEmail.get("b@c.d"));

        // Conclusion:
        // This is the type of Immutable, Object-Oriented, Functional code that any Java shop should
        // appreciate for it's legibility, reliability, and consistency.  All done with less
        // boilerplate than traditional Java coding.  Also, you can start with something brief and
        // dirty, then retrofit step by step to the point where your code is legible and easy to
        // maintain.
        //
        // If you need to write out a complex type like the first example, then it's probably time
        // to define some classes if you want to produce good code.  The pain of ugly type
        // signatures should force you to do the right thing.
    }

}