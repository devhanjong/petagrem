package com.example.toyproject.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class YoutubeVideoInfo {
	@Id
	private long id;
	private String description;
	private String thumbnailUrl;
	private String title; 		//게시글 생성자
	private String videoId; 	//게시판 생성일자
	private String publishedDate;
	private String keyword;
	
}