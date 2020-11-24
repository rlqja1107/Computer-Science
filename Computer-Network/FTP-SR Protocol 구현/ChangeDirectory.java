

import java.io.*;

public class ChangeDirectory {
	public String getabsolutePath(String second) {
		if(second.startsWith("..")) {
			File file=new File(ServerMain.curPath);
			String parentPath=file.getParent();
			if(second.length()>=2)
				return parentPath+second.substring(2);
			else return parentPath;
		}
		else if(second.startsWith(".")) {
			if(second.length()>1) {
				String path=ServerMain.curPath+second.substring(1);
				return path;
			}
			else 
				return ServerMain.curPath;	
		}
		else if(second.startsWith("\\"))
			return ServerMain.curPath+second;		
		else if(second.equals(" ")) 
			return ServerMain.curPath;

		else if(!second.contains("\\")) 
			return ServerMain.curPath+"\\"+second;	

		else 
			return second;

	}
	public boolean changePath(String path) {		
		File file=new File(path);
		if(this.isDir(file)) {
			ServerMain.curPath=path;
			return true;
		}
		else return false; 
	}
	public String listFile(File file) {
		File []list=file.listFiles();
		String response="";
		for(int i=0;i<list.length;i++) {
			if(list[i].isDirectory()) {
				response+=list[i].getName()+",-";
			}
			else {
				response+=list[i].getName()+","+list[i].length();
			}
			if(i!=list.length-1) {
				response+=",";
			}
		}
		return response;
	}
	public boolean isDir(File file) {
		return file.isDirectory()&&file.exists();
	}
}
