/*
 * =============================================================================
 * 
 *   Copyright (c) 2011-2014, The THYMELEAF team (http://www.thymeleaf.org)
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * 
 * =============================================================================
 */
package org.thymeleaf.aurora.engine;

import org.thymeleaf.aurora.util.TextUtil;

/**
 *
 * @author Daniel Fern&aacute;ndez
 * @since 3.0.0
 * 
 */
public class AttributeNames {



    public static AttributeName forXmlName(final char[] attributeNameBuffer, final int attributeNameOffset, final int attributeNameLen) {

        if (attributeNameBuffer == null) {
            throw new IllegalArgumentException("Attribute name buffer cannot be null or empty");
        }
        if (attributeNameOffset < 0 || attributeNameLen < 0) {
            throw new IllegalArgumentException("Atribute name offset and len must be equal or greater than zero");
        }


        char c;
        int i = attributeNameOffset;
        int n = attributeNameLen;
        while (n-- != 0) {

            c = attributeNameBuffer[i++];
            if (c != ':') {
                continue;
            }

            if (c == ':') {
                if (i == 1){
                    // ':' was the first char, no prefix there
                    return new AttributeName(new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));
                }

                return new XmlPrefixedAttributeName(
                        new String(attributeNameBuffer, attributeNameOffset, (i - (attributeNameOffset + 1))),
                        new String(attributeNameBuffer, i, attributeNameLen - i));
            }

        }

