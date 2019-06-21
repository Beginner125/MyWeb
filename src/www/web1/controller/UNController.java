package www.web1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import www.web1.javaBean.User;
import www.web1.mapper.UserMapper;
import www.web1.service.impl.UserServiceImpl;
import www.web1.utils.EmailUtils;
import www.web1.utils.GenerateLinkUtils;

@Controller
@SessionAttributes("user")
public class UNController {
	@Autowired
	UserMapper um;
	
	@Autowired
	UserServiceImpl us;
	
	/**用户登录
	 * @param email 用户的邮箱
	 * @param password 用户密码
	 * @param model 获取的模型参数
	 * @return 如果登录成功，重定位：{@link www.web1.controller.EDController#displayContent(int)}
	 * 否则重定位：login.jsp
	 */
	@PostMapping("loginServlet")
	public ModelAndView LoginServlet(@RequestParam("email")String email,
			@RequestParam("password")String password,
			Model model){
		ModelAndView mav = new ModelAndView();
		User user = us.checklogin(email, password);
		if(user != null){
			model.addAttribute("user", user);
			mav.setViewName("redirect:/ed-index");
		}else{
			mav.setViewName("redirect:/login.jsp");
		}
		return mav;
	}
	/**激活账户
	 * @param id 待激活账户
	 * @param checkCode 校验码，用于判断是否是正确激活链接
	 * @return 显示：info.jsp
	 */
	@RequestMapping("activateAccount")
	public ModelAndView activateAccount(@RequestParam("id") int id, 
			@RequestParam("checkCode") String checkCode){
		ModelAndView mav = new ModelAndView();
		String info = "";
		User user = um.getByID(id);
		if(user != null && !user.isActived()){
			if(checkCode.equals(GenerateLinkUtils.generateCheckcode(user))){
				user.setActived(true);
				um.updateUser(user);
				info = "你已经激活啦，可以随时用此账号登录哦！";
			}else{
				info = "激活失败，验证错误";
			}
		}else{
			info = "你已经激活啦，不用再激活咯！";
		}
		
		mav.addObject("info", info);
		mav.setViewName("Info");
		return mav;
	}
	/**添加用户，用于注册
	 * @param user 待注册用户信息
	 * @return 如果登录成功，显示：rigister.jsp，否则显示：login.jsp
	 */
	@PostMapping("addUser")
	public String addUser(User user){
		int i = us.register(user);
		if(i == 0 || i == 1){
			return "rigister";
		}else{
			return "login";
		}
	}
	/**找回密码
	 * @param email 待找回的邮箱账号
	 * @return 显示：login.jsp
	 */
	@PostMapping("findPswdServlet")
	public String findPswdServlet(@RequestParam("email") String email){
		User user = um.getByEmail(email);
		user.randomUUID();
		um.updateUser(user);
		EmailUtils.sendResetPasswordEmail(user);
		return "login";
	}
	/**显示修改密码界面
	 * @param id 待修改密码用户id
	 * @param checkCode 校验码，用于判断是否是正确修改密码链接
	 * @return 显示：updatePswd.jsp
	 */
	@RequestMapping("updatePswdServlet")
	public ModelAndView updatePswdServlet(@RequestParam("id") int id, 
			@RequestParam("checkCode") String checkCode){
		ModelAndView mav = new ModelAndView();
		mav.addObject("id", id);
		mav.addObject("checkCode", checkCode);
		mav.setViewName("updatePswd");
		return mav;
	}
	/**修改密码
	 * @param id 待修改密码用户id
	 * @param checkCode 校验码，用于判断是否是正确修改密码链接
	 * @param password 修改的新密码
	 * @return 显示：info.jsp
	 */
	@RequestMapping("upPswdServlet")
	public ModelAndView upPswdServlet(@RequestParam("id") int id, 
			@RequestParam("checkCode") String checkCode,
			@RequestParam("password") String password){
		ModelAndView mav = new ModelAndView();
		User user = um.getByID(id);
		String info = "";
		if(checkCode.equals(GenerateLinkUtils.generateCheckcode(user))){
			user.getRandomCode();
			user.setPassword(password);
			um.updateUser(user);
			info = "修改密码成功,快点去登录吧！！！<a href='login.jsp'>点此登陆</a>";
			mav.addObject("info", info);
		}else{
			info = "校验码错误,修改密码失败！可能是链接已失效";
			mav.addObject("info", info);
		}
		mav.setViewName("Info");
		return mav;
	}
	
}
