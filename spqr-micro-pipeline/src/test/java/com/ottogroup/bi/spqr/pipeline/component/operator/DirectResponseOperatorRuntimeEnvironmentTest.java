/**
 * Copyright 2015 Otto (GmbH & Co KG)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ottogroup.bi.spqr.pipeline.component.operator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.ottogroup.bi.spqr.exception.RequiredInputMissingException;
import com.ottogroup.bi.spqr.pipeline.message.StreamingDataMessage;
import com.ottogroup.bi.spqr.pipeline.queue.StreamingMessageQueueConsumer;
import com.ottogroup.bi.spqr.pipeline.queue.StreamingMessageQueueProducer;
import com.ottogroup.bi.spqr.pipeline.queue.strategy.StreamingMessageQueueWaitStrategy;

/**
 * Test case for {@link DirectResponseOperatorRuntimeEnvironment}
 * @author mnxfst
 * @since Mar 11, 2015
 */
public class DirectResponseOperatorRuntimeEnvironmentTest {

	private static ExecutorService executorService = Executors.newCachedThreadPool();
	
	@AfterClass
	public static void shutdown() {
		if(executorService != null)
			executorService.shutdownNow();
	}
	
	/**
	 * Test case for {@link DirectResponseOperatorRuntimeEnvironment#DirectResponseOperatorRuntimeEnvironment("proc-id", "pipe-id",DirectResponseOperator, StreamingMessageQueueConsumer, StreamingMessageQueueProducer)}
	 * being provided null as input to processing node id parameter 
	 */
	@Test
	public void testConstructor_withNullProcessingNodeIdInput() {
		try {
			new DirectResponseOperatorRuntimeEnvironment(null, "pipe-id",  Mockito.mock(DirectResponseOperator.class), Mockito.mock(StreamingMessageQueueConsumer.class), Mockito.mock(StreamingMessageQueueProducer.class));
			Assert.fail("Missing required input");
		} catch(RequiredInputMissingException e) {
			// expected
		}
	}
	
	/**
	 * Test case for {@link DirectResponseOperatorRuntimeEnvironment#DirectResponseOperatorRuntimeEnvironment("proc-id", "pipe-id",DirectResponseOperator, StreamingMessageQueueConsumer, StreamingMessageQueueProducer)}
	 * being provided null as input to pipeline id parameter 
	 */
	@Test
	public void testConstructor_withNullPipelineIdInput() {
		try {
			new DirectResponseOperatorRuntimeEnvironment("proc-id", null, Mockito.mock(DirectResponseOperator.class), Mockito.mock(StreamingMessageQueueConsumer.class), Mockito.mock(StreamingMessageQueueProducer.class));
			Assert.fail("Missing required input");
		} catch(RequiredInputMissingException e) {
			// expected
		}
	}
	
	/**
	 * Test case for {@link DirectResponseOperatorRuntimeEnvironment#DirectResponseOperatorRuntimeEnvironment("proc-id", "pipe-id",DirectResponseOperator, StreamingMessageQueueConsumer, StreamingMessageQueueProducer)}
	 * being provided null as input to operator parameter which must lead to a {@link RequiredInputMissingException}
	 */
	@Test
	public void testConstructor_withNullOperatorInput() {
		try {
			new DirectResponseOperatorRuntimeEnvironment("proc-id", "pipe-id", null, Mockito.mock(StreamingMessageQueueConsumer.class), Mockito.mock(StreamingMessageQueueProducer.class));
			Assert.fail("Missing required input");
		} catch(RequiredInputMissingException e) {
			// expected
		}
	}

	/**
	 * Test case for {@link DirectResponseOperatorRuntimeEnvironment#DirectResponseOperatorRuntimeEnvironment("proc-id", "pipe-id",DirectResponseOperator, StreamingMessageQueueConsumer, StreamingMessageQueueProducer)}
	 * being provided null as input to queue consumer parameter which must lead to a {@link RequiredInputMissingException}
	 */
	@Test
	public void testConstructor_withNullConsumerInput() {
		try {
			new DirectResponseOperatorRuntimeEnvironment("proc-id", "pipe-id",Mockito.mock(DirectResponseOperator.class), null, Mockito.mock(StreamingMessageQueueProducer.class));
			Assert.fail("Missing required input");
		} catch(RequiredInputMissingException e) {
			// expected
		}
	}

