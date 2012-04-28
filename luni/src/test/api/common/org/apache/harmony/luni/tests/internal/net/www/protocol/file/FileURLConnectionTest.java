/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.harmony.luni.tests.internal.net.www.protocol.file;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import junit.framework.TestCase;

import libcore.net.url.FileURLConnection;

/**
 * Tests for <code>FileURLConnection</code> class constructors and methods.
 */
public class FileURLConnectionTest extends TestCase {

    static String getContentType(String fileName) throws IOException {
        String resourceName = "org/apache/harmony/luni/tests/" + fileName;
        URL url = ClassLoader.getSystemClassLoader().getResource(resourceName);
        assertNotNull("Cannot find test resource " + resourceName, url);
        return new FileURLConnection(url).getContentType();
    }

    public void testGetContentType() throws IOException  {
        // Regression for HARMONY-4699
        assertEquals("application/rtf", getContentType("test.rtf"));
        assertEquals("text/plain", getContentType("test.java"));
        // RI would return "content/unknown"
        assertEquals("application/msword", getContentType("test.doc"));
        assertEquals("text/html", getContentType("test.htx"));
        assertEquals("application/xml", getContentType("test.xml"));
        assertEquals("text/plain", getContentType("."));
    }

    public void testGetInputStream() throws IOException {
        // Regression for Harmony-5737
        String resourceName = "org/apache/harmony/luni/tests/" + "test.rtf";
        URL url = ClassLoader.getSystemClassLoader().getResource(resourceName);
        URL anchorUrl = new URL(url,"#anchor");
        assertNotNull("Cannot find test resource " + resourceName, anchorUrl);

        FileURLConnection conn = new FileURLConnection(anchorUrl);
        assertNotNull(conn.getInputStream());

        // Regression for Harmony-5779
        String localURLString = "file://localhost/" + url.getFile();
        URL localURL = new URL(localURLString);
        conn = new FileURLConnection(localURL);
        assertNotNull(conn.getInputStream());
        assertEquals("file",conn.getURL().getProtocol());
    }

    public void testHeaderFunctions() throws IOException {
        String resourceName = "org/apache/harmony/luni/tests/";  //folder name
        URL url = ClassLoader.getSystemClassLoader().getResource(resourceName);
        FileURLConnection conn = new FileURLConnection(url);
        assertNotNull(conn.getInputStream());
        assertEquals(conn.getContentType(),  conn.getHeaderField("content-type")) ;

        resourceName = "org/apache/harmony/luni/tests/" +  "test.rtf";;  //folder name
        url = ClassLoader.getSystemClassLoader().getResource(resourceName);
        conn = new FileURLConnection(url);
        assertNotNull(conn.getInputStream());
        assertEquals(conn.getContentType(),  conn.getHeaderField("content-type")) ;
        assertEquals(Integer.toString(conn.getContentLength()),  conn.getHeaderField("content-length")) ;
        assertEquals(conn.getHeaderField(0), conn.getHeaderField("content-type"));
        assertEquals(conn.getHeaderField(1), conn.getHeaderField("content-length"));
        assertEquals(conn.getHeaderField(2), conn.getHeaderField("last-modified"));
        assertEquals("last-modified", conn.getHeaderFieldKey(2));
        assertEquals("content-length", conn.getHeaderFieldKey(1));
        assertEquals("content-type", conn.getHeaderFieldKey(0));
    }

    public void testHeader_BoundaryCheck() throws IOException {
        String resourceName = "org/apache/harmony/luni/tests/";
        URL url = ClassLoader.getSystemClassLoader().getResource(resourceName);
        URLConnection urlConnection = url.openConnection();
        assertNull(urlConnection.getHeaderField(Integer.MIN_VALUE));
        assertNull(urlConnection.getHeaderField(Integer.MAX_VALUE));
        assertNull(urlConnection.getHeaderFieldKey(Integer.MIN_VALUE));
        assertNull(urlConnection.getHeaderFieldKey(Integer.MAX_VALUE));
        assertNull(urlConnection.getHeaderField(null));
    }
}
