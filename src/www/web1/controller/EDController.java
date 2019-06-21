package www.web1.controller;

import java.io.File;
import java.net.URL;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import www.web1.javaBean.Comment;
import www.web1.javaBean.Draft;
import www.web1.javaBean.Essay;
import www.web1.javaBean.News;
import www.web1.javaBean.NewsCon;
import www.web1.javaBean.NewsCon2;
import www.web1.javaBean.User;
import www.web1.javaBean.UserRel;
import www.web1.mapper.EssayDraftMapper;
import www.web1.mapper.OtherMapper;
import www.web1.mapper.UserMapper;
import www.web1.utils.Page;

@Controller
@SessionAttributes("user")
public class EDController {
	@Autowired
	EssayDraftMapper edm;
	@Autowired
	UserMapper um;
	@Autowired
	OtherMapper om;
	
	/**处理ed-content,显示文章的页面
	 * @param pid 显示的文章的pid，显示方法
	 * @return 如果文章非空，显示：ed-content.jsp,否则，显示：login.jsp
	 */
	@RequestMapping("ed-content")
	public ModelAndView displayContent(@RequestParam("pid") int pid){
		ModelAndView mav = new ModelAndView();
		Essay essay = edm.eGetByPid(pid);
		List<Comment> comment = om.getByEssayPid(pid);
		if(essay != null){
			mav.addObject("essay", essay);
			mav.addObject("comment", comment);
			mav.setViewName("ed-content");
		}else{
			mav.setViewName("login");
		}
		return mav;
	}
	
	/**提交评论，重定位方法
	 * @param content 评论的内容
	 * @param ID 评论人ID
	 * @param essayPid 评论的文章pid
	 * @return 重定位：{@link #displayContent(int)}
	 */
	@PostMapping("subComment")
	public ModelAndView subComment(@RequestParam("content") String content,
			@RequestParam("ID") String ID,
			@RequestParam("essayPid") String essayPid){
		ModelAndView mav = new ModelAndView();
		Comment comment = new Comment(1, Integer.valueOf(essayPid), Integer.valueOf(ID), content);
		om.insertComment(comment);
		mav.setViewName("redirect:/ed-content?pid="+essayPid);
		return mav;
	}
	
	/**显示主界面,显示方法
	 * @param user 用户的Session
	 * @param page 用于分页的page对象
	 * @return 显示：ed-index.jsp
	 */
	@RequestMapping("ed-index") 
	public ModelAndView getQuestions(@SessionAttribute("user") User user,
			Page page){
		ModelAndView mav = new ModelAndView();
		PageHelper.offsetPage(page.getStart(), 5);
		String[] typeName = {"小说","随笔","散文","作文","日记","知识"};
		List<Essay> essays = null;
		essays = edm.getEssayByType(typeName[page.getType()]);
		int total = (int) new PageInfo<>(essays).getTotal();
		page.caculateLast(total);
		
		List<Essay> essaysTop6 = edm.getEssaysTop6();
		List<Essay> essaysTop62 = edm.getEssaysTop62();
		essaysTop6 = essaysTop6.subList(0, 6);
		essaysTop62 = essaysTop62.subList(0, 6);
		
		if(user != null){
			System.out.println(user.toString());
			List<Essay> essaysFan = edm.getByFanID(user.getID());
			mav.addObject("essaysFan", essaysFan);
		}else{
			mav.addObject("essaysFan", null);
		}

		mav.addObject("essays", essays);
		mav.addObject("essaysTop62", essaysTop62);
		mav.addObject("essaysTop6", essaysTop6);
		mav.addObject("total", total/5);
		mav.setViewName("ed-index");
		return mav;
	}
	
	/**显示个人用户的界面,显示方法
	 * @param user 用户的Session
	 * @param page 用于分页的page对象
	 * @return 显示：ed-person.jsp
	 */
	@RequestMapping("ed-person")
	public ModelAndView displayPerson(@SessionAttribute(value="user") User user,
			Page page){
		ModelAndView mav = new ModelAndView();
		PageHelper.offsetPage(page.getStart(), 5);
		List essays = edm.getEssayByID(user.getID());
		System.out.println(essays.size());
		int total = (int) new PageInfo<>(essays).getTotal();
		page.caculateLast(total);
		mav.addObject("total", total/5);
		mav.addObject("essays", essays);
		mav.setViewName("ed-person");
		return mav;
	}
	/**显示另一个用户的界面,显示方法
	 * @param user 用户的Session，以及另一个用户的id
	 * @return 显示：ed-userUI.jsp
	 */
	@RequestMapping("ed-userUI")
	public ModelAndView displayUserUI(@SessionAttribute("user") User user,
			@RequestParam("ID") int ID){
		ModelAndView mav = new ModelAndView();
		User person = um.getByID(ID);
		List<UserRel> res = um.getByFanID(user.getID());
		UserRel re = null;
		for(int i = 0; i < res.size(); i++){
			if(res.get(i).getID() == ID){
				re =res.get(i);
				break;
			}
		}
		List<Essay> essays = edm.getEssayByID(ID);
		mav.addObject("re", re);
		mav.addObject("person", person);
		mav.addObject("essays", essays);
		mav.setViewName("ed-userUI");
		return mav;
	}	
	