        return new AttributeName(new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));

    }



    public static AttributeName forHtmlName(final char[] attributeNameBuffer, final int attributeNameOffset, final int attributeNameLen) {

        if (attributeNameBuffer == null) {
            throw new IllegalArgumentException("Attribute name buffer cannot be null or empty");
        }
        if (attributeNameOffset < 0 || attributeNameLen < 0) {
            throw new IllegalArgumentException("Atribute name offset and len must be equal or greater than zero");
        }


        char c;
        int i = attributeNameOffset;
        int n = attributeNameLen;
        boolean inData = false;
        while (n-- != 0) {

            c = attributeNameBuffer[i++];
            if (c != ':' && c != '-') {
                continue;
            }

            if (!inData && c == ':') {
                if (i == 1){
                    // ':' was the first char, no prefix there
                    return new AttributeName(new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));
                }

                if (TextUtil.equals(false, "xml", 0, 3, attributeNameBuffer, attributeNameOffset, (i - (attributeNameOffset + 1)))) {
                    // 'xml' is not a valid dialect prefix in HTML mode
                    return new AttributeName(new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));
                }

                return new HtmlPrefixedAttributeName(
                        new String(attributeNameBuffer, attributeNameOffset, (i - (attributeNameOffset + 1))),
                        new String(attributeNameBuffer, i, attributeNameLen - i));
            }

            if (!inData && c == '-') {
                if (i == 5 && TextUtil.equals(false, "data", 0, 4, attributeNameBuffer, attributeNameOffset, 4)) {
                    inData = true;
                    continue;
                } else {
                    // this is just a normal, non-thymeleaf 'data-*' attribute
                    return new AttributeName(new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));
                }
            }

            if (inData && c == '-') {
                if (i == 6) {
                    // '-' was the first char after 'data-', no prefix there
                    return new AttributeName(new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));

                }
                return new HtmlPrefixedAttributeName(
                        new String(attributeNameBuffer, attributeNameOffset + 5, (i - (attributeNameOffset + 6))),
                        new String(attributeNameBuffer, i, attributeNameLen - i));
            }

        }

        return new AttributeName(new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));

    }



    public static AttributeName forXmlName(final String attributeName) {

        if (attributeName == null) {
            throw new IllegalArgumentException("Attribute name cannot be null or empty");
        }


        char c;
        int i = 0;
        int n = attributeName.length();
        while (n-- != 0) {

            c = attributeName.charAt(i++);
            if (c != ':') {
                continue;
            }

            if (c == ':') {
                if (i == 1){
                    // ':' was the first char, no prefix there
                    return new AttributeName(attributeName);
                }

                return new XmlPrefixedAttributeName(
                        attributeName.substring(0, i - 1),
                        attributeName.substring(i, attributeName.length()));
            }

        }

        return new AttributeName(attributeName);

    }



    public static AttributeName forHtmlName(final String attributeName) {

        if (attributeName == null) {
            throw new IllegalArgumentException("Attribute name cannot be null or empty");
        }

        char c;
        int i = 0;
        int n = attributeName.length();
        boolean inData = false;
        while (n-- != 0) {

            c = attributeName.charAt(i++);
            if (c != ':' && c != '-') {
                continue;
            }

            if (!inData && c == ':') {
                if (i == 1){
                    // ':' was the first char, no prefix there
                    return new AttributeName(attributeName);
                }

                if (TextUtil.equals(false, "xml", 0, 3, attributeName, 0, 3)) {
                    // 'xml' is not a valid dialect prefix in HTML mode
                    return new AttributeName(attributeName);
                }

                return new HtmlPrefixedAttributeName(
                        attributeName.substring(0, i - 1),
                        attributeName.substring(i,attributeName.length()));
            }

            if (!inData && c == '-') {
                if (i == 5 && TextUtil.equals(false, "data", 0, 4, attributeName, 0, 4)) {
                    inData = true;
                    continue;
                } else {
                    // this is just a normal, non-thymeleaf 'data-*' attribute
                    return new AttributeName(attributeName);
                }
            }

            if (inData && c == '-') {
                if (i == 6) {
                    // '-' was the first char after 'data-', no prefix there
                    return new AttributeName(attributeName);

                }
                return new HtmlPrefixedAttributeName(
                        attributeName.substring(5, i - 1),
                        attributeName.substring(i, attributeName.length()));
            }

        }

        return new AttributeName(attributeName);

    }



    public static AttributeName forHtmlName(final String dialectPrefix, final char[] attributeNameBuffer, final int attributeNameOffset, final int attributeNameLen) {
        if (attributeNameBuffer == null) {
            throw new IllegalArgumentException("Attribute name buffer cannot be null or empty");
        }
        if (attributeNameOffset < 0 || attributeNameLen < 0) {
            throw new IllegalArgumentException("Attribute name offset and len must be equal or greater than zero");
        }
        if (dialectPrefix == null) {
            return new AttributeName(new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));
        }
        return new HtmlPrefixedAttributeName(dialectPrefix, new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));
    }



    public static AttributeName forXmlName(final String dialectPrefix, final char[] attributeNameBuffer, final int attributeNameOffset, final int attributeNameLen) {
        if (attributeNameBuffer == null) {
            throw new IllegalArgumentException("Attribute name buffer cannot be null or empty");
        }
        if (attributeNameOffset < 0 || attributeNameLen < 0) {
            throw new IllegalArgumentException("Attribute name offset and len must be equal or greater than zero");
        }
        if (dialectPrefix == null) {
            return new AttributeName(new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));
        }
        return new XmlPrefixedAttributeName(dialectPrefix, new String(attributeNameBuffer, attributeNameOffset, attributeNameLen));
    }



    public static AttributeName forHtmlName(final String dialectPrefix, final String attributeName) {
        if (attributeName == null) {
            throw new IllegalArgumentException("Attribute name cannot be null or empty");
        }
        if (dialectPrefix == null) {
            return new AttributeName(attributeName);
        }
        return new HtmlPrefixedAttributeName(dialectPrefix, attributeName);
    }



    public static AttributeName forXmlName(final String dialectPrefix, final String attributeName) {
        if (attributeName == null) {
            throw new IllegalArgumentException("Attribute name cannot be null or empty");
        }
        if (dialectPrefix == null) {
            return new AttributeName(attributeName);
        }
        return new XmlPrefixedAttributeName(dialectPrefix, attributeName);
    }




    

    private AttributeNames() {
        super();
    }
    
    

}
