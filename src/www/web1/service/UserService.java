package www.web1.service;

import org.springframework.stereotype.Service;

import www.web1.javaBean.User;
/**用户注册服务
 * 
 */
public interface UserService {
	/**用户注册
	 * 
	 */
	public int register(User user);
	/**检查登录
	 * 
	 */
	public User checklogin(String email, String password);
}
