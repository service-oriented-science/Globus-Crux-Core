/*
 * Executable.java
 * 
 * @version:
 *     $Id v1.0$
 *
 */
package org.cyberaide.execution;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * an abstract for all the executable job/work/workflows
 *
 */
public abstract class Executable {
		Map<String,String> attributes;
		WfType provider;
		
		/**
         * Constructor
         *
         * @param provider specify the provider(eg. karajan workflow or system script)
         */
		public Executable(WfType provider){
			this.provider = provider;
			this.attributes = new HashMap<String,String>();
		}
		
		/**
         * add attributes to the executable
         *
         * @param key attributes key
         * @param value attributes value
         */
		public void addAttributes(String key, String value){
			attributes.put(key, value);
		}
		
		/**
         * override the toString() method to output info
         *
         */
		public String toString(){
			StringBuffer ret = new StringBuffer("[Provider]:" + provider + "\n[attributes]:\n");
			Set<String> keys = attributes.keySet();
			Iterator<String> it = keys.iterator();
			while(it.hasNext()){
				String akey = it.next();
				ret.append("\t[");
				ret.append(akey);
				ret.append("]:");
				ret.append(attributes.get(akey));
				ret.append("\n");
			}
			return new String(ret);
		}
}
