package com.librarist.android.rest;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

import org.junit.Test;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.tinyspring.android.rest.annotations.Get;
import com.tinyspring.android.rest.annotations.Rest;

public class ProxyText {

  private static interface IRest {

    @Get("/nz/book/9781409100652/")
    public String getString();

  }

  @Test
  public void testDoubles() throws IOException {

    InvocationHandler handler = new InvocationHandler() {

      RestTemplate template = new RestTemplate();

      @Override
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println(proxy.getClass().getInterfaces());
        System.out.println(method.getDeclaringClass().getAnnotations()[0].toString());
        System.out.println(method.getAnnotations()[0].toString());
        System.out.println(Arrays.toString(args));

        StringBuilder sb = new StringBuilder();
        sb.append(method.getDeclaringClass().getAnnotation(Rest.class).value());
        sb.append(method.getAnnotation(Get.class).value());

        System.out.println("url: " + sb.toString());
        template.getMessageConverters().add(new GsonHttpMessageConverter());
        System.out.println(template.getForObject(sb.toString(), method.getReturnType()));
        return (0);
      }
    };

    IRest rest = (IRest) Proxy.newProxyInstance(ProxyText.class.getClassLoader(), new Class[] { IRest.class, }, handler);
    rest.getString();
  }
}
