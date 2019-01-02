package net.cis.service.impl;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import net.cis.service.CarParkingPlaceService;
import net.cis.util.InvoiceCenterApplicationUtil;
import net.cis.utils.RestfulUtil;

/**
 * Created by NhanNguyen
 */
@Service
public class CarParkingPlaceServiceImpl implements CarParkingPlaceService {

	@Override
	public String getCarPPCodeById(String id) throws JSONException {
		String carppCode = "";
		String url = InvoiceCenterApplicationUtil.GET_CPP_CODE_BY_CPP_ID;
		url = url.replace("{id}", id);
		
		String response = RestfulUtil.getWithOutAccessToke(url, "application/json");
		JSONObject jsonObject = new JSONObject(response);
		
		carppCode = jsonObject.getString("parkingCode");
		
		return carppCode;
	}
	
}
 