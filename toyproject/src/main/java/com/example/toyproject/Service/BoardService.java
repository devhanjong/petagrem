package com.example.toyproject.Service;

import com.example.toyproject.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Service
public class BoardService {

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    HttpSession httpSession;

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

    public Map<String, String> authcheck(String id){
        long tmp = Long.parseLong(id, 10);
        Map<String,String> json = new HashMap<>();
        String pwd = boardRepository.findById(tmp).get().getPassword();

        json.put("pwd", pwd);
        if( httpSession.getAttribute("userid") == null){
            //guest
            json.put("result", "0");
            return json;
        }
        else if(httpSession.getAttribute("authority") != null){
            //admin
            json.put("result", "1");
            return json;
        }
        else if(httpSession.getAttribute("userid").equals(boardRepository.findById(tmp).get().getMember().getUid())){
            //세션아이디랑 게시글 아이디가 같으면
            json.put("result", "2");
            return json;
        }else{
            //로그인된 다른유저
            json.put("result", "3");
            return json;
        }

    };

}
