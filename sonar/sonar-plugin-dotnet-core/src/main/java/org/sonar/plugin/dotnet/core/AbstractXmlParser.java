/**
 * Maven and Sonar plugin for .Net
 * Copyright (C) 2010 Jose Chillan and Alexandre Victoor
 * mailto: jose.chillan@codehaus.org or alexandre.victoor@codehaus.org
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */

/*
 * Created on Jun 4, 2009
 */
package org.sonar.plugin.dotnet.core;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * A base class for XML parsers that provides simplified methods.
 * 
 * @author Jose CHILLAN Jun 4, 2009
 */
public class AbstractXmlParser
{
  protected XPathFactory factory;
  protected XPath        xpath;

  /**
   * Constructs a @link{AbstractXmlParser}.
   */
  public AbstractXmlParser()
  {
    factory = XPathFactory.newInstance();
    xpath = factory.newXPath();
  }

  /**
   * Constructs a @link{AbstractXmlParser}.
   */
  public AbstractXmlParser(String prefix, String namespace)
  {
    factory = XPathFactory.newInstance();
    xpath = factory.newXPath();
    xpath.setNamespaceContext(new DefaultNamespaceContext(prefix, namespace));
  }

  /**
   * Gets the direct attribute of an element as an integer.
   * 
   * @param element the element whose attribute will be read
   * @param attributeName the name of the attribute
   * @return the attribute's value, or 0 if not defined
   */
  public int getIntAttribute(Element element, String attributeName)
  {
    String value = getAttribute(element, attributeName);
    int result = 0;
    try
    {
      if (value != null)
      {
        // We need a double here since source monitor has sometime a strange behaviour
        result = (int) Double.parseDouble(value);
      }
    }
    catch (NumberFormatException nfe)
    {
      // Nothing
    }
    return result;
  }

  /**
   * Gets the unique (or first) sub-element having the given name.
   * 
   * @param element
   * @param name
   * @return the sub-element or <code>null</code> if their if none
   */
  public Element getUniqueSubElement(Element element, String name)
  {
    NodeList subElements = element.getElementsByTagName(name);
    if (subElements.getLength() == 0)
    {
      return null;
    }
    Element result = (Element) subElements.item(0);
    return result;
  }

  /**
   * Gets an attribute as a double.
   * 
   * @param node
   * @param attributeName
   * @return
   */
  public double getDoubleAttribute(Element element, String attributeName)
  {
    String value = getAttribute(element, attributeName);
    double result = 0;
    try
    {
      if (value != null)
      {
        result = Double.parseDouble(value);
      }
    }
    catch (NumberFormatException nfe)
    {
      // Nothing
    }
    return result;
  }

  /**
   * Reads an attribute defined by a XPath.
   * 
   * @param element the element from this the value will be extracted
   * @param path
   * @return the attribute value, or <code>null</code> if not defined
   */
  public String getAttribute(Element element, String attributeName)
  {
    String result = element.getAttribute(attributeName);
    return result;
  }

  /**
   * Evaluates an attribute defined by a XPath.
   * 
   * @param element the element from this the value will be extracted
   * @param xpath the attribute's xpath
   * @return the attribute value, or <code>null</code> if not defined
   */
  public String evaluateAttribute(Element element, String xpath)
  {
    try
    {
      XPathExpression expression = this.xpath.compile(xpath);
      String result = expression.evaluate(element);
      return result;
    }
    catch (XPathExpressionException e)
    {
      // Nothing
    }
    return null;
  }

  /**
   * Extracts the elements matching a XPath in a file.
   * 
   * @param file the URL of the file to analyse
   * @param path the xpath of the elements to extract
   * @return a non <code>null</code> list of elements
   */
  protected List<Element> extractElements(URL file, String path)
  {
    NodeList nodes;
    try
    {
      InputSource source = new InputSource(file.openStream());
      // First, we collect all the files
      XPathExpression expression = xpath.compile(path);
      nodes = (NodeList) expression.evaluate(source, XPathConstants.NODESET);
    }
    catch (Exception e)
    {
      return Collections.emptyList();
    }

    return convertToList(nodes);
  }

  /**
   * Converts a {@link NodeList} into an array of {@link Element}s.
   * 
   * @param nodes the node list to convert
   * @return
   */
  protected List<Element> convertToList(NodeList nodes)
  {
    int countNode = nodes.getLength();
    List<Element> elements = new ArrayList<Element>();
    for (int idxNode = 0; idxNode < countNode; idxNode++)
    {
      elements.add((Element) nodes.item(idxNode));
    }
    return elements;
  }

  /**
   * Converts a double representation of a time into milliseconds.
   * 
   * @param attribute the attribute containing the time
   * @return the number of milliseconds
   */
  protected int toMillisec(String attribute)
  {
    try
    {
      double timeInSec = Double.parseDouble(attribute);
      return (int) Math.round(1000 * timeInSec);
    }
    catch (Exception e)
    {
      return 0;
    }
  }

  /**
   * Safely converts an attribute into an int with no exception.
   * 
   * @param attribute
   * @return the integer value of the attribute, or 0 if the format is invalid.
   */
  protected int safelyToInteger(String attribute)
  {
    try
    {
      return Integer.parseInt(attribute);
    }
    catch (NumberFormatException e)
    {
      return 0;
    }
  }

  /**
   * Gets the content of a sub node identified by its tag.
   * 
   * @param element the parent element
   * @param tagName the name of the tag to look for
   * @return the tag content, or <code>null</code> if its was not found
   */
  protected String getNodeContent(Element element, String tagName)
  {
    String result = null;
    NodeList messageNodes = element.getElementsByTagName(tagName);
    if (messageNodes.getLength() > 0)
    {
      result = messageNodes.item(0).getTextContent();
    }
    return result;
  }
}