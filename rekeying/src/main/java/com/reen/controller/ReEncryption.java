package com.reen.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;

import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.ElementPowPreProcessing;
import it.unisa.dia.gas.jpbc.PairingPreProcessing;
import nics.crypto.Tuple;
import nics.crypto.proxy.afgh.AFGHGlobalParameters;
import nics.crypto.proxy.afgh.AFGHProxyReEncryption;


@RestController
@RequestMapping("/reencryption/")
public class ReEncryption {
	//Only for PoC!
    private static Map<String, Element> keyMap = new HashMap<String, Element>();//Only for PoC!
	private static int rBits = 256; // 160; // 20 bytes   //Only for PoC!
	private static int qBits = 1536; // 512; // 64 bytes  //Only for PoC!
	private static AFGHGlobalParameters global = new AFGHGlobalParameters(rBits, qBits); //Only for PoC!
	private static Tuple Text =null; //Only for PoC!
	//Only for PoC!
    
	@RequestMapping("/genkeys")
	public String genkeys(@RequestParam("user") String user) {

		Element e_sk = AFGHProxyReEncryption.generateSecretKey(global);
		Element e_pk = AFGHProxyReEncryption.generatePublicKey(e_sk, global);
		
		String sk = e_sk.toString();
		String pk = e_pk.toString();
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("sk", sk);
		map.put("pk", pk);
		
		if(user.equals("seller")) {
			keyMap.put("sk_a", e_sk);
			keyMap.put("pk_a", e_pk);
		}else if(user.equals("buyer")) {
			keyMap.put("sk_b", e_sk);
			keyMap.put("pk_b", e_pk);
		}
	        
	    String mapJson = JSON.toJSONString(map);
	    return mapJson;
	}
	
	
	@RequestMapping("/sendText")
	public boolean sendText(@RequestParam("text") String text) {
		boolean result = false;
		Element sk_b = keyMap.get("sk_b");
		Element sk_b_inverse = sk_b.invert();
		
		Element pk_a= keyMap.get("pk_a");
		ElementPowPreProcessing pk_a_ppp = pk_a.pow();
		
		// Re-Encryption Key
		Element pk_b = keyMap.get("pk_b");
		Element sk_a = keyMap.get("sk_a");
		Element rk_a_b = AFGHProxyReEncryption.generateReEncryptionKey(pk_b, sk_a);
		
		String message = text;
		Element m = AFGHProxyReEncryption.stringToElement(message, global.getG2());
		Tuple c_a = AFGHProxyReEncryption.secondLevelEncryption(m, pk_a_ppp, global);
		Text =c_a;
		
		PairingPreProcessing e_ppp = global.getE().pairing(rk_a_b);
		
		Tuple c_b = AFGHProxyReEncryption.reEncryption(c_a, rk_a_b, e_ppp);
		
		Element m2 = AFGHProxyReEncryption.firstLevelDecryptionPreProcessing(c_b, sk_b_inverse, global);
		
		if(message.equals(new String(m2.toBytes()).trim())) {
			result = true;
		}
		
		System.out.println("result="+result);
		return result;
	}
	
	
	@RequestMapping("/getText")
	public String getText(@RequestParam("number") String number) {
		
		Element sk_b = keyMap.get("sk_b");
		Element sk_b_inverse = sk_b.invert();
		
		Element pk_a= keyMap.get("pk_a");
		ElementPowPreProcessing pk_a_ppp = pk_a.pow();
		
		// Re-Encryption Key
		Element pk_b = keyMap.get("pk_b");
		Element sk_a = keyMap.get("sk_a");
		Element rk_a_b = AFGHProxyReEncryption.generateReEncryptionKey(pk_b, sk_a);
		
		
		PairingPreProcessing e_ppp = global.getE().pairing(rk_a_b);
		
		Tuple c_b = AFGHProxyReEncryption.reEncryption(Text, rk_a_b, e_ppp);
		
		Element m2 = AFGHProxyReEncryption.firstLevelDecryptionPreProcessing(c_b, sk_b_inverse, global);
		
		 String result = new String(m2.toBytes()).trim();
		
		System.out.println("reulst ="+result);
		return result;
	}
}

