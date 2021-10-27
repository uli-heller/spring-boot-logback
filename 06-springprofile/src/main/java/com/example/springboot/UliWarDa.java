package com.example.springboot;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UliWarDa {
	String uli;
	String cleartext;
	String encrypted;
	
	public UliWarDa(String uli, String cleartext, String encrypted) {
	  log.info("Constructor('{}','{}','{}')", uli, cleartext, encrypted);
	  this.uli = uli;
	  this.cleartext = cleartext;
	  this.encrypted = encrypted;
	}
}
