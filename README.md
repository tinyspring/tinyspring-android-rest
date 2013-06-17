REST plugin for Tinyspring Android
=======

Automatically instantiates and injects REST clients defined by annotated interface into your Activities. The plugin uses springframework-android-rest-template under cover. 

#### Just add following lines to your ivy.xml/pom.xml

```xml
<dependency org="org.springframework.android" name="spring-android-rest-template" rev="1.0.1.RELEASE" transitive="false"/>
<dependency org="com.google.code.gson" name="gson" rev="2.2.4" transitive="false"/>
<dependency org="com.tinyspring" name="tinyspring-android-rest" rev="0.0.1" transitive="false"/>
```

```xml
<dependency>
	<groupId>org.springframework.android</groupId>
	<artifactId>spring-android-rest-template</artifactId>
	<version>1.0.1.RELEASE</version>
</dependency>
<dependency>
	<groupId>com.google.code.gson</groupId>
	<artifactId>gson</artifactId>
	<version>2.2.4</version>
</dependency>
<dependency>
	<groupId>com.tinyspring</groupId>
	<artifactId>tinyspring-android-rest</artifactId>
	<version>0.0.1</version>
</dependency>
```

#### Then define Interface for your client

```java
import java.util.List;

import com.tinyspring.android.rest.annotations.Get;

public interface RestBook {

  @Get("http://my.rest.com/item/{isbn}/")
  public Item getItem(String isbn);
  
  @Get("http://my.rest.com/items/?filter={filter}")
  public List<Item> getItems(String filter);
}
```

#### And then in your activity

```java
public class MainActivity extends Activity {

  @Rest
  private RestBook rest;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((Application) getApplicationContext()).onActivityCreate(this);
    rest.getItems("all");
  }
}
```
