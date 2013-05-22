/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     David McCann - May 10, 2013 - Initial implementation
 ******************************************************************************/
package dbws;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.eclipse.persistence.platform.xml.XMLComparer;
import org.eclipse.persistence.platform.xml.XMLParser;
import org.eclipse.persistence.platform.xml.XMLPlatform;
import org.eclipse.persistence.platform.xml.XMLPlatformFactory;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests building a web service from table info.
 *
 */
public class EmpServiceTestSuite {
    static XMLComparer comparer = new XMLComparer();
    static XMLPlatform xmlPlatform = XMLPlatformFactory.getInstance().getXMLPlatform();
    static XMLParser xmlParser = xmlPlatform.newXMLParser();
    
    static final String SOAP_FINDALL_REQUEST =
        "<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
          "<env:Body>" +
            "<findAll_empType xmlns=\"urn:empService\" xmlns:urn=\"urn:emp\"/>" +
          "</env:Body>" +
        "</env:Envelope>";
    static final String SOAP_FINDALL_RESPONSE =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>" +
        "<srvc:findAll_empTypeResponse xmlns=\"urn:emp\" xmlns:srvc=\"urn:empService\">" +
            "<srvc:result>" +
                "<empType>" +
                    "<empno>7369</empno>" +
                    "<ename>SMITH</ename>" +
                    "<job>CLERK</job>" +
                    "<mgr>7902</mgr>" +
                    "<hiredate>1980-12-17</hiredate>" +
                    "<sal>800.00</sal>" +
                    "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
                    "<deptno>20</deptno>" +
                "</empType>" +    
                "<empType>" +
                    "<empno>7499</empno>" +
                    "<ename>ALLEN</ename>" +
                    "<job>SALESMAN</job>" +
                    "<mgr>7698</mgr>" +
                    "<hiredate>1981-02-20</hiredate>" +
                    "<sal>1600.00</sal>" +
                    "<comm>300.00</comm>" +
                    "<deptno>30</deptno>" +
                "</empType>" +
                "<empType>" +
                    "<empno>7521</empno>" +
                    "<ename>WARD</ename>" +
                    "<job>SALESMAN</job>" +
                    "<mgr>7698</mgr>" +
                    "<hiredate>1981-02-22</hiredate>" +
                    "<sal>1250.00</sal>" +
                    "<comm>500.00</comm>" +
                    "<deptno>30</deptno>" +
                "</empType>" +
                "<empType>" +
                    "<empno>7566</empno>" +
                    "<ename>JONES</ename>" +
                    "<job>MANAGER</job>" +
                    "<mgr>7839</mgr>" +
                    "<hiredate>1981-04-02</hiredate>" +
                    "<sal>2975.00</sal>" +
                    "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
                    "<deptno>20</deptno>" +
                "</empType>" +
                "<empType>" +
                    "<empno>7654</empno>" +
                    "<ename>MARTIN</ename>" +
                    "<job>SALESMAN</job>" +
                    "<mgr>7698</mgr>" +
                    "<hiredate>1981-09-28</hiredate>" +
                    "<sal>1250.00</sal>" +
                    "<comm>1400.00</comm>" +
                    "<deptno>30</deptno>" +
                "</empType>" +
                "<empType>" +
                    "<empno>7698</empno>" +
                    "<ename>BLAKE</ename>" +
                    "<job>MANAGER</job>" +
                    "<mgr>7839</mgr>" +
                    "<hiredate>1981-05-01</hiredate>" +
                    "<sal>2850.00</sal>" +
                    "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
                    "<deptno>30</deptno>" +
                "</empType>" +
                "<empType>" +
                    "<empno>7782</empno>" +
                     "<ename>CLARK</ename>" +
                     "<job>MANAGER</job>" +
                     "<mgr>7839</mgr>" +
                     "<hiredate>1981-06-09</hiredate>" +
                     "<sal>2450.00</sal>" +
                     "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
                     "<deptno>10</deptno>" +
                "</empType>" +
                "<empType>" +
                    "<empno>7788</empno>" +
                     "<ename>SCOTT</ename>" +
                     "<job>ANALYST</job>" +
                     "<mgr>7566</mgr>" +
                     "<hiredate>1981-06-09</hiredate>" +
                     "<sal>3000.00</sal>" +
                     "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
                     "<deptno>20</deptno>" +
                "</empType>" +
                "<empType>" +
                    "<empno>7839</empno>" +
                     "<ename>KING</ename>" +
                     "<job>PRESIDENT</job>" +
                     "<mgr xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
                     "<hiredate>1981-11-17</hiredate>" +
                     "<sal>5000.00</sal>" +
                     "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
                     "<deptno>10</deptno>" +
                "</empType>" +
                "<empType>" +
                    "<empno>7844</empno>" +
                     "<ename>TURNER</ename>" +
                     "<job>SALESMAN</job>" +
                     "<mgr>7698</mgr>" +
                     "<hiredate>1981-09-08</hiredate>" +
                     "<sal>1500.00</sal>" +
                     "<comm>0.00</comm>" +
                     "<deptno>30</deptno>" +
                "</empType>" +
                "<empType>" +
                    "<empno>7876</empno>" +
                     "<ename>ADAMS</ename>" +
                     "<job>CLERK</job>" +
                     "<mgr>7788</mgr>" +
                     "<hiredate>1987-05-23</hiredate>" +
                     "<sal>1100.00</sal>" +
                     "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
                     "<deptno>20</deptno>" +
                "</empType>" +
                "<empType>" +
                    "<empno>7900</empno>" +
                     "<ename>JAMES</ename>" +
                     "<job>CLERK</job>" +
                     "<mgr>7698</mgr>" +
                     "<hiredate>1981-12-03</hiredate>" +
                     "<sal>950.00</sal>" +
                     "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
                     "<deptno>30</deptno>" +
                "</empType>" +
                "<empType>" +
                    "<empno>7902</empno>" +
                     "<ename>FORD</ename>" +
                     "<job>ANALYST</job>" +
                     "<mgr>7566</mgr>" +
                     "<hiredate>1981-12-03</hiredate>" +
                     "<sal>3000.00</sal>" +
                     "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
                     "<deptno>20</deptno>" +
                "</empType>" +
                "<empType>" +
                    "<empno>7934</empno>" +
                     "<ename>MILLER</ename>" +
                     "<job>CLERK</job>" +
                     "<mgr>7782</mgr>" +
                     "<hiredate>1982-01-23</hiredate>" +
                     "<sal>1300.00</sal>" +
                     "<comm xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:nil=\"true\"/>" +
                     "<deptno>10</deptno>" +
                "</empType>" +
             "</srvc:result>" +
        "</srvc:findAll_empTypeResponse>";

