package com.example.springboot;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UliWarDa {
	String uli;
	
	public UliWarDa(String uli) {
	  log.info("Constructor('{}')", uli);
	  this.uli = uli;
	}
}
