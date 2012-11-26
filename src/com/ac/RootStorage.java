package com.ac;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.os.Environment;

public class RootStorage {

	Context context;
	AC ac;
	public RootStorage(Context context, AC ac){
		this.context = context;
		this.ac = ac;
	}

	
	public File path;
	public String[] list(){
		if (path==null){
			path = Environment.getRootDirectory();
		}
		
		File[] all=path.listFiles();
		
		if (all==null){
			return new String[]{};
		}
		
		ArrayList<String> dirs=new ArrayList<String>();
		ArrayList<String> files=new ArrayList<String>();
		
		for(int i=0;i<all.length;i++){
			if (all[i].isDirectory())
				dirs.add(all[i].getName());
			else
				files.add(all[i].getName());
		}

		ArrayList<String> list=new ArrayList<String>();
		list.addAll(dirs);
		list.addAll(files);

		return list.toArray(new String[list.size()]);
	}
	
	public String[] size(String[] files){
		String[] size=new String[files.length];
		for(int i=0;i<files.length;i++){
			File inf = new File( path.getAbsolutePath()+path.separator+files[i]);
			
			if (inf.isDirectory())
			{
				size[i] = "";
				continue;
			}
			
			long len=inf.length();
			
			if (len<1024)
			{
				size[i]=len+" B ";
			} else if (len<1024*1024){
				size[i]=(len/1024)+" KB";
				
			} else if (len<1024*1024*1024){
				size[i]=(len/(1024*1024))+" MB";
			} else {
				size[i]=(len/(1024*1024*1024))+" GB";
			}
		}
		
		return size;
	}

	public boolean chdir(String fileName){
		
		//:)
		if (fileName.equalsIgnoreCase("..") && path.getParentFile()!=null)
			path = path.getParentFile();
		else{
			File tmp=new File( path.getAbsolutePath()+path.separator+fileName);
			
			if (tmp.isDirectory()){
				path = tmp;
			} else {
				return false;
			}
		}
		return true;
		}
}
