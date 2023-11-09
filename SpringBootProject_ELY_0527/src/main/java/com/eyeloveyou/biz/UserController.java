package com.eyeloveyou.biz;

import java.util.HashMap;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.eyeloveyou.biz.user.UserEyeDAO;
import com.eyeloveyou.biz.user.UserEyeVO;
import com.eyeloveyou.biz.user.UserProDAO;
import com.eyeloveyou.biz.user.UserProVO;

import jakarta.servlet.http.HttpSession;

@RestController
public class UserController {

	@Autowired
    private UserProDAO userProDAO;
	@Autowired
	private UserEyeDAO userEyeDAO;
	@Autowired
    private PasswordEncoder passwordEncoder;
	// private final PasswordEncoder encoder;

	/*
	 * @Autowired public UserController(PasswordEncoder encoder) { this.encoder =
	 * encoder; }
	 */	
    @GetMapping("/login")
    public String showLoginForm() {
    	return "login";
    }
    @PostMapping("/login")
    public String login(String id, String pw, Model model, HttpSession session) {
    	UserProVO vo = new UserProVO();
    	vo.setUserId(id);
    	vo.setPassword(pw);
    	if (passwordEncoder.matches(vo.getPassword(), userProDAO.getUserPassword(vo))) {
    		UserProVO user = userProDAO.getUserData(vo);
    		session.setAttribute("userValue", user);
    		model.addAttribute("userValue", user);
    		return "index";
    	}
    	else {
    		return "redirect:/login?error";
    	}
    }
    @GetMapping("/signup")
    public String showSignForm() {
    	return "signup";
    }
    @PostMapping("/signup")
    public String signup(String id, String pw, String na, String em, String ha) {
    	if (id == null || id.isEmpty()) {
    		return "redirect:/signup?error";
    	}
    	else if (pw == null || pw.isEmpty()) {
    		return "redirect:/signup?error";
    	}
    	else if (na == null || na.isEmpty()) {
    		return "redirect:/signup?error";
    	}
    	else if (em == null || em.isEmpty()) {
    		return "redirect:/signup?error";
    	}
    	else if (ha == null || ha.isEmpty()) {
    		return "redirect:/signup?error";
    	}
    	else {
    		String encPW = passwordEncoder.encode(pw);
    		UserProVO vo = new UserProVO();
    		vo.setUserId(id);
    		vo.setPassword(encPW);
    		vo.setName(na);
    		vo.setEmail(em);
    		vo.setHomeAddress(ha);
    		userProDAO.signUserData(vo);
    		return "login";
    	}
    }
    @PostMapping("/signup/check")
    @ResponseBody
    public String idCheck(@RequestParam String userId) {
    	UserProVO vo = new UserProVO();
    	vo.setUserId(userId);
    	int isDuplicate = userProDAO.idCheck(vo);
    	if (isDuplicate > 0) {
    		return "duplicate";
    	} else {
            return "available";
        }
    }

	/*
	 * @PostMapping("/login")
	 * 
	 * @CrossOrigin(origins = "*") public UserProVO login(@RequestBody UserProVO
	 * requestData){ if (encoder.matches(requestData.getPassword(),
	 * userProDAO.getUserPassword(requestData))) { UserProVO user =
	 * userProDAO.getUserData(requestData); return user; } else { return null; } }
	 */	
	/*
	 * @PostMapping("/signup")
	 * 
	 * @CrossOrigin(origins = "*") public String signup(@RequestBody UserProVO
	 * requestData) { if (requestData.getUserId() == null ||
	 * requestData.getUserId().isEmpty()) { return "아이디 없음"; } else if
	 * (requestData.getPassword() == null || requestData.getPassword().isEmpty()) {
	 * return "비밀번호 없음"; } else if (requestData.getName() == null ||
	 * requestData.getName().isEmpty()) { return "이름 없음"; } else if
	 * (requestData.getEmail() == null || requestData.getEmail().isEmpty()) { return
	 * "이메일 없음"; } else if (requestData.getHomeAddress() == null ||
	 * requestData.getHomeAddress().isEmpty()) { return "주소 없음"; } else { String
	 * rawPW = requestData.getPassword(); String encPW = encoder.encode(rawPW);
	 * requestData.setPassword(encPW); userProDAO.signUserData(requestData); return
	 * "성공"; } }
	 * 
	 * @PostMapping("/signup/check")
	 * 
	 * @CrossOrigin(origins = "*") public boolean idCheck(@RequestBody UserProVO
	 * requestData) { int isDuplicate = userProDAO.idCheck(requestData); if
	 * (isDuplicate > 0) { return true; } else { return false; } }
	 */	
	@PostMapping("/save")
	@CrossOrigin(origins = "*")
	public String insertEyeData(@RequestBody UserEyeVO requestData) {
		int isCheck = userEyeDAO.dataCheck(requestData);
		if (isCheck >= 1) {
			userEyeDAO.updateUserData(requestData);
			return "update";
		}
		else {
			userEyeDAO.setUserEyeData(requestData);
			return "insert";
		}
	}
	
	@PostMapping("/info")
	@CrossOrigin(origins = "*")
    public JSONObject qwasd(@RequestBody UserProVO requestData) {
        UserEyeVO user = new UserEyeVO();
        user.setUserId(requestData.getUserId());
        HashMap<String, Object> myHashMap = new HashMap<>();
        user = userEyeDAO.getUserEyeAllData(user);
        myHashMap.put("userId", user.getUserId()); // 아이디
        // 차트1
        myHashMap.put("userTot", user.getTotalOperatingTime()); // 총 동작 시간
        myHashMap.put("timeAvg", userEyeDAO.getAllUserTimeAvg()); // 전체 평균 동작 시간
        // 차트2
        myHashMap.put("userBTpM", userEyeDAO.getUserBTpM(user)); // 개인 분당 눈 깜박임 횟수
        myHashMap.put("allBTpM", userEyeDAO.getAllBTpM()); // 전체 평균 분당 눈 깜박임 횟수
        // 차트3
        myHashMap.put("userWCpM", userEyeDAO.getUserWCpM(user)); // 개인 분당 경고음 출력 횟수
        myHashMap.put("allWCpM", userEyeDAO.getAllWCpM()); // 전체 평균 분당 경고음 출력 횟수
        // 차트4
        myHashMap.put("userBc", user.getBlinkCycle()); // 개인 눈 깜박임 주기
        myHashMap.put("cycleAvg", userEyeDAO.getAllUserCycleAvg()); // 전체 평균 눈 깜박임 주기
        // 차트5
        myHashMap.put("blinkRatio", userEyeDAO.getBlinkRatio(user)); // 개인 눈 깜박임 비율
        myHashMap.put("warningRatio", userEyeDAO.getWarningRatio(user)); // 개인 경고음 출력 비율
        // 순위
        myHashMap.put("count", userEyeDAO.getDataCount()); // 회원수
        myHashMap.put("userRank", userEyeDAO.getUserRank(user)); // 개인 순위
        
        JSONObject obj = new JSONObject(myHashMap);
        return obj;
    }
}
