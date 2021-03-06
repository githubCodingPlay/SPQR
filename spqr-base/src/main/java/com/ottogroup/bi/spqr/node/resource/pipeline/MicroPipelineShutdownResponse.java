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
package com.ottogroup.bi.spqr.node.resource.pipeline;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Message sent in response to pipeline shutdown request issued towards a spqr node
 * @author mnxfst
 * @since Mar 16, 2015
 */
public class MicroPipelineShutdownResponse implements Serializable {

	private static final long serialVersionUID = 212062638728588678L;

	public enum MicroPipelineShutdownState implements Serializable {
		OK, PIPELINE_ID_MISSING, TECHNICAL_ERROR
	}
	
	@JsonProperty(value="state", required=true)
	private MicroPipelineShutdownState state = MicroPipelineShutdownState.OK;
	@JsonProperty(value="msg", required=true)
	private String message = null;
	@JsonProperty(value="pid", required=true)
	private String pipelineId = null;
	
	public MicroPipelineShutdownResponse() {		
	}
	
	/**
	 * Initializes the response using the provided input
	 * @param pipelineId
	 * @param state
	 * @param msg
	 */
	public MicroPipelineShutdownResponse(final String pipelineId, final MicroPipelineShutdownState state, final String msg) {
		this.pipelineId = pipelineId;
		this.state = state;
		this.message = msg;
	}

	public MicroPipelineShutdownState getState() {
		return state;
	}

	public void setState(MicroPipelineShutdownState state) {
		this.state = state;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getPipelineId() {
		return pipelineId;
	}

	public void setPipelineId(String pipelineId) {
		this.pipelineId = pipelineId;
	}
	
	
}
