/**
 * Copyright 2013 Jaroslaw Palka<jaroslaw.palka@symentis.pl>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.integration.jgroups;

import java.util.Map;

import org.jgroups.Message;
import org.springframework.integration.MessageHeaders;

/**
 *
 * @author Jaroslaw Palka <jaroslaw.palka@symentis.pl>
 * @since 1.0.0
 *
 */
public class CustomJGroupsHeaderMapper implements JGroupsHeaderMapper {

	@Override
	public void fromHeaders(MessageHeaders headers, Message target) {
		// no op

	}

	@Override
	public Map<String, Object> toHeaders(Message source) {
		// no op
		return null;
	}

}

