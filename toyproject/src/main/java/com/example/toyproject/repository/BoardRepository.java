package com.example.toyproject.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.toyproject.model.Board;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
	
	@Query(nativeQuery = true, value = "SELECT b.*" +
            "  FROM board b" +
            " WHERE b.bbs_id IN (" +
            "  (SELECT b2.bbs_id FROM board b2 WHERE b2.bbs_id < ?1 && hidden = 0 ORDER BY b2.bbs_id DESC LIMIT 1)," +
            "  (SELECT b3.bbs_id FROM board b3 WHERE b3.bbs_id > ?2 && hidden = 0 ORDER BY b3.bbs_id LIMIT 1)" +
            " )")
    List<Board> findPreNext(long id, long id2);
}
