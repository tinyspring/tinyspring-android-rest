package com.tinyspring.android.rest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import com.tinyspring.android.rest.annotations.Get;
import com.tinyspring.android.rest.annotations.Post;

public class RestProxy {

  private static final Logger logger = LoggerFactory.getLogger(RestProxy.class);

  private RestTemplate restTemplate;

  private RestTemplate getRestTemplate() {
    return this.restTemplate;
  }

  public void setRestTemplate(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  InvocationHandler handler = new InvocationHandler() {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      Object result = null;
      try {
        if (method.isAnnotationPresent(Post.class)) {
          String url = method.getAnnotation(Post.class).value();
          result = getRestTemplate().postForObject(url, args, method.getReturnType());
        } else if (method.isAnnotationPresent(Get.class)) {
          String url = method.getAnnotation(Get.class).value();
          result = getRestTemplate().getForObject(url, method.getReturnType(), args);
        }
      } catch (Exception e) {
        logger.error("Problem when calling rest template", e);
      }
      return result;
    }
  };

  @SuppressWarnings("unchecked")
  public <T> T createProxy(Class<T> t) {
    return (T) Proxy.newProxyInstance(t.getClassLoader(), new Class[] { t }, handler);
  }
}