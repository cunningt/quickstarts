package org.switchyard.components;

/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;

import static org.jboss.arquillian.api.RunModeType.AS_CLIENT;
import org.jboss.arquillian.api.Run;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;

import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.ResourceAdapterArchive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.importer.ZipImporter;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.Assert;
import org.junit.Test;

import org.switchyard.quickstarts.m1app.InventoryService;
import org.switchyard.quickstarts.m1app.OrderService;
import org.switchyard.component.bean.Reference;

import java.util.Properties;
import java.util.zip.ZipFile;
import org.apache.log4j.Logger;

/**
 * This is a sample Arquillian test that deploys m1app, and tests it through the web service, exactly like
 * WebServiceTest.java in m1app.
 * 
 * There are a currently a number of TODO associated with arquillian support :
 * - SWITCHYARD-157 is blocking injecting the services into the test directly and testing using the
 * service beans.   The goal is that tests will @Run(IN_CONTAINER) as opposed to @Run(AS_CLIENT).
 * 
 * - the next version of Arquillian will support importing archives directly from maven resolutions - that
 * will be a lot cleaner than the way the archive is found today, where the maven dependency plugin is grabbing
 * the m1app.jar and leaving it in the ${project.build.directory} for the test. 
 * 
 * - it may be nice in the future to also deploy the deployer and the component-bean and component-soap libraries
 * rather than simple copying them out to the classpath.
 * 
 * @author tcunning 
 */
@RunWith(Arquillian.class)
@Run(AS_CLIENT)
public class SampleArquillianTest {
    private static Logger _logger = Logger.getLogger(SampleArquillianTest.class);
    private static final String BUILD_DIRECTORY = "maven.build.directory"; 
    private static final String JAR_FILE_NAME = "m1app.jar";

    private InputStream _requestStream;
    private Reader _expectedResponseReader;
    
    @Before
    public void setUp() throws Exception {
        _requestStream = getClass().getClassLoader().getResourceAsStream("soap-request.xml");
        InputStream responseStream =
            getClass().getClassLoader().getResourceAsStream("soap-response.xml");
        _expectedResponseReader = new InputStreamReader(responseStream);
    }

    @After
    public void tearDown() throws Exception {
        if (_expectedResponseReader != null) {
            _expectedResponseReader.close();
        }
        if (_requestStream != null) {
            _requestStream.close();
        }
    }
    
    @Deployment
    public static JavaArchive createTestArchive() {

       Properties prop = System.getProperties();
       String m1appLocation = (String) prop.get(BUILD_DIRECTORY);       
       
       _logger.debug("Testing " + m1appLocation + File.separator + JAR_FILE_NAME);
       System.out.println("Testing " + m1appLocation + File.separator + JAR_FILE_NAME);
       File m1app = new File(m1appLocation + File.separator + JAR_FILE_NAME);
       JavaArchive javaArchive = ShrinkWrap.create(JavaArchive.class, "test.jar");
       try {
	   javaArchive.as(ZipImporter.class).importZip(new ZipFile(m1app));
       } catch (Exception e) {
	   _logger.error(e);
       }

       _logger.debug("test.jar contains " + javaArchive.toString(true));
       System.out.println("test.jar contains " + javaArchive.toString(true));
       
       return javaArchive;
    }

    @Test
    public void testM1app() {
	HttpClient client = new HttpClient();
	String output;
	try {
            PostMethod postMethod = new PostMethod("http://localhost:18001/OrderService");
            
            postMethod.setRequestEntity(new InputStreamRequestEntity(_requestStream, "text/xml; charset=utf-8"));
            client.executeMethod(postMethod);
            output = postMethod.getResponseBodyAsString();
     
            XMLUnit.setIgnoreWhitespace(true);
            XMLAssert.assertXMLEqual(_expectedResponseReader, new StringReader(output));
	} catch (Exception e) {
	  Assert.fail(e.toString());
	}
    }
}
