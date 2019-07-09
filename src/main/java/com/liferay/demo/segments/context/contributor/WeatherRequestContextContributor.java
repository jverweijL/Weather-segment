/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.demo.segments.context.contributor;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.segments.context.Context;
import com.liferay.segments.context.contributor.RequestContextContributor;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.impl.provider.entity.StringProvider;

import javax.servlet.http.HttpServletRequest;


import org.osgi.service.component.annotations.Component;

/**
 * @author Kris
 */
@Component(
	immediate = true,
	property = {
		"request.context.contributor.key=" + WeatherRequestContextContributor.KEY,
		"request.context.contributor.type=String"
	},
	service = RequestContextContributor.class
)
public class WeatherRequestContextContributor
	implements RequestContextContributor {

	public static final String KEY = "weather";

	@Override
	public void contribute(
		Context context, HttpServletRequest httpServletRequest) {
		
		boolean hardCodeIt = true;
		
		if(hardCodeIt == true)
		{
			context.put(KEY, "sunny");
		}
		else {
		
						if(context.get(KEY) == null)
						{		
							//String ipAddress = httpServletRequest.getRemoteAddr();
							// Hardcoded to UK office IP Address
							String ipAddress = "92.234.68.98";
							String actualWeather = "";
							DefaultClientConfig defaultClientConfig = new DefaultClientConfig();
							defaultClientConfig.getClasses().add(JacksonJsonProvider.class);
							//You need to add this config due to OSGI craziness
							defaultClientConfig.getClasses().add(StringProvider.class);
							Client client = Client.create(defaultClientConfig);
						    WebResource webResource = client.resource("https://ipapi.co/" + ipAddress + "/json");
						    
						    ClientResponse response;
							try {
								response = webResource.get(ClientResponse.class);
								if (response.getStatus() == 200)
							    {			    
							    		
							    //parse JSON objects
							    
									    JSONObject jsonObject;
										try {
											jsonObject = JSONFactoryUtil.createJSONObject(response.getEntity(String.class));
											Object latitude = jsonObject.get("latitude");
										    Object longitude = jsonObject.get("longitude");			
										    
										    //Call weather API
										    
										    String apiKey = "1923b9fbe9106354dd670445a384b48d";
										    Client weatherClient = Client.create(defaultClientConfig);
										    WebResource weatherWebResource = weatherClient.resource("http://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&APPID=" + apiKey);
										    ClientResponse weatherResponse = weatherWebResource.get(ClientResponse.class);
										    
										    if(weatherResponse.getStatus() == 200) {
										    	
												    JSONObject weatherJsonObject = JSONFactoryUtil.createJSONObject(weatherResponse.getEntity(String.class));
												    int weatherCode = weatherJsonObject.getJSONArray("weather").getJSONObject(0).getInt("id");
												    if ((weatherCode >= 200 && weatherCode < 600) || weatherCode == 900 || weatherCode == 901 || weatherCode == 902) {
												    	actualWeather="rainy";
												    }
												    else
												    	if (weatherCode >= 600 && weatherCode <= 622) {
													    	actualWeather="snowy";
													    }
												    	else
												    		if (weatherCode == 800 || weatherCode == 801 ) {
												    			actualWeather="sunny";
												    		}
												    		else
												    			if (weatherCode == 802 || weatherCode == 803|| weatherCode == 804) {
												    				actualWeather="cloudy";
												    			}
												    			else
												    				if (weatherCode == 905 || weatherCode >= 956 ) {
												    					actualWeather="windy";
												    				}
												    				else
												    					if (weatherCode == 701 || weatherCode == 721 || weatherCode == 771 || weatherCode == 741) {
												    						actualWeather="misty";
												    					}
												    						
												    System.out.println("The weather rule is running and the weather is " + actualWeather);
												    context.put(KEY, actualWeather);
										    }
										    else {
										    	context.put(KEY, "unknown");
										    }
											
											
										} catch (JSONException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										} catch (ClientHandlerException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										} catch (UniformInterfaceException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										
							    }
							    else {
							    	context.put(KEY, "unknown");
							    
							    }
							} catch (UniformInterfaceException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (ClientHandlerException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} 
						    
						}
		}
	}
}