	/**
	 * Test case for {@link DirectResponseOperatorRuntimeEnvironment#DirectResponseOperatorRuntimeEnvironment("proc-id", "pipe-id",DirectResponseOperator, StreamingMessageQueueConsumer, StreamingMessageQueueProducer)}
	 * being provided null as input to queue producer parameter which must lead to a {@link RequiredInputMissingException}
	 */
	@Test
	public void testConstructor_withNullProducerInput() {
		try {
			new DirectResponseOperatorRuntimeEnvironment("proc-id", "pipe-id",Mockito.mock(DirectResponseOperator.class), Mockito.mock(StreamingMessageQueueConsumer.class), null);
			Assert.fail("Missing required input");
		} catch(RequiredInputMissingException e) {
			// expected
		}
	}
	
	/**
	 * Test case for {@link DirectResponseOperatorRuntimeEnvironment#DirectResponseOperatorRuntimeEnvironment("proc-id", "pipe-id",DirectResponseOperator, StreamingMessageQueueConsumer, StreamingMessageQueueProducer)}
	 * being provided valid input. The {@link DirectResponseOperatorRuntimeEnvironment#isRunning()} method must show true now
	 */
	@Test
	public void testConstructor_withValidInput() throws RequiredInputMissingException  {
		DirectResponseOperatorRuntimeEnvironment env = new DirectResponseOperatorRuntimeEnvironment("proc-id", "pipe-id",Mockito.mock(DirectResponseOperator.class), Mockito.mock(StreamingMessageQueueConsumer.class), Mockito.mock(StreamingMessageQueueProducer.class));
		Assert.assertTrue("The environment must be running", env.isRunning());
	}
	
	/**
	 * Test case for {@link DirectResponseOperatorRuntimeEnvironment} being provided valid input to constructor and some messages on the input queue
	 * which must be processed but lead to an error. No output must be available at the output queue but the environment must be still alive.
	 */
	@Test
	public void testConstructor_withMessagesToProcessAndFailingOperator() throws RequiredInputMissingException, InterruptedException {
		StreamingDataMessage inputMessage = new StreamingDataMessage("test".getBytes(), System.currentTimeMillis());
		DirectResponseOperator operator = Mockito.mock(DirectResponseOperator.class);
		Mockito.when(operator.onMessage(inputMessage)).thenThrow(new RuntimeException("Failed to process message"));
		StreamingMessageQueueConsumer queueConsumer = Mockito.mock(StreamingMessageQueueConsumer.class);
		StreamingMessageQueueWaitStrategy queueConsumerStrategy = Mockito.mock(StreamingMessageQueueWaitStrategy.class);
		Mockito.when(queueConsumer.getWaitStrategy()).thenReturn(queueConsumerStrategy);
		
		Mockito.when(queueConsumerStrategy.waitFor(queueConsumer)).thenReturn(inputMessage);
		StreamingMessageQueueProducer queueProducer = Mockito.mock(StreamingMessageQueueProducer.class);
		StreamingMessageQueueWaitStrategy queueProducerStrategy = Mockito.mock(StreamingMessageQueueWaitStrategy.class);
		Mockito.when(queueProducer.getWaitStrategy()).thenReturn(queueProducerStrategy);
		
		DirectResponseOperatorRuntimeEnvironment env = new DirectResponseOperatorRuntimeEnvironment("proc-id", "pipe-id",operator, queueConsumer, queueProducer);
		executorService.submit(env);

		Mockito.verify(queueProducer).getWaitStrategy();
		Mockito.verify(queueConsumer).getWaitStrategy();

		Mockito.verify(operator, Mockito.timeout(500).atLeastOnce()).onMessage(inputMessage);
		Mockito.verify(queueConsumerStrategy, Mockito.timeout(500).atLeastOnce()).waitFor(queueConsumer);
		Mockito.verify(queueProducerStrategy, Mockito.never()).forceLockRelease();
		Mockito.verify(queueProducer, Mockito.never()).insert(inputMessage);
		
		Assert.assertTrue("The environment must be running", env.isRunning());
	}
	
