package com.joelsgarage.util;

import junit.framework.TestCase;

import com.joelsgarage.model.IndividualUtility;
import com.joelsgarage.model.ModelEntity;
import com.joelsgarage.model.ModelEntityFactory;
import com.joelsgarage.util.classutil.AnInterface;
import com.joelsgarage.util.classutil.ClassWithConstructorArgs;
import com.joelsgarage.util.classutil.ClassWithMoreConstructorArgs;
import com.joelsgarage.util.classutil.ClassWithoutConstructorArgs;
import com.joelsgarage.util.dowser.JoinClass;

@SuppressWarnings("nls")
public class ClassUtilTest extends TestCase {

    public void testShortClassName() {
        String name = ClassUtil.shortClassName(ClassUtilTest.class);
        assertEquals("ClassUtilTest", name);
        name = ClassUtil.shortClassName(String.class);
        assertEquals("String", name);
        name = ClassUtil.shortClassName(Object.class);
        assertEquals("Object", name);
    }

    public void testFullClassName() {
        String name = ClassUtil.fullClassName(java.lang.String.class, "Double");
        assertEquals("java.lang.Double", name);
        name = ClassUtil.fullClassName(ModelEntity.class, "IndividualUtility");
        assertEquals("com.joelsgarage.model.IndividualUtility", name);
    }

    public void testClassInPackage() {
        Class<?> clas = ClassUtil.classInPackage(java.lang.String.class, "Double");
        assertEquals(java.lang.Double.class, clas);
        clas = ClassUtil.classInPackage(ModelEntity.class, "IndividualUtility");
        assertEquals(IndividualUtility.class, clas);
    }

    public void testInstantiateInPackage() {
        Object foo =
            ModelEntityFactory.instantiateInPackage(AnInterface.class, "ClassWithoutConstructorArgs", null,
                null);
        assertTrue(foo instanceof ClassWithoutConstructorArgs);

        foo =
            ModelEntityFactory.instantiateInPackage(AnInterface.class, "ClassWithConstructorArgs",
                new Class<?>[] { String.class }, new Object[] { new String("blah") });
        assertTrue(foo instanceof ClassWithConstructorArgs);
        assertEquals("blah", ((ClassWithConstructorArgs) foo).getFoo());

        foo =
            ModelEntityFactory.instantiateInPackage(AnInterface.class, "ClassWithMoreConstructorArgs",
                new Class<?>[] { String.class, int.class }, new Object[] {
                    new String("blah"),
                    Integer.valueOf(2) });

        assertTrue(foo instanceof ClassWithMoreConstructorArgs);
        assertEquals("blah", ((ClassWithMoreConstructorArgs) foo).getFoo());
        assertEquals(2, ((ClassWithMoreConstructorArgs) foo).getBar());

        foo = ModelEntityFactory.instantiateInPackage(JoinClass.class, "nothing", null, null);
        assertNull(foo);
    }
}
