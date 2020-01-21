package com.newgen.logger;

public class RequestCorrelation {
	 public static final String CORRELATION_ID_HEADER = "correlationId";

	 public static  String tenantId ="tenantId";
	 
	    private static final ThreadLocal<String> id = new ThreadLocal<String>();
	    
	    private static final ThreadLocal<String> tId = new ThreadLocal<String>();

	    public static String getId() {
	        return id.get();
	    }

	    public static void setId(String correlationId) {
	        id.set(correlationId);
	    }
	    
	    public static String getTenentId() {
	        return tId.get();
	    }

	    public static void setTenantId(String tenantId) {
	    	tId.set(tenantId);
	    }
	    
}
