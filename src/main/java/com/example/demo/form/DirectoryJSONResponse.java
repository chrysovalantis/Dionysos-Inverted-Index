package com.example.demo.form;

import com.example.demo.model.Collection;

/** The JSON representation of a collection.
 *
 * @author Chrysovalantis Christodoulou
 */
public class DirectoryJSONResponse {

	private Boolean status;
	private Collection col;
	
	public DirectoryJSONResponse(Boolean status, Collection col) {
		super();
		this.status = status;
		this.col = col;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public Collection getCol() {
		return col;
	}

	public void setCol(Collection col) {
		this.col = col;
	}
	
	
	
	
	
}
