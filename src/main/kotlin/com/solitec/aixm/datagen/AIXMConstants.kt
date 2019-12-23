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

import javax.xml.namespace.QName

/**
 *
 *
 * @author Manfred Odenstein, SOLITEC Software Solutions Ges.m.b.H.
 */
object AIXMConstants {
    val DEFAULT_SUBSTITUTION_GROUP =
        QName("http://www.aixm.aero/schema/5.1", "AbstractAIXMFeature")
    val GML_IDENTIFIER = QName("http://www.opengis.net/gml/3.2", "identifier")
    val GML_ID = QName("http://www.opengis.net/gml/3.2", "id")
    val XSI_NIL = QName("http://www.w3.org/2001/XMLSchema-instance", "nil")
    val CODE_SPACE = QName("codeSpace")
    val INDETERMINATE_POSITION = QName("indeterminatePosition")
    val AIXM_INTERPRETATION =
        QName("http://www.aixm.aero/schema/5.1", "interpretation")
    val AIXM_SEQUENCE_NUMBER =
        QName("http://www.aixm.aero/schema/5.1", "sequenceNumber")
    val AIXM_CORRECTION_NUMBER =
        QName("http://www.aixm.aero/schema/5.1", "correctionNumber")
    val GML_VALID_TIME = QName("http://www.opengis.net/gml/3.2", "validTime")
    val AIXM_FEATURE_LIFE_TIME =
        QName("http://www.aixm.aero/schema/5.1", "featureLifetime")
    val GML_TIME_PERIOD = QName("http://www.opengis.net/gml/3.2", "TimePeriod")
    val GML_BEGIN_POSITION =
        QName("http://www.opengis.net/gml/3.2", "beginPosition")
    val GML_END_POSITION = QName("http://www.opengis.net/gml/3.2", "endPosition")

}