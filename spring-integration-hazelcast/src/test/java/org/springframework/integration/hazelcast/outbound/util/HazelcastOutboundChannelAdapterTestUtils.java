/*
 * Copyright 2016-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.integration.hazelcast.outbound.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import org.springframework.integration.hazelcast.HazelcastIntegrationTestUser;
import org.springframework.integration.hazelcast.HazelcastTestRequestHandlerAdvice;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;

import com.hazelcast.core.ITopic;
import com.hazelcast.core.MessageListener;
import com.hazelcast.core.MultiMap;
import com.hazelcast.core.ReplicatedMap;

/**
 * Util Class for Hazelcast Outbound Channel Adapter Test Support
 *
 * @author Eren Avsarogullari
 *
 * @since 1.0.0
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public final class HazelcastOutboundChannelAdapterTestUtils {

	public static final int DATA_COUNT = 100;

	public static final int DEFAULT_AGE = 5;

	public static final String TEST_NAME = "Test_Name";

	public static final String TEST_SURNAME = "Test_Surname";

	public static void testWriteToDistributedMap(MessageChannel channel,
			Map<?, ?> distributedMap,
			HazelcastTestRequestHandlerAdvice requestHandlerAdvice) {
		testWriteToMap(channel, distributedMap, requestHandlerAdvice);
	}

	private static void testWriteToMap(MessageChannel channel,
			Map<?, ?> distributedMap,
			HazelcastTestRequestHandlerAdvice requestHandlerAdvice) {
		try {
			sendMessageToChannel(channel);
			assertTrue(requestHandlerAdvice.executeLatch.await(10, TimeUnit.SECONDS));
			verifyMapForPayload(new TreeMap(distributedMap));
		}
		catch (InterruptedException e) {
			fail("Test has been failed due to " + e.getMessage());
		}
	}

	public static void testBulkWriteToDistributedMap(MessageChannel channel,
			Map<?, ?> distributedMap,
			HazelcastTestRequestHandlerAdvice requestHandlerAdvice) {
		try {
			Map<Integer, HazelcastIntegrationTestUser> userMap =
					new HashMap<>(DATA_COUNT);
			for (int index = 1; index <= DATA_COUNT; index++) {
				userMap.put(index, getTestUser(index));
			}

			channel.send(new GenericMessage<>(userMap));

			assertTrue(requestHandlerAdvice.executeLatch.await(10, TimeUnit.SECONDS));
			verifyMapForPayload(new TreeMap(distributedMap));
		}
		catch (InterruptedException e) {
			fail("Test has been failed due to " + e.getMessage());
		}
	}

	public static void testWriteToMultiMap(MessageChannel channel,
			MultiMap<Integer, HazelcastIntegrationTestUser> multiMap,
			HazelcastTestRequestHandlerAdvice requestHandlerAdvice) {
		try {
			sendMessageToChannel(channel);
			assertTrue(requestHandlerAdvice.executeLatch.await(10, TimeUnit.SECONDS));
			verifyMultiMapForPayload(multiMap);
		}
		catch (InterruptedException e) {
			fail("Test has been failed due to " + e.getMessage());
		}
	}

	public static void testWriteToReplicatedMap(MessageChannel channel,
			ReplicatedMap<Integer, HazelcastIntegrationTestUser> replicatedMap,
			HazelcastTestRequestHandlerAdvice requestHandlerAdvice) {
		testWriteToMap(channel, replicatedMap, requestHandlerAdvice);
	}

	public static void testWriteToDistributedList(MessageChannel channel,
			List<HazelcastIntegrationTestUser> distributedList,
			HazelcastTestRequestHandlerAdvice requestHandlerAdvice) {
		testWriteToDistributedCollection(channel, distributedList, requestHandlerAdvice);
	}

	private static void testWriteToDistributedCollection(MessageChannel channel,
			Collection<HazelcastIntegrationTestUser> distributedList,
			HazelcastTestRequestHandlerAdvice requestHandlerAdvice) {
		try {
			sendMessageToChannel(channel);
			assertTrue(requestHandlerAdvice.executeLatch.await(10, TimeUnit.SECONDS));
			verifyCollection(distributedList, DATA_COUNT);
		}
		catch (InterruptedException e) {
			fail("Test has been failed due to " + e.getMessage());
		}
	}

	public static void testWriteToDistributedSet(MessageChannel channel,
			Set<HazelcastIntegrationTestUser> distributedSet,
			HazelcastTestRequestHandlerAdvice requestHandlerAdvice) {
		try {
			sendMessageToChannel(channel);
			assertTrue(requestHandlerAdvice.executeLatch.await(10, TimeUnit.SECONDS));
			final List<HazelcastIntegrationTestUser> list = new ArrayList(distributedSet);
			Collections.sort(list);
			verifyCollection(list, DATA_COUNT);
		}
		catch (InterruptedException e) {
			fail("Test has been failed due to " + e.getMessage());
		}
	}

	public static void testWriteToDistributedQueue(MessageChannel channel,
			Queue<HazelcastIntegrationTestUser> distributedQueue,
			HazelcastTestRequestHandlerAdvice requestHandlerAdvice) {
		testWriteToDistributedCollection(channel, distributedQueue, requestHandlerAdvice);
	}

	public static void testWriteToTopic(MessageChannel channel,
			ITopic<HazelcastIntegrationTestUser> topic,
			HazelcastTestRequestHandlerAdvice requestHandlerAdvice) {
		try {
			topic.addMessageListener(new MessageListener() {

				private int index = 1;

				@Override
				public void onMessage(com.hazelcast.core.Message message) {
					HazelcastIntegrationTestUser user =
							(HazelcastIntegrationTestUser) message.getMessageObject();
					verifyHazelcastIntegrationTestUser(user, index);
					index++;
				}
			});
			sendMessageToChannel(channel);
			assertTrue(requestHandlerAdvice.executeLatch.await(10, TimeUnit.SECONDS));
		}
		catch (InterruptedException e) {
			fail("Test has been failed due to " + e.getMessage());
		}

	}

	public static HazelcastIntegrationTestUser getTestUser(int index) {
		return new HazelcastIntegrationTestUser(index, TEST_NAME, TEST_SURNAME,
				index + DEFAULT_AGE);
	}

	public static void verifyMapForPayload(
			final Map<Integer, HazelcastIntegrationTestUser> map) {
		int index = 1;
		assertNotNull(map);
		assertEquals(true, map.size() == DATA_COUNT);
		for (Map.Entry<Integer, HazelcastIntegrationTestUser> entry : map.entrySet()) {
			assertNotNull(entry);
			assertEquals(index, entry.getKey().intValue());
			verifyHazelcastIntegrationTestUser(entry.getValue(), index);
			index++;
		}
	}

	public static void verifyCollection(
			final Collection<HazelcastIntegrationTestUser> coll, final int dataCount) {
		int index = 1;
		assertNotNull(coll);
		assertEquals(true, coll.size() == dataCount);
		for (HazelcastIntegrationTestUser user : coll) {
			verifyHazelcastIntegrationTestUser(user, index);
			index++;
		}
	}

	public static void verifyHazelcastIntegrationTestUser(
			HazelcastIntegrationTestUser user, int index) {
		assertNotNull(user);
		assertEquals(index, user.getId());
		assertEquals(TEST_NAME, user.getName());
		assertEquals(TEST_SURNAME, user.getSurname());
		assertEquals(index + DEFAULT_AGE, user.getAge());
	}

	private static void sendMessageToChannel(final MessageChannel channel) {
		for (int index = 1; index <= DATA_COUNT; index++) {
			channel.send(new GenericMessage<>(getTestUser(index)));
		}
	}

	private static void verifyMultiMapForPayload(
			final MultiMap<Integer, HazelcastIntegrationTestUser> multiMap) {
		int index = 1;
		assertNotNull(multiMap);
		assertEquals(true, multiMap.size() == DATA_COUNT);
		SortedSet<Integer> keys = new TreeSet<>(multiMap.keySet());
		for (Integer key : keys) {
			assertNotNull(key);
			assertEquals(index, key.intValue());
			HazelcastIntegrationTestUser user = multiMap.get(key).iterator().next();
			verifyHazelcastIntegrationTestUser(user, index);
			index++;
		}
	}

	private HazelcastOutboundChannelAdapterTestUtils() {
	}

}
