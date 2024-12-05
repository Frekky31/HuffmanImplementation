public class App {
    public static void main(String[] args) throws Exception {
        Huffman task = new Huffman();
        System.out.println(task.decodeFromFile("Task/output-mada.dat", "Task/dec_tab-mada.txt"));

        System.out.println();

        Huffman huffman = new Huffman();
        String message = "this is a test message for huffman encoding and decoding";
        huffman.encodeToFile(message, "output.dat", "dec_tab.txt");
        System.out.println(huffman.decodeFromFile("output.dat", "dec_tab.txt"));
    }
}
