package com.example.toyproject.controller;

import com.example.toyproject.exception.BadRequestException;
import com.example.toyproject.model.Board;
import com.example.toyproject.model.Comment;
import com.example.toyproject.model.Member;
import com.example.toyproject.repository.BoardRepository;
import com.example.toyproject.repository.CommentRepository;
import com.example.toyproject.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequestMapping("/blog") // 하나의 주소로 만들어주는거 /board랑 /write합쳐서 => /board/write로 주소가 지정된다
@Controller
public class BlogController {
	@Autowired
	BoardRepository boardRepository;
	@Autowired
	MemberRepository MR;
	@Autowired
	HttpSession session; 
	@Autowired
	CommentRepository CR;
	
	
	@Autowired
	HttpServletRequest HSR;
	
	@GetMapping("/board{id}")
	public String boardDetail(Model model, @PathVariable("id") long id) throws Exception{
		//비밀글이고 , 손님이나 , 다른유저이면
		if(hiddencheck(Long.toString(id)).get("result") == 1){
			if(Integer.parseInt(authcheck(Long.toString(id)).get("result")) == 0 ||
					Integer.parseInt(authcheck(Long.toString(id)).get("result")) == 3){
				throw new BadRequestException("Bad Request!!!");
			}
		}
		List<Board> prenext = boardRepository.findPreNext(id, id);
		Board preboard = null;
		Board nextboard = null;

		if(prenext.size() ==2){
			//이전글 찾기 , 다음글 찾기
			preboard = prenext.get(0);
			nextboard = prenext.get(1);
		}
		else if(prenext.size() == 1 && prenext.get(0).getBbsId() > id ){
			nextboard = prenext.get(0);
		}
		else if(prenext.size() ==1 && prenext.get(0).getBbsId() < id){
			preboard = prenext.get(0);
		}

//		System.out.println(preboard);
//		System.out.println(nextboard);



		// jpa로 해당 아이디 게시물을 조회해야
		Optional<Board> opt = boardRepository.findById(id); // 옵셔널클래스컬렉션타입 데이터타입은 보드
		Board board = opt.get();
		model.addAttribute("board", board);
		model.addAttribute("preboard", preboard);
		model.addAttribute("nextboard", nextboard);
		return "board/blog-details";
	}

	@RequestMapping(value = "hiddencheck" , method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Integer> hiddencheck(String id){
		Map<String,Integer> json = new HashMap<>();
		long tmp = Long.parseLong(id, 10);
		if(boardRepository.findById(tmp).get().getHidden() == 1){
			//비밀글이라면
			json.put("result" , 1);
			return json;
		}else {
			//공개글
			json.put("result", 0);
			return json;
		}
	}

	@PostMapping("/authcheck")
	@ResponseBody
	public Map<String, String> authcheck(String id){
		long tmp = Long.parseLong(id, 10);
		Map<String,String> json = new HashMap<>();
		String pwd = boardRepository.findById(tmp).get().getPassword();
		json.put("pwd", pwd);
		if( HSR.getAttribute("userid") == null){
			//guest
			json.put("result", "0");
			return json;
		}
		else if(HSR.getAttribute("authority") != null){
			//admin
			json.put("result", "1");
			return json;
		}
		else if(HSR.getAttribute("userid") == boardRepository.findById(tmp).get().getMember().getUid()){
			//세션아이디랑 게시글 아이디가 같으면
			json.put("result", "2");
			return json;
		}else{
			//로그인된 다른유저
			json.put("result", "3");
			return json;
		}

	};

	
	
	


	@GetMapping("") 
	public String board(Model model, @RequestParam(name = "page", defaultValue = "1") int page) {
		int startPage = (page - 1) / 10 * 10 + 1;
		int endPage = startPage + 9;
		
		PageRequest req = PageRequest.of(page - 1, 9, Sort.by(Direction.DESC, "bbsId")); // 0페이지부터
		// 주소창에 http://localhost:8080/board/?page=10 확인

		Page<Board> result = boardRepository.findAll(req);
		int totalPage = result.getTotalPages();
		if (endPage > totalPage) {
			endPage = totalPage;
		}


		List<Board> list = result.getContent();
		model.addAttribute("list", list);
		model.addAttribute("startPage", startPage);
		model.addAttribute("endPage", endPage);
		model.addAttribute("totalPage", totalPage);
		model.addAttribute("page", page);
		System.out.print(startPage+"@@"+endPage+"@@"+totalPage+"@@"+page);
		return "board/blog";
	}

		@GetMapping("/write") // board패키지 만들어주고 "/board/wrtie
	public String boardWrite() {
		return "/board/blog-write";
	}
	
	@RequestMapping(value="/write", method = RequestMethod.POST)
	@ResponseBody
	//@ResponseBody가 없을시에는 모든 자료형으로 리턴이 가능
	public String boardWritePost(@ModelAttribute Board board) {
		String result = "1";

		System.out.println(board);

		/* 로그인 여부 확인 (세션의 값 확인) */
		if (null == session.getAttribute("userid")) { // 로그인 X
			result = "0";
			return result;
		} else { // 로그인 O
			String id = session.getAttribute("userid").toString();
			Member member = MR.findById(id).get();
			board.setMember(member); 
			boardRepository.save(board);
		}
		return result;
		
		
		
		
	}

	
	@GetMapping("/update/{id}")
	public String boardUpdate(Model model, @PathVariable("id") long id) {
			Optional<Board> data = boardRepository.findById(id);// jpa로 해당아이디게시물을 조회
		Board board = data.get();
		model.addAttribute("board", board);
		return "board/update"; 
	}
	
	@PostMapping("/update/{id}")  
	public String boardUpdatePost(HttpServletRequest HSR, @ModelAttribute Board board, @PathVariable("id") long id) {
//		 member = HSR.getSession().getServletContext();
		/* 로그인 여부 확인 (세션의 값 확인) */
		Member member = MR.findById(session.getAttribute("userid").toString()).get();
		if (member == null) { // 로그인 X
			return "alert/writeAfterSign";
		} else { // 로그인 O	
			
		board.setMember(member); 
		board.setBbsId(id);
		boardRepository.save(board); 
		}
		return "redirect:/board/" + id;
	}
	
	@GetMapping("/delete/{id}")
	public String boardDelete(@PathVariable("id") long id) {
		// jpa로 해당 아이디 게시물을 조회해야
		Board deleteboard = boardRepository.findById(id).get();
		List<Comment> comment = CR.findByBoard(deleteboard);
		for (Comment comment2 : comment) {
			comment2.setBoard(null);
		}
		deleteboard.setComments(null);
		deleteboard.setMember(null);
		boardRepository.deleteById(id);
		return "redirect:/board/";
	}
	
	
	
	@GetMapping("/home")
	public String home() { 
		
		return "index";
	}
	
	
	

	
}





	


//JSON형태로 변환된 데이터를 ajax로 파싱