package com.aggfi.digest.server.botty.digestbotty.utils;

import java.util.ArrayList;

import com.google.wave.api.Blip;
import com.google.wave.api.BlipContentRefs;

public class StringBuilderAnnotater{
	public static final String STYLE_FONT_WEIGHT = "style/fontWeight";
	public static final String STYLE_FONT_STYLE = "style/fontStyle";
	public static final String LINK_WAVE = "link/wave";
	
	public static final String WEIGHT_BOLD = "bold";
	public static final String STYLE_ITALIC = "italic";
	
	private StringBuilder sb;
	private ArrayList<Integer> annStarts;
	private ArrayList<Integer> annEnds;
	private ArrayList<String> annVal;
	private ArrayList<String> annType;
	private Blip blip;
	int prevBlipLength;
	
	public StringBuilderAnnotater(Blip blip){
		sb = new StringBuilder();
		annStarts = new ArrayList<Integer>();
		annEnds = new ArrayList<Integer>();
		annVal = new ArrayList<String>();
		annType = new ArrayList<String>();
		this.blip = blip;
		prevBlipLength = blip.length();
	}
	
	public void append(String str, String annTypeStr, String annValStr){
		int curLength = sb.length();
		int strLength = str.length();
		annStarts.add(curLength);
		annEnds.add(curLength + strLength);
		annVal.add(annValStr);
		annType.add(annTypeStr);
		sb.append(str);
	}
	
	public Blip flush2Blip(){
		blip.append(sb.toString());
		int length = annStarts.size();
		for(int i = 0; i < length; i++){
			int annStart = annStarts.get(i);
			int annEnd = annEnds.get(i);
			if(annVal.get(i) != null && annType.get(i) != null){
				BlipContentRefs.range(blip, prevBlipLength + annStart, prevBlipLength + annEnd).annotate( annType.get(i), annVal.get(i) );
			}
		}
		return blip;
	}
}
