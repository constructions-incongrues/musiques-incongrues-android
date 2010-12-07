package com.headbangers.mi.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.headbangers.mi.model.RSSMessage;

public class RSSAccessServiceImpl extends BaseFeedParser {

    @Override
    public List<RSSMessage> parse(String url) {
        super.setFeedUrl(url);

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        List<RSSMessage> messages = new ArrayList<RSSMessage>();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document dom = builder.parse(this.getInputStream());
            Element root = dom.getDocumentElement();
            NodeList items = root.getElementsByTagName(ITEM);

            for (int i = 0; i < items.getLength(); i++) {

                RSSMessage message = new RSSMessage();
                Node item = items.item(i);
                NodeList properties = item.getChildNodes();

                for (int j = 0; j < properties.getLength(); j++) {
                    Node property = properties.item(j);
                    String name = property.getNodeName();

                    if (name.equalsIgnoreCase(TITLE)) {
                        NodeList titleChild = property.getChildNodes();
                        StringBuffer buffer = new StringBuffer();
                        for (int k = 0; k < titleChild.getLength(); k++) {
                            String nodeValue = titleChild.item(k)
                                    .getNodeValue();
                            buffer.append(nodeValue != null ? nodeValue
                                    : titleChild.item(k).getNodeName() + ";");
                        }
                        message.setTitle(buffer.toString());

                    } else if (name.equalsIgnoreCase(AUTHOR)) {

                        message.setAuthor(property.getFirstChild()
                                .getNodeValue());

                    } else if (name.equalsIgnoreCase(LINK)) {

                        message.setLink(property.getFirstChild().getNodeValue());

                    } else if (name.equalsIgnoreCase(ENCLOSURE)) {

                        message.setEnclosureLink(property.getAttributes()
                                .getNamedItem("url").getNodeValue());

                    } else if (name.equalsIgnoreCase(DESCRIPTION)) {

                        StringBuilder text = new StringBuilder();
                        NodeList chars = property.getChildNodes();
                        for (int k = 0; k < chars.getLength(); k++) {
                            text.append(chars.item(k).getNodeValue());
                        }
                        message.setDescription(text.toString());

                    } else if (name.equalsIgnoreCase(CONTENT)
                            || name.equalsIgnoreCase(CONTENT_ENCODED)) {

                        StringBuilder text = new StringBuilder();
                        NodeList chars = property.getChildNodes();
                        for (int k = 0; k < chars.getLength(); k++) {
                            text.append(chars.item(k).getNodeValue());
                        }
                        message.setContent(text.toString());

                    }

                    else if (name.equalsIgnoreCase(PUB_DATE)) {

                        message.setDate(property.getFirstChild().getNodeValue());
                    }
                }
                messages.add(message);
            }
        } catch (Exception e) {
            return null;
        }
        return messages;
    }

}
