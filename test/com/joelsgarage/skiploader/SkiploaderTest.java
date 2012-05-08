/*
 * Copyright 2008 Joel Truher Author: Joel Truher (joel@truher.org)
 */
package com.joelsgarage.skiploader;

import junit.framework.TestCase;

/**
 * Verify that various inputs result in the correct DB calls.
 * 
 * @author joel
 * 
 */
@SuppressWarnings("nls")
public class SkiploaderTest extends TestCase {
    public void testNothing() {
        assertTrue(true);
    }
    //
    // MockDataAccess dataAccess;
    // Skiploader skiploader;
    //
    // @Override
    // protected void setUp() {
    // this.dataAccess = new MockDataAccess();
    // this.skiploader = new Skiploader(this.dataAccess);
    // }
    //
    // protected String getTestInputString() {
    // return getContainer(getAnEntity());
    // }
    //
    // protected String getAnEntity() {
    // return "<Class>\n" //
    // + " <key>\n" //
    // + " <namespace>p1</namespace>\n" //
    // + " <type>foo</type>\n" //
    // + " <key>1</key>\n" //
    // + " </key>\n" //
    // + " <creatorId>0</creatorId>\n" //
    // + " <name>Presidential Candidate</name>\n" //
    // + " <description>foo</description>\n" //
    // + "</Class>";
    // }
    //
    // protected String getDiffTestInputString() {
    // return getContainer(getConflictEntity1(), getNewEntity());
    // }
    //
    // protected String getConflict(String... classes) {
    // String foo = "<conflict>\n";
    // for (String s : classes) {
    // for (String l : s.split("\n")) {
    // foo += " " + l + "\n";
    // }
    // }
    // foo += "</conflict>";
    // return foo;
    // }
    //
    // protected String getContainer(String... classes) {
    // // Note the fiddly newlines and spacing. crazy.
    // String foo = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" //
    // + "<container>\n";
    // for (String s : classes) {
    // foo += s;
    // }
    // foo += "</container>";
    // return foo;
    // }
    //
    // protected String getMissingEntity() {
    // return "<Class>\n" //
    // + " <key>\n" //
    // + " <namespace>p1</namespace>\n" //
    // + " <type>foo</type>\n" //
    // + " <key>2</key>\n" // same as "new" above; should still be "missing"
    // + " </key>\n" //
    // + " <creatorId>0</creatorId>\n" //
    // + " <name>Something Missing</name>\n" //
    // + " <description>hah</description>\n" //
    // + "</Class>";
    // }
    //
    // protected String getNewEntity() {
    // return "<Class> \n" //
    // + " <key> \n" //
    // + " <namespace>p1</namespace> \n" //
    // + " <type>foo</type> \n" //
    // + " <key>0</key> \n" //
    // + " </key> \n" //
    // + " <creatorId>0</creatorId> \n" //
    // + " <name>A New Entity</name> \n" //
    // + " <description>hoo hah</description> \n" //
    // + "</Class>";
    // }
    //
    // protected String getConflictEntity1() {
    // return "<Class> \n" //
    // + " <key> \n" //
    // + " <namespace>p1</namespace> \n" //
    // + " <type>foo</type> \n" //
    // + " <key>1</key> \n" //
    // + " </key> \n" //
    // + " <creatorId>0</creatorId> \n" //
    // + " <name>Presidential Candidate</name> \n" //
    // + " <description>foo</description> \n" //
    // + "</Class>\n";
    // }
    //
    // // db version
    // protected String getConflictEntity2() {
    // return "<Class>\n" //
    // + " <key>\n" //
    // + " <namespace>p1</namespace>\n" //
    // + " <type>foo</type>\n" //
    // + " <key>1</key>\n" //
    // + " </key>\n" //
    // + " <creatorId>0</creatorId>\n" //
    // + " <name>Presidential Conflict</name>\n" //
    // + " <description>hooey</description>\n" //
    // + "</Class>\n";
    // }
    //
    // /**
    // * Return serialized XML test input as an InputStream.
    // *
    // * @return
    // */
    // protected ByteArrayInputStream getTestInputStream() {
    // // this is a throwaway
    // String foo = getTestInputString();
    // // makes a copy into buf
    // byte[] buf = foo.getBytes();
    // // holds a reference to buf, so we can throw away ours
    // ByteArrayInputStream input = new ByteArrayInputStream(buf);
    // return input;
    // }
    //
    // protected ByteArrayInputStream getDiffTestInputStream() {
    // // this is a throwaway
    // String foo = getDiffTestInputString();
    // // makes a copy into buf
    // byte[] buf = foo.getBytes();
    // // holds a reference to buf, so we can throw away ours
    // ByteArrayInputStream input = new ByteArrayInputStream(buf);
    // return input;
    // }
    //
    // /**
    // * load the element array version of the above into the "database"
    // */
    // protected void loadTestInput() {
    // Element classElement = DocumentHelper.createElement("Class");
    // Element keyElement = classElement.addElement("key");
    // Element nsElement = keyElement.addElement("namespace");
    // Element typeElement = keyElement.addElement("type");
    // Element keyKeyElement = keyElement.addElement("key");
    // nsElement.setText("p1");
    // typeElement.setText("foo");
    // keyKeyElement.setText("1");
    // Element creatorIdElement = classElement.addElement("creatorId");
    // Element nameElement = classElement.addElement("name");
    // Element descriptionElement = classElement.addElement("description");
    // creatorIdElement.setText("0");
    // nameElement.setText("Presidential Candidate");
    // descriptionElement.setText("foo");
    //
    // List<Element> elementList = new ArrayList<Element>();
    // elementList.add(classElement);
    //
    // this.dataAccess.setElements(elementList);
    // }
    //
    // protected void loadDiffTestInput() {
    // List<Element> elementList = new ArrayList<Element>();
    //
    // {
    // Element classElement = DocumentHelper.createElement("Class");
    //
    // Element keyElement = classElement.addElement("key");
    // Element namespaceElement = keyElement.addElement("namespace");
    // namespaceElement.setText("p1");
    // Element typeElement = keyElement.addElement("type");
    // typeElement.setText("foo");
    // Element keyKeyElement = keyElement.addElement("key");
    // keyKeyElement.setText("1"); // note conflict
    //
    // Element creatorIdElement = classElement.addElement("creatorId");
    // Element nameElement = classElement.addElement("name");
    // Element descriptionElement = classElement.addElement("description");
    // creatorIdElement.setText("0");
    // nameElement.setText("Presidential Conflict");
    // descriptionElement.setText("hooey");
    //
    // elementList.add(classElement);
    // }
    // {
    // Element classElement = DocumentHelper.createElement("Class");
    //
    // Element keyElement = classElement.addElement("key");
    // Element namespaceElement = keyElement.addElement("namespace");
    // namespaceElement.setText("p1");
    // Element typeElement = keyElement.addElement("type");
    // typeElement.setText("foo");
    // Element keyKeyElement = keyElement.addElement("key");
    // keyKeyElement.setText("2");
    //
    // Element creatorIdElement = classElement.addElement("creatorId");
    // Element nameElement = classElement.addElement("name");
    // Element descriptionElement = classElement.addElement("description");
    //
    // creatorIdElement.setText("0");
    // nameElement.setText("Something Missing");
    // descriptionElement.setText("hah");
    //
    // elementList.add(classElement);
    // }
    //
    // this.dataAccess.setElements(elementList);
    // }
    //
    // protected List<Element> getTestElements() {
    // List<Element> elements = new ArrayList<Element>();
    // return elements;
    // }
    //
    // /**
    // * No-op.
    // */
    // public void testNothing() {
    // assertNotNull(this.skiploader);
    // }
    //
    // /** Split an inputstream into pieces */
    // public void testLoadArc() {
    // final List<String> strings = new ArrayList<String>();
    //
    // this.skiploader = new Skiploader(null) {
    // @Override
    // protected void load(String database, InputStream input) throws FatalException {
    // byte[] buf = new byte[1024];
    // StringBuilder builder = new StringBuilder();
    // try {
    // // read the whole thing
    // int len = 0;
    // while ((len = input.read(buf)) > 0) {
    // builder.append(new String(buf, 0, len));
    // }
    // strings.add(builder.toString());
    // } catch (IOException e) {
    // throw new FatalException(e);
    // }
    // }
    // };
    //
    // String xmlA = "<?xml version=\"1.0\"?><foo></foo>";
    // String xmlB = "<?xml version=\"1.0\"?><bar></bar>";
    // String xml = xmlA + xmlB;
    // ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes());
    // try {
    // this.skiploader.loadArc(null, input);
    // } catch (FatalException e) {
    // fail();
    // }
    // assertEquals(2, strings.size());
    // assertEquals(xmlA, strings.get(0));
    // assertEquals(xmlB, strings.get(1));
    // }
    //
    // /** Split an inputstream into pieces */
    // public void testLoadArcAgain() {
    // final List<String> strings = new ArrayList<String>();
    //
    // this.skiploader = new Skiploader(null) {
    // @Override
    // protected void load(String database, InputStream input) throws FatalException {
    // byte[] buf = new byte[1024];
    // StringBuilder builder = new StringBuilder();
    // try {
    // // read the whole thing
    // int len = 0;
    // while ((len = input.read(buf)) > 0) {
    // builder.append(new String(buf, 0, len));
    // }
    // strings.add(builder.toString());
    // } catch (IOException e) {
    // throw new FatalException(e);
    // }
    // }
    // };
    //
    // String xmlA = "<?xml version=\"1.0\"?><foo></foo>";
    // String xmlB = "<?xml version=\"1.0\"?><bar></bar>";
    // String xmlC = "<?xml version=\"1.0\"?><foo></bar>";
    // String xmlD = "<?xml version=\"1.0\"?><bar></foo>";
    //
    // String xml = xmlA + xmlB + xmlC + xmlD;
    // ByteArrayInputStream input = new ByteArrayInputStream(xml.getBytes());
    // try {
    // this.skiploader.loadArc(null, input);
    // } catch (FatalException e) {
    // fail();
    // }
    // assertEquals(4, strings.size());
    // assertEquals(xmlA, strings.get(0));
    // assertEquals(xmlB, strings.get(1));
    // assertEquals(xmlC, strings.get(2));
    // assertEquals(xmlD, strings.get(3));
    // }
    //
    // public void testInput() {
    // InputStream inputStream = getTestInputStream();
    // String inputString = new String();
    // try {
    // byte[] buf = new byte[1];
    // while (inputStream.read(buf) != -1) {
    // inputString += new String(buf);
    // }
    // } catch (IOException e) {
    // fail();
    // }
    //
    // String expectedInput = getTestInputString();
    // assertEquals(expectedInput, inputString);
    // }
    //
    // /**
    // * Verify that the loadProcess() method transforms an inputstream full of XML, into the
    // correct
    // * "database" writes.
    // */
    // public void testLoadProcess() {
    // InputStream input = getTestInputStream();
    // RecordReader<Element> recordReader = new XMLFileRecordReader(input);
    // ListRecordWriter<ModelEntity> writer = new ListRecordWriter<ModelEntity>();
    //
    // this.skiploader.loadProcess(recordReader, writer);
    //
    // List<ModelEntity> list = writer.getList();
    // assertEquals(1, list.size());
    // assertEquals("foo/p1/1", list.get(0).getKey().toString());
    // }
    //
    // /**
    // * Verify that the dumpProcess() method transforms a database fetch into the correct
    // * outputstream XML.
    // */
    //
    // public void testDumpProcess() {
    // loadTestInput();
    //
    // ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    //
    // List<ModelEntity> modelEntityList = new ArrayList<ModelEntity>();
    //
    // ModelEntity modelEntity = new Individual();
    // modelEntity.setName("individual foo");
    // modelEntityList.add(modelEntity);
    // modelEntity = new User();
    // modelEntity.setName("user foo");
    // modelEntityList.add(modelEntity);
    //
    // RecordReader<ModelEntity> recordReader = new ListRecordReader<ModelEntity>(modelEntityList);
    //
    // RecordWriter<Element> writer = new XMLFileRecordWriter(outputStream);
    //
    // this.skiploader.dumpProcess(recordReader, writer);
    //
    // String outputString = outputStream.toString();
    // String expectedString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<container>\n" //
    // + "<Individual>\n" //
    // + " <creatorKey>\n" //
    // + " <key></key>\n" //
    // + " <namespace></namespace>\n"//
    // + " <type></type>\n" //
    // + " </creatorKey>\n" //
    // + " <key>\n" //
    // + " <key></key>\n"//
    // + " <namespace></namespace>\n"//
    // + " <type></type>\n"//
    // + " </key>\n"//
    // + " <lastModified></lastModified>\n"//
    // + " <name>individual foo</name>\n"//
    // + "</Individual>\n"//
    // + "<User>\n"//
    // + " <admin>false</admin>\n"//
    // + " <cookie></cookie>\n"//
    // + " <creatorKey>\n"//
    // + " <key></key>\n"//
    // + " <namespace></namespace>\n"//
    // + " <type></type>\n"//
    // + " </creatorKey>\n"//
    // + " <emailAddress></emailAddress>\n"//
    // + " <guest>false</guest>\n"//
    // + " <key>\n"//
    // + " <key></key>\n"//
    // + " <namespace></namespace>\n"//
    // + " <type></type>\n"//
    // + " </key>\n"//
    // + " <lastModified></lastModified>\n"//
    // + " <name>user foo</name>\n"//
    // + " <password></password>\n"//
    // + " <realName></realName>\n"//
    // + "</User></container>";
    //
    // assertEquals(expectedString, outputString);
    // }
    //
    // /**
    // * Verify that the dumpProcess() method transforms a database fetch into the correct
    // * outputstream XML.
    // */
    //
    // public void testDumpGateProcess() {
    // loadTestInput();
    //
    // ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    //
    // List<ModelEntity> modelEntityList = new ArrayList<ModelEntity>();
    //
    // ModelEntity modelEntity = new Individual();
    // modelEntity.setName("individual foo");
    // modelEntity.setKey(new ExternalKey("little", "bunny", "foofoo"));
    // modelEntityList.add(modelEntity);
    // modelEntity = new User();
    // modelEntity.setName("user foo");
    // modelEntity.setKey(new ExternalKey("hopping", "through the", "forest"));
    // modelEntityList.add(modelEntity);
    //
    // RecordReader<ModelEntity> recordReader = new ListRecordReader<ModelEntity>(modelEntityList);
    //
    // RecordWriter<String> writer = new StringRecordWriter(outputStream);
    //
    // this.skiploader.dumpGateProcess(recordReader, writer);
    //
    // String outputString = outputStream.toString();
    // String expectedString = ""//
    // + "individual foo&type=individual&key=bunny/little/foofoo\n"//
    // + "user foo&type=user&key=through the/hopping/forest\n";
    //
    // assertEquals(expectedString, outputString);
    // }
    //
    // public void testDiff() {
    // InputStream input = getDiffTestInputStream();
    // loadDiffTestInput();
    // ByteArrayOutputStream missingOutputStream = new ByteArrayOutputStream();
    // ByteArrayOutputStream newOutputStream = new ByteArrayOutputStream();
    // ByteArrayOutputStream conflictOutputStream = new ByteArrayOutputStream();
    //
    // try {
    // this.skiploader.diff(input, missingOutputStream, newOutputStream, conflictOutputStream);
    // //$NON-NLS-1$
    // // this.skiploader.diff(input,
    // // "namespace is ignored", missingOutputStream, newOutputStream, conflictOutputStream);
    // // //$NON-NLS-1$
    // } catch (FatalException e) {
    // fail();
    // }
    //
    // String missingOutput = missingOutputStream.toString();
    // String newOutput = newOutputStream.toString();
    // String conflictOutput = conflictOutputStream.toString();
    //
    // assertEquals(getContainer(getMissingEntity()), missingOutput);
    // assertEquals(getContainer(getNewEntity()), newOutput);
    // assertEquals(getContainer(getConflict(getConflictEntity1(), getConflictEntity2())),
    // conflictOutput);
    // }
    //
    // public void testGetElementsFromDB() {
    // loadTestInput();
    // try {
    // List<Element> elementList = this.skiploader.getElementsFromDB();
    // assertEquals(1, elementList.size());
    // Element classElement = elementList.get(0);
    // assertEquals("Class", classElement.getName());
    // verifyElementChildren(classElement);
    // } catch (FatalException e) {
    // fail();
    // }
    // }
    //
    // /**
    // * Test the SAXReader, which is wrapped in a static method, i.e. does almost nothing.
    // */
    // public void testReadDocument() {
    // InputStream input = getTestInputStream();
    // try {
    // Document testDoc = SkiploaderUtil.readDocumentFromStream(input);
    // Element root = testDoc.getRootElement();
    // assertEquals("container", root.getName());
    // Iterator<?> rootIter = root.elementIterator();
    // assertTrue(rootIter.hasNext());
    // Object o = rootIter.next();
    // if (o instanceof Element) {
    //                Element element = (Element) o;
    //                assertEquals("Class", element.getName());
    //                verifyElementChildren(element);
    //
    //            } else {
    //                fail();
    //            }
    //            assertFalse(rootIter.hasNext());
    //        } catch (FatalException e) {
    //            fail();
    //        }
    //    }
    //
    //    protected void verifyElementChildren(Element element) {
    //        Iterator<?> classIter = element.elementIterator();
    //        List<Element> list = new ArrayList<Element>();
    //        while (classIter.hasNext()) {
    //            Object classObject = classIter.next();
    //            if (classObject instanceof Element) {
    //                Element classElement = (Element) classObject;
    //                list.add(classElement);
    //            } else {
    //                fail();
    //            }
    //        }
    //        assertEquals(4, list.size());
    //        assertEquals("key", list.get(0).getName());
    //        assertEquals(3, list.get(0).elements().size());
    //        assertEquals("namespace", ((Element) list.get(0).elements().get(0)).getName());
    //        assertEquals("p1", ((Element) list.get(0).elements().get(0)).getText());
    //        assertEquals("type", ((Element) list.get(0).elements().get(1)).getName());
    //        assertEquals("foo", ((Element) list.get(0).elements().get(1)).getText());
    //        assertEquals("key", ((Element) list.get(0).elements().get(2)).getName());
    //        assertEquals("1", ((Element) list.get(0).elements().get(2)).getText());
    //        assertEquals("creatorId", list.get(1).getName());
    //        assertEquals("0", list.get(1).getText());
    //        assertEquals("name", list.get(2).getName());
    //        assertEquals("Presidential Candidate", list.get(2).getText());
    //        assertEquals("description", list.get(3).getName());
    //        assertEquals("foo", list.get(3).getText());
    //    }

}
