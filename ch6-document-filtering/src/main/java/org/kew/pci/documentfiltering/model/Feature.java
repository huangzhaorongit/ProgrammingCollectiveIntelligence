package org.kew.pci.documentfiltering.model;

public class Feature implements Comparable<Feature> {

	public String content;
	
	public Feature(){
		
	}
	
	public Feature(String content){
		this.content = content;
	}
	
	public String getContent(){
		return content;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Feature other = (Feature) obj;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		return true;
	}

	@Override
	public int compareTo(Feature f) {
		return content.compareTo(f.getContent());
	}

	@Override
	public String toString() {
		return "Feature [content=" + content + "]";
	}
	
}