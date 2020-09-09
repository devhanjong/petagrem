package com.example.toyproject.repository;


import com.example.toyproject.model.YoutubeVideoInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface YoutubeVideoInfoRepository extends JpaRepository<YoutubeVideoInfo, Integer> {
	
}