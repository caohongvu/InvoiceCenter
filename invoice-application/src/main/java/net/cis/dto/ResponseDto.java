package net.cis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.cis.common.web.ResponseError;

/**
 * Created by NhanNguyen
 */
public class ResponseDto {
	
	@JsonProperty("Error")
	private ResponseError error;
	
	@JsonProperty("Data")
	private Object data;

	public ResponseError getError() {
		return error;
	}

	public void setError(ResponseError error) {
		this.error = error;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
}