	/**
	 * Test case for {@link DirectResponseOperatorRuntimeEnvironment} being provided valid input to constructor and some messages on the input queue
	 * which must be processed and forwarded to the output queue.
	 */
	@Test
	public void testConstructor_withMessagesToProcess() throws RequiredInputMissingException, InterruptedException {
		StreamingDataMessage inputMessage = new StreamingDataMessage("test".getBytes(), System.currentTimeMillis());
		DirectResponseOperator operator = Mockito.mock(DirectResponseOperator.class);
		Mockito.when(operator.onMessage(inputMessage)).thenReturn(new StreamingDataMessage[]{inputMessage});
		StreamingMessageQueueConsumer queueConsumer = Mockito.mock(StreamingMessageQueueConsumer.class);
		StreamingMessageQueueWaitStrategy queueConsumerStrategy = Mockito.mock(StreamingMessageQueueWaitStrategy.class);
		Mockito.when(queueConsumer.getWaitStrategy()).thenReturn(queueConsumerStrategy);
		
		Mockito.when(queueConsumerStrategy.waitFor(queueConsumer)).thenReturn(inputMessage);
		StreamingMessageQueueProducer queueProducer = Mockito.mock(StreamingMessageQueueProducer.class);
		StreamingMessageQueueWaitStrategy queueProducerStrategy = Mockito.mock(StreamingMessageQueueWaitStrategy.class);
		Mockito.when(queueProducer.getWaitStrategy()).thenReturn(queueProducerStrategy);
		
		DirectResponseOperatorRuntimeEnvironment env = new DirectResponseOperatorRuntimeEnvironment("proc-id", "pipe-id",operator, queueConsumer, queueProducer);
		executorService.submit(env);

		Mockito.verify(queueProducer).getWaitStrategy();
		Mockito.verify(queueConsumer).getWaitStrategy();
		
		Mockito.verify(operator, Mockito.timeout(500).atLeastOnce()).onMessage(inputMessage);
		Mockito.verify(queueConsumerStrategy, Mockito.timeout(500).atLeastOnce()).waitFor(queueConsumer);
		Mockito.verify(queueProducerStrategy, Mockito.timeout(500).atLeastOnce()).forceLockRelease();
		Mockito.verify(queueProducer, Mockito.timeout(500).atLeastOnce()).insert(inputMessage);		
		Assert.assertTrue("The environment must be running", env.isRunning());
	}
	
	/**
	 * Test case for {@link DirectResponseOperatorRuntimeEnvironment#shutdown()} where the environment previously received valid data.
	 * The attached mock objects must register the shut down attempt
	 */
	@Test
	public void testShutdown_withValidSetup() throws RequiredInputMissingException {

		StreamingDataMessage inputMessage = new StreamingDataMessage("test".getBytes(), System.currentTimeMillis());
		DirectResponseOperator operator = Mockito.mock(DirectResponseOperator.class);
		Mockito.when(operator.onMessage(inputMessage)).thenReturn(new StreamingDataMessage[]{inputMessage});
		StreamingMessageQueueConsumer queueConsumer = Mockito.mock(StreamingMessageQueueConsumer.class);
		Mockito.when(queueConsumer.next()).thenReturn(inputMessage);
		StreamingMessageQueueProducer queueProducer = Mockito.mock(StreamingMessageQueueProducer.class);
		
		DirectResponseOperatorRuntimeEnvironment env = new DirectResponseOperatorRuntimeEnvironment("proc-id", "pipe-id",operator, queueConsumer, queueProducer);		
		Assert.assertTrue("The environment must be running", env.isRunning());
		env.shutdown();
		
		Mockito.verify(operator, Mockito.timeout(500)).shutdown();

	}
	
}
