/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 ******************************************************************************/
package org.apache.olingo.odata2.core.ep.producer;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

import java.io.InputStream;
import java.util.Locale;

import org.apache.olingo.odata2.api.ODataServiceVersion;
import org.apache.olingo.odata2.api.commons.HttpContentType;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.commons.ODataHttpHeaders;
import org.apache.olingo.odata2.api.processor.ODataErrorContext;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.core.ep.ProviderFacadeImpl;
import org.apache.olingo.odata2.testutil.helper.StringHelper;
import org.junit.Test;

/**
 *  
 */
public class JsonErrorProducerTest {

  @Test
  public void jsonSerialization() throws Exception {
    testSerializeJSON("ErrorCode", "Message", Locale.GERMANY);
    testSerializeJSON("ErrorCode", "Message", Locale.GERMAN);
  }

  @Test
  public void jsonSerializationWithoutLocale() throws Exception {
    testSerializeJSON("ErrorCode", "Message", null);
  }

  @Test
  public void jsonSerializationEmpty() throws Exception {
    testSerializeJSON(null, null, null);
  }

  // helper method
  private void testSerializeJSON(final String errorCode, final String message, final Locale locale) throws Exception {
    ODataErrorContext ctx = new ODataErrorContext();
    ctx.setContentType(HttpContentType.APPLICATION_JSON);
    ctx.setErrorCode(errorCode);
    ctx.setHttpStatus(HttpStatusCodes.INTERNAL_SERVER_ERROR);
    ctx.setLocale(locale);
    ctx.setMessage(message);

    ODataResponse response = new ProviderFacadeImpl().writeErrorDocument(ctx);
    assertNull("EntitypProvider must not set content header", response.getContentHeader());
    assertEquals(ODataServiceVersion.V10, response.getHeader(ODataHttpHeaders.DATASERVICEVERSION));
    final String jsonErrorMessage = StringHelper.inputStreamToString((InputStream) response.getEntity());
    assertEquals("{\"error\":{\"code\":"
        + (errorCode == null ? "null" : "\"" + errorCode + "\"")
        + ","
        + "\"message\":{\"lang\":"
        + (locale == null ? "null" : ("\"" + locale.getLanguage()
            + (locale.getCountry().isEmpty() ? "" : ("-" + locale.getCountry())) + "\""))
        + ",\"value\":" + (message == null ? "null" : "\"" + message + "\"") + "}}}",
        jsonErrorMessage);
  }
}
