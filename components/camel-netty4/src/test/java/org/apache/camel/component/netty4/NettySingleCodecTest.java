/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.netty4;

import java.util.concurrent.TimeUnit;

import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.JndiRegistry;
import org.junit.Test;

public class NettySingleCodecTest extends BaseNettyTest {

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry registry = super.createRegistry();
        StringEncoder stringEncoder = new StringEncoder();

        StringDecoder stringDecoder = new StringDecoder();

        registry.bind("encoder", stringEncoder);
        registry.bind("decoder", stringDecoder);
        return registry;
    }

    @Test
    public void canSupplySingleCodecToEndpointPipeline() throws Exception {
        String poem = new Poetry().getPoem();
        MockEndpoint mock = getMockEndpoint("mock:single-codec");
        mock.expectedBodiesReceived(poem);
        sendBody("direct:single-codec", poem);
        mock.await(1, TimeUnit.SECONDS);
        mock.assertIsSatisfied();

    }

    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() throws Exception {
                from("direct:single-codec").to("netty4:tcp://localhost:{{port}}?encoder=#encoder&sync=false");

                from("netty4:tcp://localhost:{{port}}?decoder=#decoder&sync=false").to("mock:single-codec");
            }
        };
    }
}