package gdufs.agency.controller;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSONObject;
import gdufs.agency.dao.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.auth0.jwt.interfaces.Claim;
import com.fasterxml.jackson.databind.ObjectMapper;

import gdufs.agency.entity.User;
import gdufs.agency.service.UserService;
import gdufs.agency.util.MD5Util;
import gdufs.agency.util.Token;

import gdufs.agency.util.AesCbcUtil;
import gdufs.agency.util.HttpUtil;

@Controller
@Scope(value = "prototype")
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserMapper userMapper;
	@RequestMapping("add")
	@ResponseBody
	public Object add(String code,String encryptedData,String iv){
		Map map = new HashMap();
		//登录凭证不能为空
		if (code == null || code.length() == 0) {
			map.put("status", 0);
			map.put("msg", "code 不能为空");
			return map;
		}
		//小程序唯一标识   (在微信小程序管理后台获取)
		String wxspAppid = "wx7e34c5a1a92ed85d";  //"wxd5313e0bf7f485d4";
		//小程序的 app secret (在微信小程序管理后台获取)
		String wxspSecret = "aec5fa7d679d117833e4216ffd6aba05";  //"df0aacd3b50c1e2fc7d8e5cc56029f8a";
		//授权（必填）
		String grant_type = "authorization_code";


		//////////////// 1、向微信服务器 使用登录凭证 code 获取 session_key 和 openId ////////////////
		//请求参数
		String params = "appid=" + wxspAppid + "&secret=" + wxspSecret + "&js_code=" + code + "&grant_type=" + grant_type;
		//发送请求
		String sr = HttpUtil.get("https://api.weixin.qq.com/sns/jscode2session?"+params);
		//解析相应内容（转换成json对象）
		JSONObject json = JSONObject.parseObject(sr);
		//获取会话密钥（session_key）
		String session_key = json.get("session_key").toString();
		//用户的唯一标识（openId）
		String openId = (String) json.get("openId");

		//////////////// 2、对encryptedData加密数据进行AES解密 ////////////////
		try {
			String result = AesCbcUtil.getUserInfo(encryptedData, session_key, iv);
			if (null != result && result.length() > 0) {
				map.put("status", 1);
				map.put("msg", "解密成功");

				JSONObject userInfoJSON = JSONObject.parseObject(result);
				Map userInfo = new HashMap();
				userInfo.put("openId", userInfoJSON.get("openId"));
				userInfo.put("nickName", userInfoJSON.get("nickName"));
				userInfo.put("gender", userInfoJSON.get("gender"));
				userInfo.put("city", userInfoJSON.get("city"));
				userInfo.put("province", userInfoJSON.get("province"));
				userInfo.put("country", userInfoJSON.get("country"));
				userInfo.put("avatarUrl", userInfoJSON.get("avatarUrl"));
				userInfo.put("unionId", userInfoJSON.get("unionId"));
				if (userMapper.sel(userInfo.get("openId").toString())==null) {
					userMapper.insertSelective(userInfo);
				}
				map.put("userInfo", userInfo);
				return map;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		map.put("status", 0);
		map.put("msg", "解密失败");
		return map;
	}
	@RequestMapping("getList")
	@ResponseBody
	public Object getList(){
		return userMapper.selectByPrimaryKey();
	}
	@RequestMapping("del")
	@ResponseBody
	public Object del(String openId){
		return userMapper.deleteByPrimaryKey(openId);
	}
	@RequestMapping("userInfo")
	@ResponseBody
	public Object userInfo(String openId){
		return userMapper.sel(openId);
	}

	@Resource
	private UserService userService;


//根据用户ID返回用户信息
	@RequestMapping(value = "/getUser", produces = "text/html;charset=UTF-8")
	public @ResponseBody String getUser(HttpServletRequest request, HttpServletResponse response) throws Exception {

		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/json");
		String openId = request.getParameter("openId");
		Map<String, String> map = new HashMap<String, String>();
		ObjectMapper mapper = new ObjectMapper();
		String resultString = "";

		try {
			User user = userService.getUser(openId);

			map.put("nickName", user.getNickName());
			map.put("gender", user.getGender());
			map.put("city", user.getCity());
			System.out.println(user.getCity());
			map.put("country", user.getCountry());
			System.out.println(user.getCountry());
			map.put("avatarUrl", user.getAvatarUrl());
			map.put("unionId", user.getUnionId());
			map.put("province", user.getProvince());

			map.put("phoneNum", user.getPhonenum());
			map.put("academy", user.getAcademy());
			System.out.println(user.getAcademy());
			map.put("address", user.getAddress());
			System.out.println(user.getAddress());
            map.put("balance", user.getBalance().toString());

			resultString = mapper.writeValueAsString(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultString;
	}

//判断session是否过期sessionId
	@RequestMapping("/checkSession")
	public void checkSession(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String cookie = request.getHeader("Cookie");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		Token c = new Token();

		try {
			Map<String, Claim> claims = c.verifyToken(cookie);
			String openId = claims.get("user_id").asString();

			out.print(1);

		} catch (Exception e) {
			out.print(0);
		}
	}

//更新用户信息
	@RequestMapping("/updateUser")
	public void updateUser(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		String openId = request.getParameter("openId");
		String phoneNum = request.getParameter("phoneNum");
		String academy = request.getParameter("academy");
		String address = request.getParameter("address");

		System.out.println("openId: " + openId + "\n"+ "\n" + "phoneNum: " + phoneNum + "\n"
				+ "academy: " + academy + "\n" + "address: " + address + "\n");
		try {
			User user=new User();
			user.setOpenId(openId);
			user.setPhonenum(phoneNum);
			user.setAcademy(academy);
			user.setAddress(address);
			
			boolean result=userService.updateUser(user);
			if(result)
				out.print(1);
			else
				out.print(0);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

		//更新用户余额
		@RequestMapping("/updateBalance")
		public void updateBalance(HttpServletRequest request, HttpServletResponse response) throws Exception {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			
			try {
				String openId = request.getParameter("openId");
				Double balance = Double.parseDouble(request.getParameter("balance"));
				System.out.println("openId: " + openId + "\n"+"balance: " + balance + "\n" );
				User user=new User();
				user.setOpenId(openId);
				user.setBalance(balance);
				
				boolean result=userService.updateUser(user);
				if(result)
					out.print(1);
				else
					out.print(0);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
}
