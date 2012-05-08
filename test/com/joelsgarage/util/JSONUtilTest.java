/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.util;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class JSONUtilTest extends TestCase {

    public String printArray(int indent, JSONArray ja) {
        String output = "[\n";
        for (int index = 0; index < ja.length(); ++index) {
            output += "\n";
            for (int i2 = 0; i2 < indent; ++i2) {
                output += " ";
            }
            try {
                Object o = ja.get(index);
                output += String.valueOf(index) + " : ";
                if (o instanceof JSONArray) {
                    output += printArray(indent + 2, (JSONArray) o);
                } else if (o instanceof JSONObject) {
                    output += printObject(indent + 2, (JSONObject) o);
                } else {
                    output += o.toString();
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        output += "]";
        return output;
    }

    public String printObject(int indent, JSONObject jo) {
        String output = "{\n";
        for (String k : JSONObject.getNames(jo)) {
            output += "\n";
            for (int i2 = 0; i2 < indent; ++i2) {
                output += " ";
            }
            try {
                Object o = jo.get(k);
                output += k + " : ";
                if (o instanceof JSONArray) {
                    output += printArray(indent + 2, (JSONArray) o);
                } else if (o instanceof JSONObject) {
                    output += printObject(indent + 2, (JSONObject) o);
                } else {
                    output += o.toString();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        output += "}";
        return output;
    }

    public static class Salary {
        int basicPay = -1;

        public Salary(int basicPay) {
            this.basicPay = basicPay;
        }

        public int getBasicPay() {
            return basicPay;
        }

        public void setBasicPay(int basicPay) {
            this.basicPay = basicPay;
        }

    }

    public static class Employee {
        int age;
        String name;
        Salary sal;
        Integer intge = new Integer(77);
        Boolean status = new Boolean(false);
        String date;

        Salary[] salArray = { new Salary(30), new Salary(40) };

        List l = new ArrayList();

        {
            l.add(new Salary(301));
            l.add(new Salary(401));
        };

        Hashtable lMap = new Hashtable();

        {
            lMap.put("SAL-1", new Salary(3011));
            lMap.put("SAL-2", new Salary(4012));
        };

        public Employee(int age, String name, Salary sal) {
            super();
            this.age = age;
            this.name = name;
            this.sal = sal;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public Integer getIntge() {
            return intge;
        }

        public void setIntge(Integer intge) {
            this.intge = intge;
        }

        public List getL() {
            return l;
        }

        public void setL(List l) {
            this.l = l;
        }

        public Hashtable getLMap() {
            return lMap;
        }

        public void setLMap(Hashtable map) {
            lMap = map;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Salary getSal() {
            return sal;
        }

        public void setSal(Salary sal) {
            this.sal = sal;
        }

        public Salary[] getSalArray() {
            return salArray;
        }

        public void setSalArray(Salary[] salArray) {
            this.salArray = salArray;
        }

        public Boolean getStatus() {
            return status;
        }

        public void setStatus(Boolean status) {
            this.status = status;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }

    /**
     * The type of a key is always String. The type of value is either String or Map or Array.
     * 
     * @author joel
     */

    public String expectedOutput() {
        return "{" //

            + "\"age\":11," //
            + "\"name\":\"Biju\"," //
            + "\"sal\":{\"basicPay\":100}," //
            + "\"intge\":77," //
            + "\"status\":false," //
            + "\"date\":\n  \"fake date\"," //
            + "\"salArray\":[" //
            + "{\"basicPay\":30}," //
            + "{\"basicPay\":40}" //
            + "]," //
            + "\"l\":[" //
            + "{\"basicPay\":301},\n" //
            + "      {\"basicPay\":401}" //
            + "]," //     
            + "\"lMap\":{" //
            + "\"SAL-2\":{\"basicPay\":4012}," //
            + "\"SAL-1\":{\"basicPay\":\n      3011}" //
            + "}" //
            + "}\n";
    }

    public String jsonExpectedOutput() {
        return "{\n" //
            + " \"LMap\": {\n" //
            + "  \"SAL-1\": {\"basicPay\": 3011},\n" //
            + "  \"SAL-2\": {\"basicPay\": 4012}\n" //
            + " },\n" //
            + " \"age\": 11,\n" //
            + " \"date\": \"fake date\",\n" //
            + " \"intge\": 77,\n" //
            + " \"l\": [\n" //
            + "  {\"basicPay\": 301},\n" //
            + "  {\"basicPay\": 401}\n" //
            + " ],\n" //
            + " \"name\": \"Biju\",\n" //
            + " \"sal\": {\"basicPay\": 100},\n" //
            + " \"salArray\": [\n" //
            + "  {\"basicPay\": 30},\n" //
            + "  {\"basicPay\": 40}\n" //
            + " ],\n" //
            + " \"status\": false\n" //
            + "}";
    }

    public static String foo() {
        Employee p = new Employee(11, "Biju", new Salary(100));
        p.setDate("fake date");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(p);
        return json;
    }

    public void testSimple() {
        String foo = foo();
        assertEquals(expectedOutput(), foo);
    }

    public void testSimple2() {
        Employee p = new Employee(11, "Biju", new Salary(100));
        p.setDate("fake date");
        JSONObject obj = new JSONObject(p, false);
        try {
            String actualOutput = obj.toString(1);
            assertEquals(jsonExpectedOutput(), actualOutput);
        } catch (JSONException e) {
            e.printStackTrace();
            fail();
        }
    }

    public static String inputTwo() {
        return " [ {" //
            + "  \"!/film/performance/film\" : [ {" //
            + "    \"actor\" : [ {" //
            + "      \"/people/person/date_of_birth\" : null," //
            + "      \"/people/person/date_of_birth<\" : \"1970\"," //
            + "      \"/people/person/date_of_birth>\" : \"1960\"," //
            + "      \"/people/person/gender\" : \"male\"," //
            + "      \"name\" : null," //
            + "      \"type\" : \"/film/actor\"" //
            + "    } ]," //
            + "    \"type\" : \"/film/performance\"" //
            + "  } ]," //
            + "  \"name\" : null," //
            + "  \"type\" : \"/film/film\"" //
            + "} ]";
    }

    /** This does not work, it throws a NPE. */
    // public void testTwo() {
    // Object o = GeneralObjectDeserializer.fromJson(inputTwo());
    // assertTrue(o instanceof Object[]);
    // Object[] a = (Object[]) o;
    //
    // assertEquals(1, a.length);
    //
    // Object o2 = a[0];
    //
    // assertTrue(o2 instanceof Map);
    //
    // Map<String, Object> m = Map.class.cast(o2);
    //
    // for (Map.Entry<String, Object> entry : m.entrySet()) {
    // String k = entry.getKey();
    // Object o3 = entry.getValue();
    // System.out.println("k: " + k + " v: " + o3.toString());
    // }
    // }
    
    public void testThree() {
        try {
            System.out.println(printArray(0, new JSONArray(inputTwo())));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail();
        }
    }

}
