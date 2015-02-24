package com.gmail.mariska.martin.mtginventory.service.utils;

import java.math.BigDecimal;

import com.gmail.mariska.martin.mtginventory.utils.Utils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UtilsTest {

    private Foo foo1;
    private Foo2 foo2;

    @Before
    public void beforeTests() {
        foo1 = new Foo("dasda", 11, new BigDecimal(132));
        foo2 = new Foo2("dasda", 11, 12, new BigDecimal(132));
    }

    @Test
    public void itShouldHasSameFieldsTest() throws NoSuchFieldException {
        Assert.assertFalse(Utils.hasChange(foo1, foo2, "field1", "field2", "f4"));
        foo2.setField1("xy");
        Assert.assertTrue(Utils.hasChange(foo1, foo2, "field1", "field2", "f4"));
        Assert.assertTrue(Utils.hasChange(foo1, foo2, "f3"));
    }

    @Test
    public void itShouldHasChangesAfterChangeFieldTest() throws NoSuchFieldException {
        foo2.setField1("xy");
        Assert.assertTrue(Utils.hasChange(foo1, foo2, "field1", "field2", "f4"));
    }

    @Test
    public void itShouldReturnChangeWhenObjectNotHaveFieldTest() throws NoSuchFieldException {
        Assert.assertTrue(Utils.hasChange(foo1, foo2, "f3"));
    }

    @SuppressWarnings("UnusedDeclaration")
    public static class Foo {
        private String field1;
        private int field2;
        private BigDecimal f4;

        public Foo(String field1, int field2, BigDecimal f4) {
            super();
            this.field1 = field1;
            this.field2 = field2;
            this.f4 = f4;
        }

        public String getField1() {
            return field1;
        }

        public void setField1(String field1) {
            this.field1 = field1;
        }

        public int getField2() {
            return field2;
        }

        public void setField2(int field2) {
            this.field2 = field2;
        }

        public BigDecimal getF4() {
            return f4;
        }

        public void setF4(BigDecimal f4) {
            this.f4 = f4;
        }
    }

    public static class Foo2 extends Foo {
        private long f3;

        public Foo2(String field1, int field2, long x, BigDecimal f4) {
            super(field1, field2, f4);
            this.f3 = x;
        }

        public long getF3() {
            return f3;
        }
    }
}
