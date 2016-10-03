package datastructures;

/**
 * Message that is sent to a user when they join a chatroom so that the
 * background is displayed with the correct colour they have chosen
 */

public class ColourMessage implements Message {
	
	private String colour;

	/* set colour in constructor */
	public ColourMessage(String colour) {
		this.colour = colour;
	}

	/* setters and getters */
	public void setColour(String c) {
		colour = c;
	}

	public String getColour() {
		return colour;
	}

}
