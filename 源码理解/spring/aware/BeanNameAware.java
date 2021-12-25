package org.springframework.beans.factory;

// bean名称感知器
// 实现此接口的bean 则是向BeanFactory获取此bean在容器中的id
// BeanFactory会感知到 则会将bean的id注入到此bean中
// class Demo implements BeanNameAware{
	
//	private String id;
	
//	private String name;

//    void setBeanName(String var1){
//		this.id = var1;
//	}
}
public interface BeanNameAware extends Aware {
	// 设置名称
    void setBeanName(String var1);
}