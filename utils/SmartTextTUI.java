package utils;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

final public class SmartTextTUI extends TextTUI {
  private String xml;

  /**
 * Add XML tags to convert it to TextTUI.
 * <p>Supported elements:</p>
 * <ul>
 * <li><b>Supported tags:</b>
 * <ul>
 * <li>{@code <span>} - inline container used to mark up a part of a text.</li>
 * <li>{@code <p>} - automatically adds a single blank line after each element.</li>
 * <li>{@code <a>} - defines a hyperlink using the {@code href} attribute.</li>
 * </ul>
 * </li>
 * <li><b>Supported attributes across all tags:</b>
 * <ul>
 * <li>{@code color} (supports hexadecimal color)</li>
 * <li>{@code bg-color} (supports hexadecimal color)</li>
 * <li>{@code style} (supports bold, italic, underline, strikethrough, hidden, and invert)</li>
 * </ul>
 * </li>
 * </ul>
 * <p><b>Note:</b> Tags must be wrapped in a single root element.</p>
 * @param xml the XML string to convert
 */
  public SmartTextTUI(String xml) {
    this.xml = xml;
  }

  @Override
  protected String text(boolean reset) {
    return tagsConverter();
  }

  // Convert Tags -> TextTUI
  private String tagsConverter() {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = null;
    try {
      builder = factory.newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    Document doc = null;
    try {
      doc = builder.parse(new ByteArrayInputStream(xml.getBytes()));
    } catch (SAXException | IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    parents.add(ANSI.Reset.toString());
    searching(doc.getDocumentElement(), 0);
    return sb.toString();
  }

  private Stack<String> parents = new Stack<>();
  private StringBuilder sb = new StringBuilder();
  private void searching(Element element, int currentDepth) {

    NamedNodeMap attributes = element.getAttributes();
    List<Integer> style = new ArrayList<>();
    String color = "";
    String bg = "";

    for (int i = 0; i < attributes.getLength(); i++) {
      Node attr = attributes.item(i);
      switch (attr.getNodeName().toLowerCase()) {
        case "color":
          color = attr.getNodeValue();
          break;
        case "bg-color":
          bg = attr.getNodeValue();
          break;
        case "style":
          style = getStyleIndexes(attr.getNodeValue());
          break;
        default:
          break;
      }
    }

    String currentStyle = ANSIformat.format("", color, style, bg, false);
    parents.push(currentStyle);
    sb.append(currentStyle);

    String tagName = element.getNodeName().toLowerCase();
    if (tagName.equals("a")) {
      String href = element.getAttribute("href");
      sb.append("\u001b]8;;" + href + "\u001b\\");
    }

    NodeList children = element.getChildNodes();
    for (int i = 0; i < children.getLength(); i++) {
        Node child = children.item(i);
        
        if (child.getNodeType() == Node.ELEMENT_NODE) {
            searching((Element) child, currentDepth + 1);
        } else if (child.getNodeType() == Node.TEXT_NODE) {
            // text
            sb.append(cleanText(child.getNodeValue()));
        }
    }


    if (tagName.equals("a")) {
      sb.append("\u001b]8;;\u001b\\");
    }


    parents.pop();
    sb.append(ANSI.Reset);
    sb.append(parents.peek());

    if (tagName.equals("p")) {
      sb.append("\n");
    }
  }

  private List<Integer> getStyleIndexes(String style){
    List<Integer> allStyles = new ArrayList<>();
    style = style.replaceAll("[^A-Za-z ]", " ").replaceAll("[ ]+", " ").toLowerCase();

    String[] lists = style.split(" ");
    for (String list : lists) {
      for(Style s : Style.values()) {
        if(list.equals(s.toString().toLowerCase())) {
          allStyles.add(s.getStyle());
          break;
        }
      }
    }

    return allStyles;
  }

  private String cleanText(String rawText) {
    if (rawText.trim().isEmpty()) return "";

    return rawText.replace("\r", "").replace("\n", "").replaceAll(" +", " ");
  }

}
