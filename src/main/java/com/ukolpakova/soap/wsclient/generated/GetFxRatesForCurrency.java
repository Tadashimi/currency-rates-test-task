
package com.ukolpakova.soap.wsclient.generated;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>{@code
 * <complexType>
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="tp" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="ccy" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="dtFrom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="dtTo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "tp",
    "ccy",
    "dtFrom",
    "dtTo"
})
@XmlRootElement(name = "getFxRatesForCurrency")
public class GetFxRatesForCurrency {

    protected String tp;
    protected String ccy;
    protected String dtFrom;
    protected String dtTo;

    /**
     * Gets the value of the tp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTp() {
        return tp;
    }

    /**
     * Sets the value of the tp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTp(String value) {
        this.tp = value;
    }

    /**
     * Gets the value of the ccy property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCcy() {
        return ccy;
    }

    /**
     * Sets the value of the ccy property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCcy(String value) {
        this.ccy = value;
    }

    /**
     * Gets the value of the dtFrom property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDtFrom() {
        return dtFrom;
    }

    /**
     * Sets the value of the dtFrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDtFrom(String value) {
        this.dtFrom = value;
    }

    /**
     * Gets the value of the dtTo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDtTo() {
        return dtTo;
    }

    /**
     * Sets the value of the dtTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDtTo(String value) {
        this.dtTo = value;
    }

}
