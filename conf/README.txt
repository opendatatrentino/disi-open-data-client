For tests to succeed you need to put in this directory a file named sweb-webapi-model-override.properties 
overriding at least the properties in the "Mandatory configuration section" of src/main/resources/META-INF/sweb-webapi-model.properties

Notice sweb-webapi-model-override is called differently from sweb-webapi-model.properties on purpose so it won't be found 
by sweb client and we can test Disi client own overriding mechanism.

Files in this directory won't be versioned.

