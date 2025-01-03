import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class Huffman {
	private HuffmanNode head;
	private Map<Character, String> encHuffmanCodeMap = new HashMap<>();
	private Map<String, Character> decHuffmanCodeMap = new HashMap<>();

	public static int[] calculateFrequency(String message) {
		int[] frequencyTable = new int[128];
		for (int i = 0; i < message.length(); i++) {
			int character = message.charAt(i);
			if (character >= 0 && character < 128) {
				frequencyTable[character]++;
			}
		}
		return frequencyTable;
	}

	public static HuffmanNode buildHuffmanTree(int[] frequencyTable) {
		// Use of priority queue which "sorts" the nodes based on frequency,
		// starting from the lowest
		PriorityQueue<HuffmanNode> pq = new PriorityQueue<>();
		for (int i = 0; i < frequencyTable.length; i++) {
			if (frequencyTable[i] > 0) {
				pq.add(new HuffmanNode((char) i, frequencyTable[i]));
			}
		}

		while (pq.size() > 1) {
			HuffmanNode left = pq.poll();
			HuffmanNode right = pq.poll();
			pq.add(new HuffmanNode(left, right));
		}

		return pq.poll();
	}

	public void printHuffmanTree() {
		printHuffmanTree(head, "");
	}

	// For debbuging, prints the tree with frequencies and codes
	public void printHuffmanTree(HuffmanNode node, String code) {
		if (node == null)
			return;

		if (node.getLeft() == null && node.getRight() == null) {
			System.out.println(node.getCharacter() + " " + node.getFrequency() + " " + code);
			return;
		}

		printHuffmanTree(node.getLeft(), code + "0");
		printHuffmanTree(node.getRight(), code + "1");
	}

	public String encode(String message) {
		int[] frequencyTable = calculateFrequency(message);
		head = buildHuffmanTree(frequencyTable);
		generateMap(head, "");

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < message.length(); i++) {
			sb.append(encHuffmanCodeMap.get(message.charAt(i)));
		}
		return sb.toString();
	}

	// Recursively traverse the huffman tree to generate the codes
	public void generateMap(HuffmanNode node, String code) {
		if (node == null)
			return;

		// Stores the codes in maps for better access
		if (node.getLeft() == null && node.getRight() == null) {
			encHuffmanCodeMap.put(node.getCharacter(), code);
			decHuffmanCodeMap.put(code, node.getCharacter());
			return;
		}

		generateMap(node.getLeft(), code + "0");
		generateMap(node.getRight(), code + "1");
	}

	public String decode(String encodedMessage) {
		// Go through all bits, if a code is found in the map, append the character
		StringBuilder sb = new StringBuilder();
		StringBuilder temp = new StringBuilder();
		for (int i = 0; i < encodedMessage.length(); i++) {
			temp.append(encodedMessage.charAt(i));
			if (decHuffmanCodeMap.containsKey(temp.toString())) {
				sb.append(decHuffmanCodeMap.get(temp.toString()));
				temp = new StringBuilder();
			}
		}
		return sb.toString();
	}

	public void writeDecTable(String fileName) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 128; i++) {
			if (encHuffmanCodeMap.containsKey((char) i)) {
				sb.append(i).append(":").append(encHuffmanCodeMap.get((char) i)).append("-");
			}
		}
		try {
			Files.write(Paths.get(fileName), sb.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void readDecTable(String fileName) {
		try {
			String decTable = Files.readString(Paths.get(fileName));
			String[] entries = decTable.split("-");
			for (String entry : entries) {
				String[] parts = entry.split(":");
				decHuffmanCodeMap.put(parts[1], (char) Integer.parseInt(parts[0]));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void encodeToFile(String message, String byteFileName, String decTabFileName) {
		String encoded = encode(message);

		int padding = 8 - (encoded.length() % 8);
		for (int i = 0; i < padding; i++) {
			encoded += (i == 0 ? "1" : "0");
		}

		byte[] byteArray = new byte[encoded.length() / 8];
		for (int i = 0; i < encoded.length(); i += 8) {
			byteArray[i / 8] = (byte) Integer.parseInt(encoded.substring(i, i + 8), 2);
		}

		writeDecTable(decTabFileName);

		try {
			var fos = new FileOutputStream(byteFileName);
			fos.write(byteArray);
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String decodeFromFile(String bytefileName, String decTabFileName) {
		File file = new File(bytefileName);
		byte[] bFile = new byte[(int) file.length()];
		try {
			FileInputStream fis = new FileInputStream(file);
			fis.read(bFile);
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		readDecTable(decTabFileName);

		// Byte Array to Bit String
		StringBuilder bitString = new StringBuilder();
		for (byte b : bFile) {
			bitString.append(String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0'));
		}

		// Remove padding
		int lastIndex = bitString.lastIndexOf("1");
		return decode(bitString.substring(0, lastIndex));
	}
}
