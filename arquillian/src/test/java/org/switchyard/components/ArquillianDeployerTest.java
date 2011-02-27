package org.switchyard.components;

import java.io.File;
import java.io.FileInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.runner.RunWith;
import org.junit.Assert;
import org.junit.Test;

import org.jboss.arquillian.api.ArquillianResource;
import org.jboss.arquillian.api.Deployer;
import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.api.DeploymentTarget;
import org.jboss.arquillian.api.Expected;
import org.jboss.arquillian.api.Protocol;
import org.jboss.arquillian.api.Target;
import org.jboss.arquillian.api.DeploymentTarget;
import org.jboss.arquillian.spi.TestClass;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.ArchivePaths;

import org.jboss.shrinkwrap.api.spec.JavaArchive;

import org.jboss.shrinkwrap.api.spec.ResourceAdapterArchive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.dependencies.Dependencies;
import org.jboss.shrinkwrap.dependencies.impl.filter.StrictFilter;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.apache.log4j.Logger;

@RunWith(Arquillian.class)
public class ArquillianDeployerTest {

    @ArquillianResource
    private Deployer deployer;
    
    private static Logger _logger = Logger.getLogger(ArquillianDeployerTest.class);
    
    private static File getArtifact(String artifact) throws Exception {
	File files[] = Dependencies.artifact(artifact).resolveAsFiles();
	//new StrictFilter());
	if (files.length == 0) {
	    throw new Exception("Found no files for artifact " + artifact);
	} else if (files.length > 1) {
	    throw new Exception("Found too many files for artifact " + artifact);
	}
	
	_logger.debug("Found file " + files[0].toString() + " for artifact " + artifact);
	return files[0];
    }
    
    @Deployment(name="m1app", startup=true, testable=true)
    public static JavaArchive createTestArchive() {
	JavaArchive javaArchive = ShrinkWrap.create(JavaArchive.class, "m1app.jar");
	try {		
	    javaArchive.as(ZipImporter.class).importFrom(getArtifact("org.switchyard.quickstarts:m1app:1.0-SNAPSHOT"));
	} catch (Exception e) {
	    _logger.error(e);
	}
	System.out.println(javaArchive.toString(true));
	return javaArchive;
    }

    /*
    @Deployment(name="components-bean", testable=false, startup=true)
    public static JavaArchive createBeanComponentArchive() {
	JavaArchive javaArchive = ShrinkWrap.create(JavaArchive.class, "components-bean");
	try {
	javaArchive.as(ZipImporter.class).importFrom(getArtifact("org.switchyard.components:switchyard-components-bean:1.0-SNAPSHOT"));
	} catch (Exception e) {
	    _logger.error(e);
	}
	System.out.println(javaArchive.toString(true));
	return javaArchive;
    }

    @Deployment(name="components-soap", testable=false, startup=true)
    public static JavaArchive createSOAPComponentArchive() {
	JavaArchive javaArchive = ShrinkWrap.create(JavaArchive.class, "components-soap.jar");;
	try {
	    javaArchive.as(ZipImporter.class).importFrom(getArtifact("org.switchyard.components:switchyard-components-soap:1.0-SNAPSHOT"));

	} catch (Exception e) {
	    _logger.error(e);
	}
       
	System.out.println(javaArchive.toString(true));
	return javaArchive;
    }
    */
    
   @Deployment(name="test", startup=false, testable=false)
   public static JavaArchive createDummyTestArchive() {
       JavaArchive javaArchive = ShrinkWrap.create(JavaArchive.class, "test.jar")
       		.addAsResource(EmptyAsset.INSTANCE,
       	            ArchivePaths.create("beans.xml"));
       return javaArchive;
   }
    
   @Deployment(name="switchyard-deployer", startup=true, testable=false)
   public static JavaArchive createDeployerArchive() {
       JavaArchive javaArchive = null;
       try {
	   File deployerJARs[] = Dependencies.artifacts("org.switchyard:switchyard-config:1.0-SNAPSHOT", 
		   "org.switchyard:switchyard-api:1.0-SNAPSHOT", "org.switchyard:switchyard-transform:1.0-SNAPSHOT",
		   "org.switchyard:switchyard-runtime:1.0-SNAPSHOT", "org.switchyard:switchyard-deploy:1.0-SNAPSHOT", 
		   "org.switchyard:switchyard-deploy-jboss-as6:1.0-SNAPSHOT").resolveAsFiles();
	   //new StrictFilter());
	   File components[] = Dependencies.artifacts("org.switchyard.components:switchyard-component-bean:1.0-SNAPSHOT",
		   "org.switchyard.components:switchyard-component-soap:1.0-SNAPSHOT").resolveAsFiles();
	   //new StrictFilter());
	   javaArchive = ShrinkWrap.create(JavaArchive.class, "deployer.jar");
	   
	   for (File deployerJAR : deployerJARs) {
	       javaArchive.addAsResource(deployerJAR, "/switchyard.deployer/" + deployerJAR.getName());
	   }
	
	   for (File component : components) {
	       javaArchive.addAsResource(component, component.getName());
	   }
	   
	   javaArchive.addAsResource("switchyard-deployers-jboss-beans.xml", "/switchyard.deployer/META-INF/switchyard-deployers-jboss-beans.xml");
	   
	   /*
	   javaArchive = ShrinkWrap.create(JavaArchive.class, "deployer.jar")
           	.addAsResource(getArtifact("org.switchyard:switchyard-config:1.0-SNAPSHOT"), "/switchyard.deployer/switchyard-config-1.0.SNAPSHOT.jar")
           	.addAsResource(getArtifact("org.switchyard:switchyard-api:1.0-SNAPSHOT"), "/switchyard.deployer/switchyard-api-1.0.SNAPSHOT.jar")
           	.addAsResource(getArtifact("org.switchyard:switchyard-transform:1.0-SNAPSHOT"), "/switchyard.deployer/switchyard-transform-1.0-SNAPSHOT.jar")
           	.addAsResource(getArtifact("org.switchyard:switchyard-runtime:1.0-SNAPSHOT"), "/switchyard.deployer/switchyard-runtime-1.0-SNAPSHOT.jar")
           	.addAsResource(getArtifact("org.switchyard:switchyard-deploy:1.0-SNAPSHOT"), "/switchyard.deployer/switchyard-deployer-1.0-SNAPSHOT.jar")
           	.addAsResource(getArtifact("org.switchyard.components:switchyard-component-bean:1.0-SNAPSHOT"), "/switchyard-components-bean.jar")
          	.addAsResource(getArtifact("org.switchyard.components:switchyard-component-soap:1.0-SNAPSHOT"), "/switchyard-components-soap.jar")
          	.addAsResource(getArtifact("org.switchyard:switchyard-deploy-jboss-as6:1.0-SNAPSHOT"), "/switchyard.deployer/switchyard-deploy-jboss-as6-1.0.SNAPSHOT.jar")
          	.addAsResource("switchyard-deployers-jboss-beans.xml", "/switchyard.deployer/META-INF/switchyard-deployers-jboss-beans.xml");
       	*/
       } catch (Exception e) {
	   _logger.error(e);
       }
       System.out.println(javaArchive.toString(true));
       
       return javaArchive;
   }
				
   @Test
   @DeploymentTarget("m1app")
   public void emptyTest() {
       System.out.println("Start test");
       try {Thread.sleep(500000);} catch (Exception e) {};
       System.out.println("End test");
   }
}

