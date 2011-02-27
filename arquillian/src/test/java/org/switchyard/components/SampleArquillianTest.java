package org.switchyard.components;

import java.io.File;

import org.switchyard.components.TemperatureConverter;

import org.junit.runner.RunWith;
import org.junit.Assert;
import org.junit.Test;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.ArchivePaths;

import org.jboss.shrinkwrap.api.spec.JavaArchive;

import org.jboss.shrinkwrap.api.spec.ResourceAdapterArchive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.shrinkwrap.api.importer.ZipImporter;
import java.util.zip.ZipFile;
import org.apache.log4j.Logger;

@RunWith(Arquillian.class)
public class SampleArquillianTest {
    private static Logger _logger = Logger.getLogger(SampleArquillianTest.class);
    
   @Deployment
   public static JavaArchive createTestArchive() {
       File m1app = new File("/home/tcunning/.m2/repository/org/switchyard/quickstarts/m1app/1.0-SNAPSHOT/m1app-1.0-SNAPSHOT.jar");
       JavaArchive javaArchive = ShrinkWrap.create(JavaArchive.class, "test.jar");
       try {
	   javaArchive.as(ZipImporter.class).importZip(new ZipFile(m1app));
       } catch (Exception e) {
	   _logger.error(e);
       }

       _logger.debug("test.jar contains " + javaArchive.toString(true));

       return javaArchive;
   }

   @Test
   public void testM1app() {
  	System.out.println("SAMPLE TEST"); 
   }
}
