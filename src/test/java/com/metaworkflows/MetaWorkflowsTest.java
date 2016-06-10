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
public class MetaWorkflowsTest extends GeonetworkTest {
    private final static Logger LOGGER = Logger.getLogger(MetaWorkflowsTest.class);

	String tempDir= "C:\\tmp\\"; // windows
    
    public MetaWorkflowsTest() {
    }

    @Test
    public void testRegisterResult() throws Exception {
    	  	 
    	removeAllMetadata();    	
    	MetaWorkflow metaWorkflow = new MetaWorkflow();

    	    	 
    	System.out.println("Register Result Test");
    	String newTitleElement = "Shape_ee802b63-070d-43c0-9558-983bca748d31443813113178467117";
    	String newUrlElement = "http://localhost:8000/geoserver/N52/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=N52:Shape_ee802b63-070d-43c0-9558-983bca748d31443813113178467117";
    	long insertedId = metaWorkflow.RegisterResult(newTitleElement,newUrlElement);
            	
        //query for that newly inserted element 
        Element retrievedElement = metaWorkflow.GetMetadata(insertedId);

        
        //get the location of the retrieved element
        Element Urllocation = metaWorkflow.getLocationElement(retrievedElement);  
        System.out.println("GeoNetwork: getLocationElement " + Urllocation.getText()); 
        //chstr.setText("updated title from JBPM");
               

    }
 
    
    
}
