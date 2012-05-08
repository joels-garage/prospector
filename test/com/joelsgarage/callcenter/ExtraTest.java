/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.callcenter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import junit.framework.TestCase;

import org.apache.xerces.xni.parser.XMLInputSource;
import org.cyberneko.html.HTMLConfiguration;
import org.cyberneko.html.filters.DefaultFilter;

/**
 * I think these are extraneous things I did while searching for a typo. But since I hate to delete
 * anything ever, here they are. They're all related to GATE.
 * 
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class ExtraTest extends TestCase {

    /** Instantiate HTMLConfiguration */
    public void testNeko() {
        HTMLConfiguration conf = new HTMLConfiguration();
        conf.cleanup();
    }

    /** Instantiate XMLInputSource */
    public void testXerces() {
        XMLInputSource src = new XMLInputSource("", "", "");
        src.setBaseSystemId("");
    }

    /** Instantiate DefaultFilter */
    public void testNeko2() {
        DefaultFilter f = new DefaultFilter();
        f.toString();
    }

    /** What's my version? */
    public void testXercesVersion() {
        System.out.println("xerces version: "
            + String.valueOf(org.apache.xerces.impl.Version.getVersion()));
    }

    /** What's my version? */
    public void testNekoVersion() {
        System.out.println("neko version: "
            + String.valueOf(org.cyberneko.html.Version.getVersion()));
    }

    /** Print detailed verison info */
    public void testNekoVersion2() {
        Class<?> cls;
        try {
            cls = Class.forName("org.cyberneko.html.HTMLConfiguration");

            Package pkg = cls.getPackage();

            System.out.println(" name " + String.valueOf(pkg.getName()));

            System.out.println("  specTitle " + String.valueOf(pkg.getSpecificationTitle()));
            System.out.println("  specVendor " + String.valueOf(pkg.getSpecificationVendor()));
            System.out.println("  specVersion " + String.valueOf(pkg.getSpecificationVersion()));

            System.out.println("  implTitle " + String.valueOf(pkg.getImplementationTitle()));
            System.out.println("  implVendor " + String.valueOf(pkg.getImplementationVendor()));
            System.out.println("  implVersion " + String.valueOf(pkg.getImplementationVersion()));
        } catch (ClassNotFoundException e) {
            System.out.println("FUCK!");

            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void testConfigLoad() {
        // Verify we can load the config file
        try {
            File userConfigFile =
                new File(this.getClass().getClassLoader().getResource("gateexamples/user-gate.xml")
                    .toURI());
            InputStream inp = new FileInputStream(userConfigFile);
            int b = 0;
            StringBuffer buf = new StringBuffer();
            while ((b = inp.read()) != -1) {
                buf.append((char) b);
            }
            System.out.println("input was " + String.valueOf(buf));
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
