/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 

var textHeight = 10;
var padding = 10;
var namespace = "http://www.w3.org/2000/svg";

var displayAttributes = true;
var displayMethods = true;
var displayNotes = true;

function Type(name){
	this.width = 160;
	this.style = "type";
	
	this.insert = function(evt){
		y = 0;
		y = this.insertOutline(evt,y);
		y = this.insertName(evt,y);
		if(displayAttributes==true){
			y = this.insertAttributes(evt,y);
		}
		if(displayMethods==true){
			y = this.insertMethods(evt,y);
		}
		if(displayNotes==true){
			y = this.insertNotes(evt,y);
		}
	}
	
	this.height = function(){
		height = 0;
		height += this.heightName();
		if(displayAttributes==true){
			height += this.heightAttributes();
		}
		if(displayMethods==true){
			height += this.heightMethods();
		}
		return height;
	}
	
	
	
	
	
	
	this.insertOutline = function(evt,y){

		style = evt.target.getAttributeNS(null, "class");
		evt.target.setAttributeNS(null,"class",style+" "+this.style);

		e = document.createElementNS(namespace, "rect");
		e.setAttributeNS(null, "x", 0);
		e.setAttributeNS(null, "y", 0);
		e.setAttributeNS(null, "width",  this.width);
		e.setAttributeNS(null, "height", this.height());
        	e.setAttributeNS(null, "class", "outline");
        	evt.target.appendChild(e);

		return y;
	}
	
	
	
	
	
	
	
	this.name = name;
	
	this.heightName = function(){
		return padding * 2 + textHeight;
	}
	
	this.insertName = function(evt,y){
	
		y += padding;
		y += textHeight;
		
		e = document.createElementNS(namespace, "text");
		e.setAttributeNS(null, "x", this.width/2);
		e.setAttributeNS(null, "y", y);
		e.setAttributeNS(null, "class", "title");
		e.appendChild(document.createTextNode(this.name));
		evt.target.appendChild(e);
        
		y += padding;
	
		return y;
	}
	
	
	
	
	
	
	
	this.attributeList = new Array();
	this.attributeCount = 0;
	
	this.addAttribute = function(text){
		this.attributeList[this.attributeCount++]=text;
	}
	
	this.heightAttributes = function(){
		if(this.attributeCount>0){
			return padding * 2 + this.attributeCount*textHeight;
		}
		else{
			return padding;
		}
	}
	
	this.insertAttributes = function(evt,y){
	
		e = document.createElementNS(namespace, "line");
		e.setAttributeNS(null, "x1", 0);
		e.setAttributeNS(null, "y1", y);
		e.setAttributeNS(null, "x2", this.width);
		e.setAttributeNS(null, "y2", y);
		e.setAttributeNS(null, "class", "divider");
		evt.target.appendChild(e);
		
		y += padding;
		
		for(i=0;i<this.attributeCount;++i){
			y += textHeight;
			
			e = document.createElementNS(namespace, "text");
			e.setAttributeNS(null, "x", padding);
			e.setAttributeNS(null, "y", y);
			e.setAttributeNS(null, "class", "attribute");
			e.appendChild(document.createTextNode(this.attributeList[i]));
			evt.target.appendChild(e);
		}
		
		if(this.attributeCount>0){
			y += padding;
		}
	
		return y;
	}
	
	
	
	
	
	this.methodList = new Array();
	this.methodCount = 0;
	
	this.addMethod = function(text){
		this.methodList[this.methodCount++]=text;
	}
	
	this.heightMethods = function(){
		if(this.methodCount>0){
			return padding * 2 + this.methodCount*textHeight;
		}
		else{
			return padding;
		}
	}
	
	this.insertMethods = function(evt,y){
	
		e = document.createElementNS(namespace, "line");
		e.setAttributeNS(null, "x1", 0);
		e.setAttributeNS(null, "y1", y);
		e.setAttributeNS(null, "x2", this.width);
		e.setAttributeNS(null, "y2", y);
		e.setAttributeNS(null, "class", "divider");
		evt.target.appendChild(e);
		
		y += padding;
		
		for(i=0;i<this.methodCount;++i){
			y += textHeight;
			
			e = document.createElementNS(namespace, "text");
			e.setAttributeNS(null, "x", padding);
			e.setAttributeNS(null, "y", y);
			e.setAttributeNS(null, "class", "method");
			e.appendChild(document.createTextNode(this.methodList[i]));
			evt.target.appendChild(e);
		}
		
		if(this.methodCount>0){
			y += padding;
		}
	
		return y;
	}
	
	
	
	
	
	this.noteList = new Array();
	this.noteCount = 0;
	
	this.addNote = function(text){
		this.noteList[this.noteCount++]=text;
	}
	
	this.insertNotes = function(evt,y){
		if(this.noteCount>0){
			joinTop = y;
		
			y += padding;
			
			e = document.createElementNS(namespace, "line");
			e.setAttributeNS(null, "x1", this.width/2-padding*2);
			e.setAttributeNS(null, "y1", y);
			e.setAttributeNS(null, "x2", this.width/2+padding*2);
			e.setAttributeNS(null, "y2", joinTop);
			e.setAttributeNS(null, "class", "note connect");
			evt.target.appendChild(e);
			
			
			
			height = this.heightNotes();
			
			e = document.createElementNS(namespace, "polygon");
			e.setAttributeNS(null, "points", "0,"+(y+padding)+" 0,"+(y+height)+" "+this.width+","+(y+height)+" "+this.width+","+y+" "+padding+","+y);
			e.setAttributeNS(null, "class", "note");
			evt.target.appendChild(e);
			
			e = document.createElementNS(namespace, "polygon");
			e.setAttributeNS(null, "points", ""+padding+","+y+" 0,"+(y+padding)+" "+padding+","+(y+padding));
			e.setAttributeNS(null, "class", "note corner");
			evt.target.appendChild(e);
			
			y += padding;
			
		
			for(i=0;i<this.noteCount;++i){
				y += textHeight;

				e = document.createElementNS(namespace, "text");
				e.setAttributeNS(null, "x", padding);
				e.setAttributeNS(null, "y", y);
				e.setAttributeNS(null, "class", "note");
				e.appendChild(document.createTextNode(this.noteList[i]));
				evt.target.appendChild(e);
			}
		}
	}
	
	this.heightNotes = function(){
		if(this.noteCount>0){
			return padding*2 + this.noteCount*textHeight;
		}
	}
}

