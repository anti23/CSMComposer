package datastructure;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Project implements Serializable {
	private static final long serialVersionUID = -1926334169841725594L;
	Map<String, Animation> animations = new HashMap<String, Animation>();
	
	
	public void addAnimation(String fileName)
	{
		Animation a = new Animation(fileName);
		animations.put(fileName, a);
	}
	
	public void addAnimation(String fileName,Animation a)
	{
		if (a != null)
			animations.put(fileName, a);
		else
			System.out.println("Project: addAnimation: trying to add NULL");
		
	}
	
	public void removeAnimation(String fileName)
	{
		animations.remove(fileName);
	}
	
	public Animation getAnimation(String fileName)
	{
		return animations.get(fileName);
	}
	
	public Map<String, Animation> getAnimations()
	{
		return animations;
	}
	
}
