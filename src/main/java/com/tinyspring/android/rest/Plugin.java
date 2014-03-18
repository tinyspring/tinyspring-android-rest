package com.tinyspring.android.rest;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.app.Activity;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.tinyspring.android.plugin.IActivityPlugin;
import com.tinyspring.android.plugin.InjectFieldsPlugin;
import com.tinyspring.android.rest.annotations.Rest;
import com.tinyspring.springframework.context.ApplicationContext;
import com.tinyspring.springframework.context.ApplicationContextAware;
import com.tinyspring.springframework.util.ReflectionUtils;
import com.tinyspring.springframework.util.ReflectionUtils.FieldCallback;
import com.tinyspring.springframework.util.ReflectionUtils.FieldFilter;

/**
 * This plugin is responsible for injecting rest clients into fields annotated
 * with @OnClick.
 * 
 * @author 35pr17
 * 
 */
public class Plugin implements IActivityPlugin, ApplicationContextAware {

	private RestProxy restProxy;

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
			ReflectionUtils.setField(field, activity, getRestProxy().createProxy(field.getType()));
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

	public RestProxy getRestProxy() {
		if (this.restProxy == null) {
			this.loadDefaultProxy();
		}
		return restProxy;
	}

	public void setRestProxy(RestProxy restProxy) {
		this.restProxy = restProxy;
	}

	protected void loadDefaultProxy() {
		RestTemplate restTemplate = new RestTemplate();
		List<MediaType> mediaTypes = new ArrayList<MediaType>();
		mediaTypes.add(MediaType.APPLICATION_JSON);
		mediaTypes.add(MediaType.APPLICATION_XHTML_XML);
		mediaTypes.add(MediaType.TEXT_HTML);

		GsonBuilder builder = new GsonBuilder();

		builder.registerTypeAdapter(Date.class, new JsonDeserializer() {
			@Override
			public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
				return new Date(json.getAsJsonPrimitive().getAsLong());
			}
		});

		GsonHttpMessageConverter gson = new GsonHttpMessageConverter();
		gson.setSupportedMediaTypes(mediaTypes);
		gson.setGson(builder.create());

		restTemplate.getMessageConverters().add(gson);
		
	    this.restProxy = new RestProxy();
	    this.restProxy.setRestTemplate(restTemplate);
	}
}
