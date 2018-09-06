package com.aizhixin.cloud.dd;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class TestFile {
	@Test
	public void tt() throws HttpException, IOException {
	
		org.apache.commons.httpclient.HttpClient hc=new org.apache.commons.httpclient.HttpClient();
		PostMethod hp=new PostMethod("http://127.0.0.1:8002/api/v1/test/postFile");
//		hp.setHeader("Authorization", "Bearer d0a831a0-2c4a-4fb5-8ef6-c9b85b3ca9ef");
		File f=new File("I:\\ajax\\test.html");
		try {
			  FilePart fp = new FilePart("file", f);
			  HttpMethodParams fmp=new HttpMethodParams();
			 MultipartRequestEntity mEntityBuilder =new  MultipartRequestEntity(new Part[]{fp}, fmp);
			hp.setRequestEntity(mEntityBuilder);
//			hp.setRequestHeader("Authorization", "Bearer d0a831a0-2c4a-4fb5-8ef6-c9b85b3ca9ef");
			hc.executeMethod(hp);
//	        hp.getResponseBodyAsString();
			
//			MultipartPostMethod m=new MultipartPostMethod(uri)
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		}
		

		
	}
}
