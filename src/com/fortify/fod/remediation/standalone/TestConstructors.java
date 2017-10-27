package com.fortify.fod.remediation.standalone;

/**
 * Created by jazl on 10/6/2017.
 */
public class TestConstructors extends BaseClass {
    public TestConstructors(String msg) {
        //super(msg);
        System.out.println("Sub class constructed! msg = "+msg);
    }
    public static void main(String[] args) {
        String s = "oh hai sharon!";
        TestConstructors test = new TestConstructors(s);
    }
}


class BaseClass {
    public BaseClass() {
        System.out.println("Super class default constructor called");
    }
    public BaseClass(String msg) {
        System.out.println("Super class constructed! msg = "+msg);
    }
}

