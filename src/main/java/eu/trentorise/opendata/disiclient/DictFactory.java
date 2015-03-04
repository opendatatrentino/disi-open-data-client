package eu.trentorise.opendata.disiclient;

import eu.trentorise.opendata.commons.Dict;
import eu.trentorise.opendata.commons.OdtUtils;
import eu.trentorise.opendata.semtext.SemText;
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
            dictBuilder = dictBuilder.put(l, (String) pairs.getValue());
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

            dictBuilder.put(l, vals);

        }
        return dictBuilder.build();
    }
    
    public static Dict namesToDict(List<Name> names){
        Dict.Builder dictBuilder = Dict.builder();
        for (Name name : names) {
            dictBuilder.put(DictFactory.multimapToDict(name.getNames()));
        }
        return dictBuilder.build();
    }
       
    public static Dict semtextsToDict(Map<String, List<SemText>> semtextsMap){
    
        if (semtextsMap.isEmpty()) {
            return Dict.of();
        }

        Dict.Builder dict = Dict.builder();
        
        Iterator<?> it = semtextsMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            Locale l = OdtUtils.languageTagToLocale((String) pairs.getKey());
            List<SemText> vals = (List<SemText>) pairs.getValue();
            List<String> strings = new ArrayList();
            for (SemText stexts : vals) {
                strings.add(stexts.getText());
            }
            dict = dict.put(l, strings);
        }
        return dict.build();        
    }

}
