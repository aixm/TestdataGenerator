/*
##########################################################################
##########################################################################
Copyright (c) 2019, EUROCONTROL
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright
   notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright
   notice, this list of conditions and the following disclaimer in the
   documentation and/or other materials provided with the distribution.
 * Neither the name of EUROCONTROL nor the names of its contributors
   may be used to endorse or promote products derived from this software
   without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.

##########################################################################
##########################################################################
*/

package com.solitec.aixm.datagen

import org.apache.ws.commons.schema.*
import org.apache.ws.commons.schema.utils.XmlSchemaObjectBase
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.OutputStream
import java.util.*
import java.util.concurrent.atomic.AtomicLong
import javax.xml.datatype.DatatypeFactory
import javax.xml.namespace.NamespaceContext
import javax.xml.namespace.QName
import javax.xml.stream.XMLOutputFactory
import javax.xml.stream.XMLStreamWriter
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource


/**
 * This class contains just a few options for the AIXMRenderer.
 *
 * @author Manfred Odenstein, SOLITEC Software Solutions Ges.m.b.H.
 */
data class RenderOptions(val begin: GregorianCalendar, val end: GregorianCalendar?)

/**
 * Renders the document.
 *
 * @author Manfred Odenstein, SOLITEC Software Solutions Ges.m.b.H.
 */
class AIXMRenderer(private val schemaCol: XmlSchemaCollection, private val writer: XMLStreamWriter) {

    private val gmlId = AtomicLong(1)

    companion object {

        val datatypeFactory = DatatypeFactory.newInstance()

        /**
         * Internal helper method to create a XMLStreamWriter object.
         *
         * @param namespaceContext  The used namespaces.
         * @param outputStream      The target file.
         */
        private fun createXmlWriter(namespaceContext: NamespaceContext, outputStream: OutputStream): XMLStreamWriter {
            val xmlStreamWriter = XMLOutputFactory.newInstance().run {
                setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, false)
                createXMLStreamWriter(outputStream, "utf-8")
            }
            xmlStreamWriter.namespaceContext = namespaceContext
            xmlStreamWriter.setDefaultNamespace("")
            return xmlStreamWriter
        }

