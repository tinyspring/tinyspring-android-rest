package com.tinyspring.android.rest;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.Activity;

import com.tinyspring.android.plugin.APlugin;
import com.tinyspring.android.plugin.InjectFieldsPlugin;
import com.tinyspring.android.rest.annotations.Rest;
import com.tinyspring.springframework.context.ApplicationContext;
import com.tinyspring.springframework.context.ApplicationContextAware;
import com.tinyspring.springframework.util.ReflectionUtils;
import com.tinyspring.springframework.util.ReflectionUtils.FieldCallback;
import com.tinyspring.springframework.util.ReflectionUtils.FieldFilter;

/**
 * This plugin is responsible for injecting rest clients into fields annotated with @OnClick.
 * 
 * @author 35pr17
 * 
 */
public class Plugin extends APlugin implements ApplicationContextAware {

  private static RestProxy restProxy = new RestProxy();

  private FieldFilter fieldInjectFilter = new FieldFilter() {

    @Override
    public boolean matches(Field field) {
      return field.getAnnotation(Rest.class) != null;
    }
  };

  private class FieldInjectProcessor implements FieldCallback {

    Activity activity;

    public FieldInjectProcessor(Activity activity) {
      this.activity = activity;
    }

    @Override
    public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
      log.debug("Processing rest injection for field '{}'", field.getName());
      ReflectionUtils.makeAccessible(field);
      ReflectionUtils.setField(field, activity, restProxy.getProxy(field.getType()));
    }
  };

  private static final Logger log = LoggerFactory.getLogger(InjectFieldsPlugin.class);

  ApplicationContext applicationContext;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @Override
  public void onActivityCreate(Activity activity) {
    ReflectionUtils.doWithFields(activity.getClass(), new FieldInjectProcessor(activity), fieldInjectFilter);
  }
}
