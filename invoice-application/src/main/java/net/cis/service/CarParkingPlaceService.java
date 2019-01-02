package net.cis.service;

import org.json.JSONException;

/**
 * Created by NhanNguyen
 */
public interface CarParkingPlaceService {
	
	String getCarPPCodeById(String id) throws JSONException;
	
}
 