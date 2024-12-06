public class HuffmanNode implements Comparable<HuffmanNode> {
	private char character;
	private int frequency;
	private HuffmanNode left;
	private HuffmanNode right;

	public HuffmanNode(char character, int frequency) {
		this.character = character;
		this.frequency = frequency;
	}

	public HuffmanNode(HuffmanNode left, HuffmanNode right) {
		this.frequency = left.frequency + right.frequency;
		this.left = left;
		this.right = right;
	}

	@Override
	public int compareTo(HuffmanNode other) {
		return Integer.compare(frequency, other.frequency);
	}

	public char getCharacter() {
		return character;
	}

	public int getFrequency() {
		return frequency;
	}

	public HuffmanNode getLeft() {
		return left;
	}

	public HuffmanNode getRight() {
		return right;
	}
}