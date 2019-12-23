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

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import java.util.*


/**
 * Extension function to parse a XML DateTime text representation
 *
 * @return A Calendar object on success or null if text is invalid
 */
fun String.toCalendarOrNull(): GregorianCalendar? {
    return try {
        AIXMRenderer.datatypeFactory.newXMLGregorianCalendar(this).toGregorianCalendar()
    } catch (e: IllegalArgumentException) {
        null
    }
}

/**
 * Implementation of the command line parser.
 *
 * @author Manfred Odenstein, SOLITEC Software Solutions Ges.m.b.H.
 */
class DataGenCLI : CliktCommand(name = "data-gen") {

    val inputFile by argument(help = "The name of the message schema file, e.g. <path to AIXMBasicMessage.xsd>.").file(exists = true)
    val outputFile by argument(help = "The name of the output file.").file()
    val validTimeBegin by option(help = "validTime beginPosition").convert {
        it.toCalendarOrNull() ?: fail("A correct XML DateTime value is required")
    }.default(createDefaultCalendar())

    private fun createDefaultCalendar(): GregorianCalendar = GregorianCalendar(TimeZone.getTimeZone("UTC")).apply {
        set(Calendar.MILLISECOND, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.HOUR_OF_DAY, 0)
    }

    val validTimeEnd by option(help = "validTime endPosition").convert {
        it.toCalendarOrNull() ?: fail("A correct XML DateTime value is required")
    }

    override fun run() {
        val schemaParser = SchemaParser(inputFile, AIXMConstants.DEFAULT_SUBSTITUTION_GROUP)
        val options = RenderOptions(validTimeBegin, validTimeEnd)
        AIXMRenderer.renderDocument(schemaParser, outputFile, options)
        println("Done.")
    }

}

/**
 * Main entry point.
 */
fun main(args: Array<String>) = DataGenCLI().main(args)
