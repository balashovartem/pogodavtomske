package com.example;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Artemiy
 * Date: 12.03.12
 * Time: 10:43
 * To change this template use File | Settings | File Templates.
 */

public class HtmlHelper {
    TagNode rootNode;

    public HtmlHelper(URL htmlPage) throws IOException
    {
        HtmlCleaner cleaner = new HtmlCleaner();
        rootNode = cleaner.clean(htmlPage);
    }

    List<TagNode> getLinksByClass(String CSSClassname)
    {
        List<TagNode> linkList = new ArrayList<TagNode>();

        TagNode linkElements[] = rootNode.getElementsByName("a", true);
        for (int i = 0; linkElements != null && i < linkElements.length; i++)
        {
            String classType = linkElements[i].getAttributeByName("class");
            if (classType != null && classType.equals(CSSClassname))
            {
                linkList.add(linkElements[i]);
            }
        }

        return linkList;
    }
    List<TagNode> getTableByClass(String CSSClassname)
    {
        List<TagNode> linkList = new ArrayList<TagNode>();

        TagNode linkElements[] = rootNode.getElementsByName("table", true);
        for (int i = 0; linkElements != null && i < linkElements.length; i++)
        {
            String classType = linkElements[i].getAttributeByName("class");
            if (classType != null && classType.equals(CSSClassname))
            {
                linkList.add(linkElements[i]);
            }
        }

        return linkList;
    }
    List<TagNode> getTableByClass( TagNode localRoot , String CSSClassname)
    {
        List<TagNode> linkList = new ArrayList<TagNode>();

        TagNode linkElements[] = localRoot.getElementsByName("table", true);
        for (int i = 0; linkElements != null && i < linkElements.length; i++)
        {
            String classType = linkElements[i].getAttributeByName("class");
            if (classType != null && classType.equals(CSSClassname))
            {
                linkList.add(linkElements[i]);
            }
        }

        return linkList;
    }
    List<TagNode> getDivById(String id )
    {
        List<TagNode> linkList = new ArrayList<TagNode>();

        TagNode divElements[] = rootNode.getElementsByName("div", true);
        for (int i = 0; divElements != null && i < divElements.length; i++)
        {
            String divId = divElements[i].getAttributeByName("id");
            if (divId != null && divId.equals(id))
            {
                linkList.add(divElements[i]);
            }
        }

        return linkList;
    }
}