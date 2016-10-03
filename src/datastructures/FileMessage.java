package datastructures;

/**
 * subclass of the abstract superclass ChatMessage which represents all
 * FileMessages (images, videos, pdf, doc) sent and received by users
 */

public class FileMessage extends ChatMessage {

	private String filename;

	public FileMessage() {
		super();	// call to superclass constructor
	}

	/* setter and getter */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFilename() {
		return filename;
	}
}
