/**
 * 
 */
package com.yuncore.bdfs.client.local;

import java.util.List;

/**
 * @author ouyangfeng
 *
 */
public interface FileExclude {
	
	public void addExclude(String file);
	
	public void addExclude(List<String> files);

	public List<String> getExcludes();
	
	public boolean rmExclude(String exclude);
	
}
