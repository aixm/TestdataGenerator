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
import org.apache.ws.commons.schema.utils.NamespaceMap
import org.apache.ws.commons.schema.utils.NamespacePrefixList
import java.io.BufferedInputStream
import java.io.File
import javax.xml.namespace.QName
import javax.xml.transform.stream.StreamSource

/**
 * Parses and encapsulates the AIXM schemas.
 *
 * @author Manfred Odenstein, SOLITEC Software Solutions Ges.m.b.H.
 */
class SchemaParser(private val inputFile: File, substitutionGroup: QName) {

    val schemaCollection = XmlSchemaCollection().apply {
        setBaseUri(inputFile.absoluteFile.parent)
    }

    val schema = parse()
    val namespaceContext = createNamespaceContext(schema.namespaceContext)

    val elementsBySubstitution =
        schemaCollection.getElementsBySubstitution(substitutionGroup)

    init {
        if (elementsBySubstitution.isEmpty()) {
            throw IllegalArgumentException("Unable to find any AIXM Features, please check schema")
        }
    }

    val rootQName: QName = schema.elements.keys.first()

    val separator = getMemberSeparator()

    /**
     * Parser the schema(s)
     */
    private fun parse(): XmlSchema {
        val inputStream = BufferedInputStream(inputFile.inputStream())
        return schemaCollection.read(StreamSource(inputStream))
    }

    /**
     * Detects the separator, e.g. <aixm:hasMember>
     *
     * @return  The member separator
     */
    private fun getMemberSeparator(): QName {
        val content = (schema.elements.values.first().schemaType as XmlSchemaComplexType).contentModel.content as XmlSchemaComplexContentExtension
        val refName = ((content.particle as XmlSchemaSequence).items[0] as XmlSchemaGroupRef).refName

        return ((schemaCollection.getGroupByQName(refName).particle as XmlSchemaSequence).items[0] as XmlSchemaElement).qName
    }

    /**
     * Creates a namespace context out of the scanned one and adds the xsi one.
     *
     * @param scannedContext    The scanned namspace context.
     */
    private fun createNamespaceContext(scannedContext: NamespacePrefixList): NamespaceMap {
        val newContext = NamespaceMap()
        scannedContext.declaredPrefixes.forEach { prefix ->
            val _prefix = if ("" == prefix) "message" else prefix
            newContext.add(_prefix, scannedContext.getNamespaceURI(prefix))
        }
        newContext.add("xsi", "http://www.w3.org/2001/XMLSchema-instance")
        return newContext
    }

}