package dev.fileeditor.oauth2.state;

import java.util.HashMap;

/**
 * The default {@link StateController} implementation.
 */
public class DefaultStateController implements StateController {

	private final static String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	private final HashMap<String, String> states = new HashMap<>();

	@Override
	public String generateNewState(String redirectUri) {
		String state = randomState();
		states.put(state, redirectUri);
		return state;
	}

	@Override
	public String consumeState(String state) {
		return states.remove(state);
	}

	private static String randomState() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 10; i++) {
			builder.append(CHARACTERS.charAt((int) (Math.random() * CHARACTERS.length())));
		}
		return builder.toString();
	}
}
