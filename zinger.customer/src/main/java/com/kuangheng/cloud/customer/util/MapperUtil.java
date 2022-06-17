package com.kuangheng.cloud.customer.util;
 
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
 
/**
 * 通过泛型和反射自动Mapper数据
* <p>Title: MapperUtil.java</p>
* <p>Description: </p>
* <p>Copyright: Copyright (c) 2016</p>
* <p>Company: YunGe</p>
* @author L
* @date 2017年5月5日 下午4:35:58
* @version 1.0
 */
public class MapperUtil<T> {
	/**
	 * List<map> mapper 装载 为List<?>
	 * @param maplist
	 * @return
	 * @throws Exception
	 */
	public List<T> mapperList(List<Map> maplist,Class<T> t) throws Exception{
		List<Object> rtnlist=new ArrayList<>();
		if(maplist==null||maplist.size()==0){
			return (List<T>)rtnlist;
		}
		for(Map map:maplist){
			Object tobj=t.newInstance();
			for(Object key:map.keySet()){
				Field field =t.getDeclaredField((String) key);
				field.setAccessible(true);
				field.set(tobj, map.get(key));
			}
			rtnlist.add(tobj);
		}
		return (List<T>)rtnlist;
	}
	
	/**
	 * 反射Mapper  数据对象
	 * @param map
	 * @param t
	 * @return
	 * @throws Exception
	 */
	public <T>T mapperObj(Map map,Class<T> t) throws Exception{
		if(map==null||map.size()==0){
			return t.newInstance();
		}
			Object tobj=t.newInstance();
			for(Object key:map.keySet()){
				Field field =t.getDeclaredField((String) key);
				field.setAccessible(true);
				field.set(tobj, map.get(key));
			}
		return (T) tobj;
	}
}