	/**关注一名用户,重定位方法
	 * @param user 用户的Session，以及另一个用户的id
	 * @return 重定位： {@link #displayUserUI(User, int)} 
	 */
	@RequestMapping("concern")
	public ModelAndView concern(@SessionAttribute("user") User user,
			@RequestParam("ID") int ID){
		ModelAndView mav = new ModelAndView();
		UserRel r = new UserRel(ID, user.getID());
		um.addUserRel(r);
		mav.setViewName("redirect:/ed-userUI?ID="+String.valueOf(ID));
		return mav;
	}
	/**取消关注一名用户,重定位方法
	 * @param user 用户的Session，以及另一个用户的id
	 * @return 重定位： {@link #displayUserUI(User, int)}
	 */
	@RequestMapping("unconcern")
	public ModelAndView unconcern(@SessionAttribute("user") User user,
			@RequestParam("ID") int ID){
		ModelAndView mav = new ModelAndView();
		UserRel r = new UserRel(ID, user.getID());
		um.deleteUserRel(r);
		mav.setViewName("redirect:/ed-userUI?ID="+String.valueOf(ID));
		return mav;
	}
	/** 显示草稿界面,重定位方法
	 * @param user 用户的Session，以及另一个用户的id
	 * @param pid 草稿的pid
	 * @return 重定位：{@link #displayUserUI(User, int)} 
	 */
	@RequestMapping("ed-upload")
	public ModelAndView displayUpload(@SessionAttribute(value="user") User user,@RequestParam(value="pid",defaultValue="0") int pid){
		ModelAndView mav = new ModelAndView();
		List drafts = edm.getDraftByID(user.getID());
		Draft draft = null;
		for(int i = 0; i < drafts.size(); i++){
			Draft d = (Draft) drafts.get(i);
			if(d.getPid() == pid){
				draft = d;
			}
		}
		mav.addObject("drafts", drafts);
		mav.addObject("draft", draft);
		mav.setViewName("ed-upload");
		return mav;
	}
	/** 显示消息列表界面,显示方法
	 * @param user 用户的Session
	 * @return 显示：ed-news.jsp
	 */
	@RequestMapping("ed-news")
	public ModelAndView displayNews(@SessionAttribute(value="user") User user){
		ModelAndView mav = new ModelAndView();
		List<User> people = om.getNewsList(user.getID());
		mav.addObject("people", people);
		mav.setViewName("ed-news");
		return mav;
	}
	/** 显示消息对话框界面,显示方法
	 * @param user 用户的Session
	 * @param id 另一个用户的id
	 * @return 显示：ed-Frame.jsp
	 */
	@RequestMapping("ed-newsFrame")
	public ModelAndView displayNewsFrame(@SessionAttribute(value="user") User user,@RequestParam(value="id") int id){
		ModelAndView mav = new ModelAndView();
		List<NewsCon> meNewsCons = om.getUsersNews(user.getID(), id);
		List<NewsCon> newsCons = om.getUsersNews(id, user.getID());
		NewsCon2 newsConAll = null;
		try {
			newsConAll = new NewsCon2(meNewsCons, newsCons);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(newsConAll.getSize() == 0){
			newsConAll = null;
		}
		mav.addObject("news", newsConAll);
		mav.addObject("userb", id);
		mav.setViewName("ed-newsFrame");
		return mav;
	}
	/** 给另一个用户发送消息,重定位方法
	 * @param user 用户的Session
	 * @param userb 另一个用户的id
	 * @param text 发送的文本
	 * @return 重定位：{@link #displayNewsFrame(User, int)}
	 */
	@PostMapping("subNews")
	public ModelAndView subNews(@SessionAttribute(value="user") User user,
			@RequestParam("userb") int userb, 
			@RequestParam("text") String text){
		ModelAndView mav = new ModelAndView();
		om.addNews(user.getID(), userb, text);
		mav.setViewName("redirect:/ed-newsFrame?id="+String.valueOf(userb));
		return mav;
	}
	
	/** 提交用户头像图片,重定位方法
	 * @param user 用户的Session
	 * @param request 请求对象
	 * @param file 上传的文件
	 * @param model 模型
	 * @return 重定位：{@link #displayNewsFrame(User, int)}
	 */
	@PostMapping("/subHeadPic")
	public String subHeadPic(@SessionAttribute(value="user") User user,
			HttpServletRequest request,
			@RequestParam("file") MultipartFile file,
			Model model) throws Exception{
		if(!file.isEmpty()){
			String path = request.getServletContext().getRealPath("/headImage");
			String name = file.getOriginalFilename();
			String filename = String.valueOf(user.getID())+"."+name.substring(name.length()-3, name.length());
			String filehead = "http://localhost:8080/MyWeb/headImage/";
			file.transferTo(new File(path + File.separator + filename));
			user.setPathOfHead(filehead+filename);
			um.updateUser(user);
			model.addAttribute("user", user);
			System.out.println("上传至路径："+filehead+filename);
			return "ed-person";
		}
		return "ed-index";
	}
	/** 提交用户背景图片,重定位方法
	 * @param user 用户的Session
	 * @param request 请求对象
	 * @param file 上传的文件
	 * @param model 模型
	 * @return 重定位：{@link #displayNewsFrame(User, int)}
	 */
	@PostMapping("/subBgPic")
	public String subBgPic(@SessionAttribute(value="user") User user,
			HttpServletRequest request,
			@RequestParam("file") MultipartFile file,
			Model model) throws Exception{
		if(!file.isEmpty()){
			String path = request.getServletContext().getRealPath("/headImage");
			String name = file.getOriginalFilename();
			String filename = String.valueOf(user.getID())+"bg."+name.substring(name.length()-3, name.length());
			String filehead = "http://localhost:8080/MyWeb/headImage/";
			file.transferTo(new File(path + File.separator + filename));
			user.setPathOfBg(filehead+filename);
			um.updateUser(user);
			model.addAttribute("user", user);
			System.out.println("上传至路径："+filehead+filename);
			return "ed-person";
		}
		return "ed-index";
	}
	/** 用户将草稿提交为文章,重定位方法
	 * @param pid 提交草稿的pid
	 * @param user 用户Session
	 * @param theme 用户上传的草稿题目
	 * @param comCon 用户上传的草稿内容
	 * @param type 用户上传的草稿类型
	 * @return 重定位：{@link #displayContent(int)}
	 */
	@PostMapping("submitDraft")
	public String submitDraft(@RequestParam("pid") int pid,
			@SessionAttribute(value="user") User user,
			@RequestParam("theme") String theme,
			@RequestParam("comCon") String comCon,
			@RequestParam("type") String type){
		Draft draft = new Draft(pid, user.getID(), theme, comCon);
		Essay essay = new Essay(draft, type);
		edm.addEssay(essay);
		edm.deleteDraft(pid);
		return "redirect:/ed-index";
	}
	/** 增添或修改用户草稿,重定位方法
	 * @param pid 草稿的pid
	 * @param user 用户Session
	 * @param theme 草稿题目
	 * @param comCon 草稿内容
	 * @return 重定位：{@link #displayUpload(User, int)}
	 */
	@PostMapping("saveDraft")
	public String saveDraft(@RequestParam("pid") int pid,
			@SessionAttribute(value="user") User user,
			@RequestParam("theme") String theme,
			@RequestParam("comCon") String comCon){
		Draft draft = new Draft(pid, user.getID(), theme, comCon);
		//System.out.println(draft.toString());
		if(draft.getPid() == 0){
			edm.addDraft(draft);
		}else{
			edm.updateDraft(draft);
		}
		return "redirect:/ed-upload";
	}
	/** 用户删除草稿,重定位方法
	 * @param pid 草稿的pid
	 * @param user 用户Session
	 * @param theme 草稿题目
	 * @param comCon 草稿内容
	 * @return 重定位：{@link #displayUpload(User, int)}
	 */
	@PostMapping("deleteDraft")
	public String deleteDraft(@RequestParam("pid") int pid,
			@SessionAttribute(value="user") User user,
			@RequestParam("theme") String theme,
			@RequestParam("comCon") String comCon){
		Draft draft = new Draft(pid, user.getID(), theme, comCon);
		System.out.println(draft.toString());
		edm.deleteDraft(pid);
		return "redirect:/ed-upload";
	}
	/** 管理员删除文章,重定位方法
	 * @param pid 文章的pid
	 * @param user 用户Session
	 * @param id 删除文章所属用户id
	 * @param comCon 草稿内容
	 * @return 重定位：{@link #displayContent(int)}
	 */
	@RequestMapping("vipDelete")
	public String vipDelete(@RequestParam("pid") int pid,
			@RequestParam("id") int id,
			@SessionAttribute(value="user") User user){
		if(user.getID() == 1){
			edm.deleteEssay(pid);
		}
		return "redirect:/ed-index";
	}
	/** 注销登录,重定位方法
	 * @param session 会话
	 * @return 重定位：login.jsp
	 */
	@RequestMapping("loginout")
	public String loginout(HttpSession session){
		session.invalidate();
		return "redirect:/login.jsp";
	}
	
	
}