function Interface(name){
	this.superclass = Type;
	this.superclass(name);
	delete this.superclass; 
	this.rounded = true;
	
	this.insertOutline = function(evt,y){

		style = evt.target.getAttributeNS(null, "class");
		evt.target.setAttributeNS(null,"class",style+" "+this.style);

		e = document.createElementNS(namespace, "rect");
		e.setAttributeNS(null, "x", 0);
		e.setAttributeNS(null, "y", 0);
		e.setAttributeNS(null, "width",  this.width);
		e.setAttributeNS(null, "height", this.height());
		e.setAttributeNS(null, "rx", padding*1.5);
		e.setAttributeNS(null, "ry", padding*1.5);
        	e.setAttributeNS(null, "class", "outline");
		evt.target.appendChild(e);

		return y;
	}
	
	this.heightAttributes = function(){
		return 0;
	}
	
	this.insertAttributes = function(evt,y){
		return y;
	}
	
}
Interface.prototype = new Type;

function Class(name){
	this.superclass = Type;
	this.superclass(name);
	delete this.superclass; 
	this.rounded = false;
}
Class.prototype = new Type;




function PackageSymbol(name,width,height){
	this.name = name;
	this.nameWidth = 150;
	this.width = width;
	this.height = height;
	
	this.insert = function(evt){
		
		style = evt.target.getAttributeNS(null, "class");
		evt.target.setAttributeNS(null,"class",style+" package");
		
		first = evt.target.getFirstChild();
		
		e = document.createElementNS(namespace, "rect");
		e.setAttributeNS(null, "x", 0);
		e.setAttributeNS(null, "y", 0);
		e.setAttributeNS(null, "width", this.nameWidth);
		e.setAttributeNS(null, "height", textHeight+padding);
		e.setAttributeNS(null, "class", "outline");
		e.appendChild(document.createTextNode(this.name));
		evt.target.insertBefore(e,first);
		
		e = document.createElementNS(namespace, "text");
		e.setAttributeNS(null, "x", this.nameWidth/2);
		e.setAttributeNS(null, "y", textHeight+padding/2);
		e.setAttributeNS(null, "class", "title");
		e.appendChild(document.createTextNode(this.name));
		evt.target.insertBefore(e,first);
		
		e = document.createElementNS(namespace, "rect");
		e.setAttributeNS(null, "x", 0);
		e.setAttributeNS(null, "y", textHeight+padding);
		e.setAttributeNS(null, "width", this.width);
		e.setAttributeNS(null, "height", this.height-textHeight-padding);
		e.setAttributeNS(null, "class", "outline");
		e.appendChild(document.createTextNode(this.name));
		evt.target.insertBefore(e,first);
		
		e = document.createElementNS(namespace, "rect");
		e.setAttributeNS(null, "x", padding);
		e.setAttributeNS(null, "y", textHeight+padding*2);
		e.setAttributeNS(null, "width", this.width-padding*2);
		e.setAttributeNS(null, "height", this.height-textHeight-padding*3);
		e.setAttributeNS(null, "class", "inner");
		e.appendChild(document.createTextNode(this.name));
		evt.target.insertBefore(e,first);
		
		
	}
}

