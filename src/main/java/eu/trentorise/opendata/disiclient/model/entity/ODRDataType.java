package eu.trentorise.opendata.disiclient.model.entity;

import it.unitn.disi.sweb.webapi.model.kb.types.DataType;

/**
 * @author Ivan Tankoyeu <tankoyeu@disi.unitn.it>
 * @date 26 Feb 2014
 * 
 */
public class ODRDataType {

    public static final String STRING = "STRING";
    public static final String BOOLEAN = "BOOLEAN";
    public static final String DATE = "DATE";
    public static final String INTEGER = "INTEGER";
    public static final String FLOAT = "FLOAT";
    public static final String LONG = "LONG";
    public static final String CONCEPT = "CONCEPT";
    public static final String SSTRING = "SSTRING";
    public static final String NLSTRING = "NLSTRING";
    public static final String COMPLEX_TYPE = "COMPLEX_TYPE";
    public static final String STRUCTURE = "STRUCTURE";
    public static final String ENTITY = "ENTITY";
    

	
	


DataType convertDataType(String dataType){
	
	if (dataType.equals(ODRDataType.STRING)) return DataType.STRING;
	if (dataType.equals(ODRDataType.SSTRING)) return DataType.SSTRING;
	if (dataType.equals(ODRDataType.BOOLEAN)) return DataType.BOOLEAN;
	if (dataType.equals(ODRDataType.COMPLEX_TYPE)) return DataType.COMPLEX_TYPE;

	if (dataType.equals(ODRDataType.CONCEPT)) return DataType.CONCEPT;
	if (dataType.equals(ODRDataType.DATE)) return DataType.DATE;
	if (dataType.equals(ODRDataType.FLOAT)) return DataType.FLOAT;
	if (dataType.equals(ODRDataType.INTEGER)) return DataType.INTEGER;
	if (dataType.equals(ODRDataType.LONG)) return DataType.LONG;
	if (dataType.equals(ODRDataType.NLSTRING)) return DataType.NLSTRING;
	else return null;
}
	
}