package com.ys.sbbs.controller;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/file")
public class FileController {
	@Value("${spring.servlet.multipart.location}") private String uploadDir;
	
	@GetMapping("/profile/{filename}")
	public ResponseEntity<Resource> profile(@PathVariable String filename) {
		Path path = Paths.get(uploadDir + "profile/" + filename);
		try {
			String contentType = Files.probeContentType(path);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentDisposition(
					ContentDisposition.builder("attachment")
						.filename(filename, StandardCharsets.UTF_8)
						.build()
			);
			headers.add(HttpHeaders.CONTENT_TYPE, contentType);
			Resource resource = new InputStreamResource(Files.newInputStream(path));
			return new ResponseEntity<Resource>(resource, headers, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@GetMapping("/download/{filename}")
	public ResponseEntity<Resource> download(@PathVariable String filename) {
		Path path = Paths.get(uploadDir + "upload/" + filename);
		try {
			String contentType = Files.probeContentType(path);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentDisposition(
					ContentDisposition.builder("attachment")
					.filename(filename, StandardCharsets.UTF_8)
					.build()
					);
			headers.add(HttpHeaders.CONTENT_TYPE, contentType);
			Resource resource = new InputStreamResource(Files.newInputStream(path));
			return new ResponseEntity<Resource>(resource, headers, HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