    @Test
    public void testService() {
    	try {
	        QName qname = new QName("urn:empService", "empServicePort");
	        Service service = Service.create(new QName("urn:emp", "empService"));
	        service.addPort(qname, SOAPBinding.SOAP11HTTP_BINDING, "http://" + "localhost" + ":" + "8080" + "/dbws.simple/emp");
	        //service.addPort(qname, SOAPBinding.SOAP11HTTP_BINDING, "http://" + host + ":" + port + "/dbws.simple/emp");
	        Dispatch<SOAPMessage> sourceDispatch = service.createDispatch(qname, SOAPMessage.class, Service.Mode.MESSAGE);
	                    
            SOAPMessage request = createSOAPMessage(SOAP_FINDALL_REQUEST);
	        SOAPMessage response = sourceDispatch.invoke(request);
	        assertNotNull("findAll_empType failed:  response is null.", response);
            SOAPBody responseBody = response.getSOAPPart().getEnvelope().getBody();
            Document resultDoc = responseBody.extractContentAsDocument();
            Document controlDoc = xmlParser.parse(new StringReader(SOAP_FINDALL_RESPONSE));
            
            NodeList elts = resultDoc.getDocumentElement().getElementsByTagNameNS("urn:empService", "result");
            assertTrue("The wrong number of elements were returned.", ((elts != null && elts.getLength() > 0) && elts.getLength() == 1));
            Node testNode = elts.item(0);
            assertTrue("Didn't find [<srvc:result>] element", testNode.getLocalName().equalsIgnoreCase("result"));
            elts = controlDoc.getDocumentElement().getElementsByTagNameNS("urn:empService", "result");
            Node ctrlNode = elts.item(0);
            assertTrue("findAll_empType document comparison failed.  Expected:\n" + documentToString(ctrlNode) + "\nbut was:\n" + documentToString(testNode), comparer.isNodeEqual(ctrlNode, testNode));
        } catch (Exception x) {
    		fail("Service test failed: " + x.getMessage());
    	}
	}
    
    
    /**
     * Create a SOAP message based on a given String.
     */
    public static SOAPMessage createSOAPMessage(String message) {
        SOAPMessage soapMessage;
        try {
            MessageFactory factory = MessageFactory.newInstance();
            soapMessage = factory.createMessage();
            soapMessage.getSOAPPart().setContent((Source)new StreamSource(new StringReader(message)));
            soapMessage.saveChanges();
        } catch (Exception e) {
            e.printStackTrace();
            soapMessage = null;
        }
        return soapMessage;
    }
    
    /**
     * Returns the given org.w3c.dom.Document as a String.
     *
     */
    public static String documentToString(Node doc) {
        DOMSource domSource = new DOMSource(doc);
        StringWriter stringWriter = new StringWriter();
        StreamResult result = new StreamResult(stringWriter);
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty("indent", "yes");
            transformer.transform(domSource, result);
            return stringWriter.toString();
        } catch (Exception e) {
            // e.printStackTrace();
            return "<empty/>";
        }
    }
}