package com.tinyspring.android.rest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.tinyspring.android.rest.annotations.Get;

public class RestProxy {

  private static final Logger logger = LoggerFactory.getLogger(RestProxy.class);

  private RestTemplate restTemplate;

  private RestTemplate getRestTemplate() {
    if (this.restTemplate == null) {
      this.restTemplate = new RestTemplate();

      List<MediaType> mediaTypes = new ArrayList<MediaType>();
      mediaTypes.add(MediaType.APPLICATION_JSON);
      mediaTypes.add(MediaType.APPLICATION_XHTML_XML);
      mediaTypes.add(MediaType.TEXT_HTML);

      GsonHttpMessageConverter gson = new GsonHttpMessageConverter();
      gson.setSupportedMediaTypes(mediaTypes);
      
      this.restTemplate.getMessageConverters().add(gson);
    }
    return this.restTemplate;
  }

  InvocationHandler handler = new InvocationHandler() {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      try {
        String url = method.getAnnotation(Get.class).value();
        System.out.println("url: " + url);
        return getRestTemplate().getForObject(url, method.getReturnType(), args);
      } catch (Exception e) {
        logger.error("Problem when calling rest template", e);
      }
      return (0);
    }
  };

  @SuppressWarnings("unchecked")
  public <T> T getProxy(Class<T> t) {
    return (T) Proxy.newProxyInstance(t.getClassLoader(), new Class[] { t }, handler);
  }
}
