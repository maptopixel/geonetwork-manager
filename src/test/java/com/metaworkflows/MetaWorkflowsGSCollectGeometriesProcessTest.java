/*
 *  GeoNetwork-Manager - Simple Manager Library for GeoNetwork
 *
 *  Copyright (C) 2007,2011 GeoSolutions S.A.S.
 *  http://www.geo-solutions.it
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.metaworkflows;

import it.geosolutions.geonetwork.GNClient;
import it.geosolutions.geonetwork.exception.GNException;
import it.geosolutions.geonetwork.exception.GNLibException;
import it.geosolutions.geonetwork.exception.GNServerException;
import it.geosolutions.geonetwork.util.GNInsertConfiguration;
import it.geosolutions.geonetwork.util.GNPriv;
import it.geosolutions.geonetwork.util.GNPrivConfiguration;
import it.geosolutions.geonetwork.util.GNSearchRequest;
import it.geosolutions.geonetwork.util.GNSearchResponse;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumSet;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.junit.Test;

import static org.junit.Assert.*;

import org.junit.Ignore;

/**
 *
 * @author Julian Rosser
 */
public class MetaWorkflowsGSCollectGeometriesProcessTest extends GeonetworkTest {
    private final static Logger LOGGER = Logger.getLogger(MetaWorkflowsTest.class);

	String tempDir= "C:\\tmp\\"; // windows
    
    public MetaWorkflowsGSCollectGeometriesProcessTest() {
    }

    @Test
    public void testRegisterProcess() throws Exception {    	
    	
    	//Uncomment to clean out the cat
    	//removeAllMetadata();    	
    	
    	MetaWorkflow metaWorkflow = new MetaWorkflow();
        
    	System.out.println("Registering a process as 19139");
    	String processTitleElement = "gs:CollectGeometries";
    	String serviceUrlElement = "http://geoprocessing.westus.cloudapp.azure.com:8082/geoserver/ows";
    	String insertedProcessId = metaWorkflow.RegisterResult(processTitleElement,serviceUrlElement);
        
        //query for that newly inserted element
    	System.out.println("query for that newly inserted prcess element");
        Element processElement = metaWorkflow.GetMetadata(insertedProcessId);
    	        
        //get the location of the retrieved process element
        Element processLocation = metaWorkflow.getLocationElement(processElement);  
        System.out.println("GeoNetwork: getLocationElement " + processLocation.getText()); 
        
        //get the location of the retrieved process element
        Element processTitle = metaWorkflow.getTitleElement(processElement);  
        System.out.println("GeoNetwork: getTitleElement of process " + processTitle.getText()); 
        
   
        
        // service stuff. Bit deprecated. 
        //get the location of the retrieved element
        //Element serviceElement = metaWorkflow.GetMetadata("50420");  
        //System.out.println("GeoNetwork: serviceElement " + serviceElement.getText());
        //Element serviceElement2 = metaWorkflow.getMatchingProcessElement(serviceElement,"pillar2");  
       
        //System.out.println("GeoNetwork: serviceElement2 " + serviceElement2.getText());
        
    	

        
    }
 
   
    
    
}