        /**
         * Renders the sample document.
         *
         * @param schemaParser  Instance of SchemaParser.
         * @param outputFile    The target file of the generated content.
         * @param options       Some options, currently only begin and end date for the TimePeriod element will be provided.
         */
        fun renderDocument(schemaParser: SchemaParser, outputFile: File, options: RenderOptions) {
            val beginPosition = datatypeFactory.newXMLGregorianCalendar(options.begin).toXMLFormat()
            val endPosition = if (options.end != null) {
                datatypeFactory.newXMLGregorianCalendar(options.end).toXMLFormat()
            } else {
                null
            }
            val separator = schemaParser.separator
            val tempFile: File = File.createTempFile("data_gen", null)
            tempFile.deleteOnExit()
            val bufferedOutputStream = BufferedOutputStream(tempFile.outputStream())

            bufferedOutputStream.use { outputStream ->
                val writer = createXmlWriter(schemaParser.namespaceContext, outputStream)
                val renderer = AIXMRenderer(schemaParser.schemaCollection, writer)
                writer.document {

                    element(schemaParser.rootQName) {
                        schemaParser.namespaceContext.entries.forEach {entry: MutableMap.MutableEntry<String, Any> ->
                            writeNamespace(entry.key, entry.value.toString())
                        }
                        renderer.renderGmlIdSequence()
                        schemaParser.elementsBySubstitution.forEach { t: XmlSchemaElement ->
                            element(separator) {
                                val uuid = UUID.randomUUID()
                                element(t.qName) {
                                    attribute(AIXMConstants.GML_ID, "uuid.$uuid")
                                    element(AIXMConstants.GML_IDENTIFIER, uuid.toString()) {
                                        attribute(AIXMConstants.CODE_SPACE, "urn:uuid:")
                                    }
                                    val featureElements = FeatureElements.createFromElement(t.qName)
                                    element(QName(t.qName.namespaceURI, "timeSlice")) {
                                        element(featureElements.timeSlice) {
                                            renderer.renderGmlIdSequence()
                                            renderer.renderTime(AIXMConstants.GML_VALID_TIME, beginPosition, endPosition)
                                            element(AIXMConstants.AIXM_INTERPRETATION, "BASELINE") {}
                                            element(AIXMConstants.AIXM_SEQUENCE_NUMBER, "1") {}
                                            element(AIXMConstants.AIXM_CORRECTION_NUMBER, "0") {}
                                            renderer.renderTime(AIXMConstants.AIXM_FEATURE_LIFE_TIME, beginPosition, endPosition)
                                            renderer.renderGroupRef(featureElements.propertyGroup)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                writer.flush()
            }

            // pretty print result
            val transformer = TransformerFactory.newInstance().newTransformer().apply {
                setOutputProperty(OutputKeys.INDENT, "yes")
                setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4")
            }
            outputFile.createNewFile()
            val formattedOutputStream = BufferedOutputStream(outputFile.outputStream())
            formattedOutputStream.use {
                val streamResult = StreamResult(it)
                val inputStream = BufferedInputStream(tempFile.inputStream())
                transformer.transform(StreamSource(inputStream), streamResult)
            }
        }

    }

    /**
     * helper method to render the gml:id attribute with value.
     */
    fun renderGmlIdSequence() {
        writer.attribute(AIXMConstants.GML_ID, String.format("data_%010d", gmlId.getAndIncrement()))
    }

    /**
     * helper method to render the timePeriod elements.
     *
     * @param qName     The QName object of the enclosing element of the timePeriod.
     * @param begin     The content for the beginPosition element.
     * @param end       The content for the endPosition element.
     */
    fun renderTime(qName: QName, begin: String, end: String?) {
        writer.element(qName) {
            element(AIXMConstants.GML_TIME_PERIOD) {
                renderGmlIdSequence()
                element(AIXMConstants.GML_BEGIN_POSITION, begin) {}
                if (end != null) {
                    element(AIXMConstants.GML_END_POSITION, end) {}
                } else {
                    emptyElement(AIXMConstants.GML_END_POSITION) {
                        attribute(AIXMConstants.INDETERMINATE_POSITION, "unknown")
                    }
                }
            }
        }
    }

    /**
     * Helper method to walk through the schema tree.
     *
     * @param member    Some schema object.
     */
    fun decodeCollectionMember(member: XmlSchemaObjectBase) {
        when (member) {
            is XmlSchemaGroupRef -> {
                renderGroupRef(member.refName)
            }
            is XmlSchemaGroup -> {
                renderGroup(member)
            }
            is XmlSchemaSequence -> {
                renderSequence(member)
            }
            is XmlSchemaChoice -> {
                renderChoice(member)
            }
            is XmlSchemaElement -> {
                renderElement(member)
            }
        }
    }

    /**
     * Helper method to render a group.
     *
     * @param group An instance of XmlSchemaGroup
     */
    fun renderGroup(group: XmlSchemaGroup) {
        when (val particle = group.particle) {
            is XmlSchemaSequence -> {
                renderSequence(particle)
            }
            is XmlSchemaChoice -> {
                renderChoice(particle)
            }
        }

    }

    /**
     * Helper method to render a group by reference.
     *
     * @param groupQName    The QName of the group.
     */
    fun renderGroupRef(groupQName: QName) = renderGroup(schemaCol.getGroupByQName(groupQName))

    /**
     * Helper method to render a sequence
     *
     * @param sequence  An instance of XmlSchemaSequence
     */
    fun renderSequence(sequence: XmlSchemaSequence) {
        sequence.items.forEach { decodeCollectionMember(it) }
    }

    /**
     * Helper method to render a choice, just taking the first choice option.
     *
     * @param choice    An instance of XmlSchemaChoice
     */
    fun renderChoice(choice: XmlSchemaChoice) {
        decodeCollectionMember(choice.items.first())
    }

    /**
     * Helper method to render an element, including the xsi:nil attribute
     *
     * @param element   An instance of XmlSchemaElement
     */
    fun renderElement(element: XmlSchemaElement) {
        writer.emptyElement(element.qName) {
            attribute(AIXMConstants.XSI_NIL, "true")
        }
    }

}

/**
 * Helper class to generate the timeSlice and propertyGroup QNames.
 *
 * @author Manfred Odenstein, SOLITEC Software Solutions Ges.m.b.H.
 */
data class FeatureElements(val timeSlice: QName, val propertyGroup: QName) {

    companion object {
        fun createFromElement(qName: QName): FeatureElements {
            return FeatureElements(
                QName(qName.namespaceURI, qName.localPart + "TimeSlice"),
                QName(qName.namespaceURI, qName.localPart + "PropertyGroup")
            )
        }
    }
}
