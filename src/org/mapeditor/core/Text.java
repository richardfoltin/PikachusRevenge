/*-
 * #%L
 * libtiled
 * %%
 * Copyright (C) 2004 - 2019 Thorbj?rn Lindeijer <thorbjorn@lindeijer.nl>
 * %%
 * Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice,
   this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.11 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.03.18 at 01:06:44 PM CET 
//


package org.mapeditor.core;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * Used to mark an object as a text object. Contains the actual<br>
 * text as character data.
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Text", propOrder = {
    "value"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2019-03-18T01:06:44+01:00", comments = "JAXB RI v2.2.11")
public class Text {

    /**
     * 
     */
    @XmlValue
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-03-18T01:06:44+01:00", comments = "JAXB RI v2.2.11")
    protected String value;
    /**
     * The font family used (default: "sand-serif")
     * 
     */
    @XmlAttribute(name = "fontfamily")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-03-18T01:06:44+01:00", comments = "JAXB RI v2.2.11")
    protected String fontfamily;
    /**
     * The size of the font in pixels (not using points,<br>
     * because other sizes in the TMX format are also using<br>
     * pixels) (default: 16)
     * 
     */
    @XmlAttribute(name = "pixelsize")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-03-18T01:06:44+01:00", comments = "JAXB RI v2.2.11")
    protected Integer pixelsize;
    /**
     * Whether word wrapping is enabled (1) or disabled<br>
     * (0). Defaults to 0.
     * 
     */
    @XmlAttribute(name = "wrap")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-03-18T01:06:44+01:00", comments = "JAXB RI v2.2.11")
    protected Boolean wrap;
    /**
     * Color of the text in `#AARRGGBB` or `#RRGGBB` format<br>
     * (default: #000000)
     * 
     */
    @XmlAttribute(name = "color")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-03-18T01:06:44+01:00", comments = "JAXB RI v2.2.11")
    protected String color;
    /**
     * Whether the font is bold (1) or not (0). Defaults to<br>
     *  0.
     * 
     */
    @XmlAttribute(name = "bold")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-03-18T01:06:44+01:00", comments = "JAXB RI v2.2.11")
    protected Boolean bold;
    /**
     * Whether the font is italic (1) or not (0). Defaults<br>
     * to 0.
     * 
     */
    @XmlAttribute(name = "italic")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-03-18T01:06:44+01:00", comments = "JAXB RI v2.2.11")
    protected Boolean italic;
    /**
     * Whether a line should be drawn below the text (1) or<br>
     * not (0). Defaults to 0.
     * 
     */
    @XmlAttribute(name = "underline")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-03-18T01:06:44+01:00", comments = "JAXB RI v2.2.11")
    protected Boolean underline;
    /**
     * Whether a line should be drawn through the text (1)<br>
     * or not (0). Defaults to 0.
     * 
     */
    @XmlAttribute(name = "strikeout")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-03-18T01:06:44+01:00", comments = "JAXB RI v2.2.11")
    protected Boolean strikeout;
    /**
     * Whether kerning should be used while rendering the<br>
     * text (1) or not (0). Default to 1.
     * 
     */
    @XmlAttribute(name = "kerning")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-03-18T01:06:44+01:00", comments = "JAXB RI v2.2.11")
    protected Boolean kerning;
    /**
     * Horizontal alignment of the text within the object<br>
     * (`left` (default), `center` or `right`)
     * 
     */
    @XmlAttribute(name = "halign")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-03-18T01:06:44+01:00", comments = "JAXB RI v2.2.11")
    protected HorizontalAlignment halign;
    /**
     * Vertical alignment of the text within the object<br>
     * (`left` (default), `center` or `right`)
     * 
     */
    @XmlAttribute(name = "valign")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-03-18T01:06:44+01:00", comments = "JAXB RI v2.2.11")
    protected VerticalAlignment valign;

    /**
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-03-18T01:06:44+01:00", comments = "JAXB RI v2.2.11")
    public String getValue() {
        return value;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-03-18T01:06:44+01:00", comments = "JAXB RI v2.2.11")
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * The font family used (default: "sand-serif")
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-03-18T01:06:44+01:00", comments = "JAXB RI v2.2.11")
    public String getFontfamily() {
        return fontfamily;
    }

    /**
     * The font family used (default: "sand-serif")
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-03-18T01:06:44+01:00", comments = "JAXB RI v2.2.11")
    public void setFontfamily(String value) {
        this.fontfamily = value;
    }

    /**
     * The size of the font in pixels (not using points,<br>
     * because other sizes in the TMX format are also using<br>
     * pixels) (default: 16)
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-03-18T01:06:44+01:00", comments = "JAXB RI v2.2.11")
    public Integer getPixelsize() {
        return pixelsize;
    }

    /**
     * The size of the font in pixels (not using points,<br>
     * because other sizes in the TMX format are also using<br>
     * pixels) (default: 16)
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-03-18T01:06:44+01:00", comments = "JAXB RI v2.2.11")
    public void setPixelsize(Integer value) {
        this.pixelsize = value;
    }

    /**
     * Whether word wrapping is enabled (1) or disabled<br>
     * (0). Defaults to 0.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-03-18T01:06:44+01:00", comments = "JAXB RI v2.2.11")
    public Boolean isWrap() {
        return wrap;
    }

    /**
     * Whether word wrapping is enabled (1) or disabled<br>
     * (0). Defaults to 0.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-03-18T01:06:44+01:00", comments = "JAXB RI v2.2.11")
    public void setWrap(Boolean value) {
        this.wrap = value;
    }

    /**
     * Color of the text in `#AARRGGBB` or `#RRGGBB` format<br>
     * (default: #000000)
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-03-18T01:06:44+01:00", comments = "JAXB RI v2.2.11")
    public String getColor() {
        return color;
    }

    /**
     * Color of the text in `#AARRGGBB` or `#RRGGBB` format<br>
     * (default: #000000)
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-03-18T01:06:44+01:00", comments = "JAXB RI v2.2.11")
    public void setColor(String value) {
        this.color = value;
    }

    /**
     * Whether the font is bold (1) or not (0). Defaults to<br>
     *  0.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-03-18T01:06:44+01:00", comments = "JAXB RI v2.2.11")
    public Boolean isBold() {
        return bold;
    }

    /**
     * Whether the font is bold (1) or not (0). Defaults to<br>
     *  0.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-03-18T01:06:44+01:00", comments = "JAXB RI v2.2.11")
    public void setBold(Boolean value) {
        this.bold = value;
    }

    /**
     * Whether the font is italic (1) or not (0). Defaults<br>
     * to 0.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-03-18T01:06:44+01:00", comments = "JAXB RI v2.2.11")
    public Boolean isItalic() {
        return italic;
    }

    /**
     * Whether the font is italic (1) or not (0). Defaults<br>
     * to 0.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-03-18T01:06:44+01:00", comments = "JAXB RI v2.2.11")
    public void setItalic(Boolean value) {
        this.italic = value;
    }

    /**
     * Whether a line should be drawn below the text (1) or<br>
     * not (0). Defaults to 0.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-03-18T01:06:44+01:00", comments = "JAXB RI v2.2.11")
    public Boolean isUnderline() {
        return underline;
    }

    /**
     * Whether a line should be drawn below the text (1) or<br>
     * not (0). Defaults to 0.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-03-18T01:06:44+01:00", comments = "JAXB RI v2.2.11")
    public void setUnderline(Boolean value) {
        this.underline = value;
    }

    /**
     * Whether a line should be drawn through the text (1)<br>
     * or not (0). Defaults to 0.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-03-18T01:06:44+01:00", comments = "JAXB RI v2.2.11")
    public Boolean isStrikeout() {
        return strikeout;
    }

    /**
     * Whether a line should be drawn through the text (1)<br>
     * or not (0). Defaults to 0.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-03-18T01:06:44+01:00", comments = "JAXB RI v2.2.11")
    public void setStrikeout(Boolean value) {
        this.strikeout = value;
    }

    /**
     * Whether kerning should be used while rendering the<br>
     * text (1) or not (0). Default to 1.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-03-18T01:06:44+01:00", comments = "JAXB RI v2.2.11")
    public Boolean isKerning() {
        return kerning;
    }

    /**
     * Whether kerning should be used while rendering the<br>
     * text (1) or not (0). Default to 1.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-03-18T01:06:44+01:00", comments = "JAXB RI v2.2.11")
    public void setKerning(Boolean value) {
        this.kerning = value;
    }

    /**
     * Horizontal alignment of the text within the object<br>
     * (`left` (default), `center` or `right`)
     * 
     * @return
     *     possible object is
     *     {@link HorizontalAlignment }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-03-18T01:06:44+01:00", comments = "JAXB RI v2.2.11")
    public HorizontalAlignment getHalign() {
        return halign;
    }

    /**
     * Horizontal alignment of the text within the object<br>
     * (`left` (default), `center` or `right`)
     * 
     * @param value
     *     allowed object is
     *     {@link HorizontalAlignment }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-03-18T01:06:44+01:00", comments = "JAXB RI v2.2.11")
    public void setHalign(HorizontalAlignment value) {
        this.halign = value;
    }

    /**
     * Vertical alignment of the text within the object<br>
     * (`left` (default), `center` or `right`)
     * 
     * @return
     *     possible object is
     *     {@link VerticalAlignment }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-03-18T01:06:44+01:00", comments = "JAXB RI v2.2.11")
    public VerticalAlignment getValign() {
        return valign;
    }

    /**
     * Vertical alignment of the text within the object<br>
     * (`left` (default), `center` or `right`)
     * 
     * @param value
     *     allowed object is
     *     {@link VerticalAlignment }
     *     
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2019-03-18T01:06:44+01:00", comments = "JAXB RI v2.2.11")
    public void setValign(VerticalAlignment value) {
        this.valign = value;
    }

}
