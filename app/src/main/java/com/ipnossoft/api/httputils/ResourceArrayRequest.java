package com.ipnossoft.api.httputils;

import com.android.volley.*;
import com.android.volley.toolbox.HttpHeaderParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.ipnossoft.api.httputils.exceptions.ServiceError;
import com.ipnossoft.api.httputils.model.ResourcesResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
//cavaj
public class ResourceArrayRequest extends Request
{

  private final Class elementType;
  private final com.android.volley.Response.Listener listener;
  private ObjectMapper mapper;

  public ResourceArrayRequest(Class class1, int i, String s, com.android.volley.Response.Listener listener1, com.android.volley.Response.ErrorListener errorlistener)
  {
    super(i, s, errorlistener);
    elementType = class1;
    listener = listener1;
    mapper = new ObjectMapper();
  }

  protected void deliverResponse(Object obj)
  {
    deliverResponse((List)obj);
  }

  protected void deliverResponse(List list)
  {
    listener.onResponse(list);
  }

  protected Response parseNetworkResponse(NetworkResponse networkresponse)
  {
    Object obj;
    try
    {
      obj = new String(networkresponse.data, HttpHeaderParser.parseCharset(networkresponse.headers));
    }
    // Misplaced declaration of an exception variable
    catch(Exception e)
    {
      try
      {
        obj = new String(networkresponse.data);
      }
      // Misplaced declaration of an exception variable
      catch(Exception e1)
      {
        return Response.error(new VolleyError(networkresponse));
      }
    }

    try {
      if(networkresponse.statusCode == 200)
      {
        return Response.success(((ResourcesResponse)mapper.readValue(((String) (obj)), mapper.getTypeFactory().constructParametricType(ResourcesResponse.class, new Class[] {
                elementType
        }))).getResources(), HttpHeaderParser.parseCacheHeaders(networkresponse));
      }
      throw (ServiceError)mapper.readValue(((String) (obj)), ServiceError.class);
    } catch (ServiceError serviceError) {
      serviceError.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
