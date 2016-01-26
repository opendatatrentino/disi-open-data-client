<p class="josman-to-strip">
WARNING: THIS IS ONLY A TEMPLATE FOR THE DOCUMENTATION. <br/>
RELEASE DOCS ARE ON THE <a href="http://opendatatrentino.github.io/disi-open-data-client/" target="_blank">PROJECT WEBSITE</a>
</p>

### Usage


#### Configuration


Since it's not clear how much configuration read from sweb clients is shared and how much is per client, we assume configuration to be shared among clients but you can still change it at runtime. To configure at runtime a DisiClient (and also all the other disiclients at once in the jvm), do the following:

```
	DisiEkb disiEkb = new DisiEkb();
    
    Map<String,String> myProps = new HashMap();
    
        
    disiEkb.setProperties(myProps);

```
IMPORTANT: the map must contain at least the mandatory properties indicated <a href="../src/main/resources/META-INF/sweb-webapi-model.properties" target="_blank">in this file</a>


Further configuration can be handled in with static class `SwebConfiguration`, which is a hack that extends Sweb clients `Configuration` class. Currently `SwebConfiguration` is in ColumnRecognizer library for dependency hell problems todo link.


#### ID vs GUID

Sweb supports local ids but also global ids (guids)
Since mostly sweb api seems to prefer local ids, the `UrlMapper` (which is in semtext-nltext for dep hell problems) class that tranlsates from sweb id to open entity url uses only local ids.

#### Caching

Sweb clients seem not to have any caching, so we implemented Etype and Concept caching. In branch-0.11  there is some way to refresh the cache elements once they're loaded.

### Etypes

Each Etype must have a unique Concept associated to it, otherwise etype creation fails.

### Concepts

Concepts name derives from synsets associated to the Concept. To each concept you can associate a `label` string, though (and the label is _not_ a name)



