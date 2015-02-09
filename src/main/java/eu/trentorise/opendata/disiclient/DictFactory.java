package eu.trentorise.opendata.disiclient;

import eu.trentorise.opendata.commons.Dict;
import eu.trentorise.opendata.commons.OdtUtils;
import eu.trentorise.opendata.semantics.nlp.model.SemanticText;
import it.unitn.disi.sweb.webapi.model.eb.Name;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author David Leoni
 */
public class DictFactory {

    public static Dict mapToDict(Map<String, String> map) {
        Dict.Builder dictBuilder = Dict.builder();
        Iterator<?> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            Locale l = OdtUtils.languageTagToLocale((String) pairs.getKey());
            dictBuilder = dictBuilder.putAll(l, (String) pairs.getValue());
        }
        return dictBuilder.build();
    }

    public static Dict multimapToDict(Map<String, List<String>> map) {
        Dict.Builder dictBuilder = Dict.builder();
        Iterator<?> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            Locale l = OdtUtils.languageTagToLocale((String) pairs.getKey());

            List<String> vals = (List<String>) pairs.getValue();

            dictBuilder.putAll(l, vals);

        }
        return dictBuilder.build();
    }
    
    public static Dict namesToDict(List<Name> names){
        Dict.Builder dictBuilder = Dict.builder();
        for (Name name : names) {
            dictBuilder.putAll(DictFactory.multimapToDict(name.getNames()));
        }
        return dictBuilder.build();
    }
       
    public static Dict semtextsToDict(Map<String, List<SemanticText>> semtextsMap){
    
        if (semtextsMap.isEmpty()) {
            return Dict.of();
        }

        Dict.Builder dict = Dict.builder();
        
        Iterator<?> it = semtextsMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            Locale l = OdtUtils.languageTagToLocale((String) pairs.getKey());
            List<SemanticText> vals = (List<SemanticText>) pairs.getValue();
            List<String> strings = new ArrayList();
            for (SemanticText stexts : vals) {
                strings.add(stexts.getText());
            }
            dict = dict.putAll(l, strings);
        }
        return dict.build();        
    }

}
